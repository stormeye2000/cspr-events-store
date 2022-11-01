package com.stormeye.event.store.service.storage.impl.block;

import com.casper.sdk.identifier.block.HeightBlockIdentifier;
import com.casper.sdk.model.deploy.Delegator;
import com.casper.sdk.model.deploy.SeigniorageAllocation;
import com.casper.sdk.model.deploy.Validator;
import com.casper.sdk.model.event.blockadded.BlockAdded;
import com.casper.sdk.service.CasperService;
import com.stormeye.event.exception.EventServiceException;
import com.stormeye.event.exception.StoreConsumerException;
import com.stormeye.event.repository.BlockRepository;
import com.stormeye.event.service.event.EventInfo;
import com.stormeye.event.service.storage.domain.*;
import com.stormeye.event.store.service.storage.StorageFactory;
import com.stormeye.event.store.service.storage.StorageService;
import com.stormeye.event.store.service.storage.impl.common.TransactionalRunner;
import com.stormeye.event.store.service.storage.impl.era.EraService;
import com.stormeye.event.store.service.storage.impl.reward.RewardStorage;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Component;

import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;

/**
 * @author ian@meywood.com
 */
@Component
class BlockAddedService implements StorageService<Block> {

    private final Logger logger = LoggerFactory.getLogger(BlockAddedService.class);
    private final BlockRepository blockRepository;
    private final EraService eraService;
    private final RewardStorage rewardStorage;
    private final TransactionalRunner transactionalRunner;

    BlockAddedService(final BlockRepository blockRepository,
                      final EraService eraService,
                      final RewardStorage rewardStorage,
                      final StorageFactory storageFactory,
                      final TransactionalRunner transactionalRunner) {
        this.blockRepository = blockRepository;
        this.eraService = eraService;
        this.rewardStorage = rewardStorage;
        this.transactionalRunner = transactionalRunner;
        storageFactory.register(BlockAdded.class, this);
    }

    @Override
    public Block store(final EventInfo eventInfo) {

        final BlockAdded toStore = (BlockAdded) eventInfo.getData();

        try {
            return transactionalRunner.runInTransaction(() -> storeBlock(eventInfo.getId(), toStore));
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


    private Block storeBlock(final Long eventId, final BlockAdded toStore) {
        //  final BlockAdded toStore = (BlockAdded) eventInfo.getData();

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
                        eventId
                )
        );


        var eraEnd = toStore.getBlock().getHeader().getEraEnd();
        if (eraEnd != null) {
            var era = new Era(
                    toStore.getBlock().getHeader().getEraId(),
                    toStore.getBlock().getHeader().getHeight(),
                    toStore.getBlock().getHeader().getTimeStamp(),
                    toStore.getBlock().getHeader().getProtocolVersion()
            );
            eraService.store(era);
        }

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

        /*
        const eraSummary = await this.casperClient.getEraInfoBySwitchBlockHeight(event.block.header.height);

            for (const reward of eraSummary.stored_value.EraInfo.seigniorage_allocations) {
                if (reward.Validator) {
                    this.storeEntity('ValidatorReward', {
                        eraId: eraSummary.era_id,
                        publicKey: reward.Validator.validator_public_key,
                        amount: reward.Validator.amount,
                        timestamp: event.block.header.timestamp,
                    });
                }
                else if (reward.Delegator) {
                    this.storeEntity('DelegatorReward', {
                        eraId: eraSummary.era_id,
                        publicKey: reward.Delegator.delegator_public_key,
                        validatorPublicKey: reward.Delegator.validator_public_key,
                        amount: reward.Delegator.amount,
                        timestamp: event.block.header.timestamp,
                    });
                }
            }

            if (this.isVersionGreaterOrEqual(apiVersion.version, '1.2.0')) {
                for (let validator of event.block.header.era_end.next_era_validator_weights) {
                    this.storeEntity('EraValidator', {
                        eraId: event.block.header.era_id + 1,
                        publicKeyHex: validator.validator,
                        weight: validator.weight,
                        rewards: 0,
                        hasEquivocation: 0,
                        wasActive: 0,
                    });
                }
            }
         */

        return block;
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

    private static CasperService getCasperSdkService(final String source) {
        try {
            var uri = new URI(source);
            return CasperService.usingPeer(uri.getHost(), uri.getPort());
        } catch (URISyntaxException | MalformedURLException e) {
            throw new EventServiceException(e);
        }
    }
}
