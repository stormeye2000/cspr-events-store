package com.stormeye.event.store.service.storage.impl.block;

import com.casper.sdk.identifier.block.HeightBlockIdentifier;
import com.casper.sdk.model.deploy.Delegator;
import com.casper.sdk.model.deploy.SeigniorageAllocation;
import com.casper.sdk.model.deploy.Validator;
import com.casper.sdk.model.era.ValidatorWeight;
import com.casper.sdk.model.event.blockadded.BlockAdded;
import com.casper.sdk.model.key.PublicKey;
import com.casper.sdk.service.CasperService;
import com.fasterxml.jackson.core.TreeNode;
import com.stormeye.event.exception.EventServiceException;
import com.stormeye.event.exception.StoreConsumerException;
import com.stormeye.event.repository.BlockRepository;
import com.stormeye.event.service.storage.domain.*;
import com.stormeye.event.store.service.storage.EventInfo;
import com.stormeye.event.store.service.storage.StorageFactory;
import com.stormeye.event.store.service.storage.StorageService;
import com.stormeye.event.store.service.storage.impl.common.TransactionalRunner;
import com.stormeye.event.store.service.storage.impl.era.EraService;
import com.stormeye.event.store.service.storage.impl.era.EraValidatorService;
import com.stormeye.event.store.service.storage.impl.reward.RewardStorage;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Component;

import java.math.BigInteger;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.security.NoSuchAlgorithmException;

import static com.stormeye.event.store.service.storage.impl.VersionUtils.isVersionGreaterOrEqual;

/**
 * @author ian@meywood.com
 */
@Component
class BlockAddedService implements StorageService<Block> {

    public static final String V1_2_0 = "1.2.0";
    private final Logger logger = LoggerFactory.getLogger(BlockAddedService.class);
    private final BlockRepository blockRepository;
    private final EraService eraService;
    private final EraValidatorService eraValidatorService;

    private final RewardStorage rewardStorage;
    private final TransactionalRunner transactionalRunner;

    BlockAddedService(final BlockRepository blockRepository,
                      final EraService eraService,
                      final EraValidatorService eraValidatorService,
                      final RewardStorage rewardStorage,
                      final StorageFactory storageFactory,
                      final TransactionalRunner transactionalRunner) {
        this.blockRepository = blockRepository;
        this.eraService = eraService;
        this.eraValidatorService = eraValidatorService;
        this.rewardStorage = rewardStorage;
        this.transactionalRunner = transactionalRunner;
        storageFactory.register(BlockAdded.class, this);
    }

    @Override
    public Block store(final EventInfo eventInfo) {

        final BlockAdded toStore = (BlockAdded) eventInfo.getData();

        try {
            return transactionalRunner.runInTransaction(() -> storeBlock(eventInfo));
        } catch (Exception e) {
            if (isDuplicateEventException(e)) {
                return blockRepository.findByBlockHashAndEventId(toStore.getBlockHash(), eventInfo.getId());
            } else {
                throw StoreConsumerException.getRuntimeException(e);
            }
        }
    }

    private boolean isDuplicateEventException(Exception e) {
        return e instanceof DataIntegrityViolationException && e.getMessage().contains("UKIDXE_EVENT_ID_BLOCK_HASH_INDEX");
    }

    @NotNull
    private Block storeBlock(final EventInfo eventInfo) throws NoSuchAlgorithmException {

        final BlockAdded toStore = (BlockAdded) eventInfo.getData();

        final Block block;

        block = this.blockRepository.save(
                new Block(toStore.getBlockHash(),
                        toStore.getBlock().getHeader().getParentHash(),
                        toStore.getBlock().getHeader().getTimeStamp(),
                        toStore.getBlock().getHeader().getStateRootHash(),
                        toStore.getBlock().getBody().getDeployHashes().size(),
                        toStore.getBlock().getBody().getTransferHashes().size(),
                        toStore.getBlock().getHeader().getEraId(),
                        toStore.getBlock().getBody().getProposer(),
                        toStore.getBlock().getHeader().getHeight(),
                        eventInfo.getId()
                )
        );


        if (isEraEnded(toStore)) {
            var era = new Era(
                    toStore.getBlock().getHeader().getEraId(),
                    toStore.getBlock().getHeader().getHeight(),
                    toStore.getBlock().getHeader().getTimeStamp(),
                    toStore.getBlock().getHeader().getProtocolVersion()
            );
            eraService.store(era);

            // TODO get host from configuration
            CasperService casperSdkService = getCasperSdkService("http://65.21.235.219:7777");

            try {
                // What to do if unable to contact node. we need a queue here
                var eraInfo = casperSdkService.getEraInfoBySwitchBlock(new HeightBlockIdentifier(1184341L /*toStore.getBlock().getHeader().getHeight()*/));

                if (eraInfo != null
                        && eraInfo.getEraSummary() != null
                        && eraInfo.getEraSummary().getStoredValue() != null
                        && eraInfo.getEraSummary().getStoredValue().getValue() != null) {

                    var allocations = eraInfo.getEraSummary().getStoredValue().getValue().getSeigniorageAllocations();

                    if (allocations != null) {
                        for (var allocation : allocations) {

                            Reward reward = buildReward(block, allocation);
                            if (reward != null) {
                                rewardStorage.store(reward);
                            }
                        }
                    }
                }
            } catch (Exception e) {
                logger.error("Error getEraInfoBySwitchBlock for height {}", toStore.getBlock().getHeader().getHeight(), e);
            }

            if (isVersionGreaterOrEqual(eventInfo.getVersion(), V1_2_0)) {
                createEraValidator(toStore);
            } else {
                createEraValidatorFromJson(eventInfo, toStore);
            }
        }

        return block;
    }

    private void createEraValidator(BlockAdded toStore) {
        for (ValidatorWeight validator : toStore.getBlock().getHeader().getEraEnd().getNextEraValidatorWeights()) {
            this.eraValidatorService.create(
                    toStore.getBlock().getHeader().getEraId() + 1,
                    validator.getValidator(),
                    validator.getWeight(),
                    0,
                    0,
                    0
            );
        }
    }

    private void createEraValidatorFromJson(final EventInfo eventInfo, final BlockAdded toStore) throws NoSuchAlgorithmException {

        TreeNode nextEraValidatorWeights = eventInfo.getJsonData().at("/block/header/era_end/next_era_validator_weights");

        if (!nextEraValidatorWeights.isMissingNode() && nextEraValidatorWeights.isArray()) {

            for (int i = 0; i < nextEraValidatorWeights.size(); i++) {
                TreeNode validatorNode = nextEraValidatorWeights.get(i);
                this.eraValidatorService.create(
                        toStore.getBlock().getHeader().getEraId() + 1,
                        PublicKey.fromTaggedHexString(validatorNode.get("validator").toString()),
                        new BigInteger(validatorNode.get("weight").toString()),
                        0,
                        0,
                        0);
            }
        }
    }

    private Reward buildReward(Block block, SeigniorageAllocation toStore) {
        final Reward reward;

        if (toStore instanceof Validator) {
            // TODO Store validation reward
            reward = new ValidatorReward();
        } else if (toStore instanceof Delegator) {
            // TODO Store delegator reward
            reward = new DelegatorReward();
        } else {
            reward = null;
        }
        return reward;
    }

    private boolean isEraEnded(final BlockAdded blockAdded) {
        return blockAdded.getBlock().getHeader().getEraEnd() != null;
    }

    private static CasperService getCasperSdkService(final String source) {
        try {
            var uri = new URI(source);
            return CasperService.usingPeer(uri.getHost(), uri.getPort());
        } catch (URISyntaxException | MalformedURLException e) {
            throw new EventServiceException(e);
        }
    }


}
