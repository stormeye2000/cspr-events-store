package com.stormeye.event.store.service.storage.impl.block;

import com.casper.sdk.model.era.ValidatorWeight;
import com.casper.sdk.model.event.blockadded.BlockAdded;
import com.casper.sdk.model.key.PublicKey;
import com.fasterxml.jackson.core.TreeNode;
import com.stormeye.event.common.TransactionalRunner;
import com.stormeye.event.exception.StoreConsumerException;
import com.stormeye.event.repository.BlockRepository;
import com.stormeye.event.service.storage.domain.Block;
import com.stormeye.event.store.service.storage.EventInfo;
import com.stormeye.event.store.service.storage.StorageFactory;
import com.stormeye.event.store.service.storage.StorageService;
import com.stormeye.event.store.service.storage.impl.VersionUtils;
import com.stormeye.event.store.service.storage.impl.era.EraService;
import com.stormeye.event.store.service.storage.impl.era.EraValidatorService;
import com.stormeye.event.store.service.storage.impl.reward.RewardService;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Component;

import java.math.BigInteger;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static com.stormeye.event.store.service.storage.impl.VersionUtils.isVersionGreaterOrEqual;

/**
 * The service that stores blocks when a block is added. If the block's era has ended also stores the era info and
 * rewards.
 *
 * @author ian@meywood.com
 */
@Component
class BlockAddedService implements StorageService<Block> {

    public static final String V1_2_0 = "1.2.0";
    private final Logger logger = LoggerFactory.getLogger(BlockAddedService.class);
    private final BlockRepository blockRepository;
    private final EraService eraService;
    private final EraValidatorService eraValidatorService;
    private final RewardService rewardService;
    private final TransactionalRunner transactionalRunner;

    BlockAddedService(final BlockRepository blockRepository,
                      final EraService eraService,
                      final EraValidatorService eraValidatorService,
                      final RewardService rewardService,
                      final StorageFactory storageFactory,
                      final TransactionalRunner transactionalRunner) {
        this.blockRepository = blockRepository;
        this.eraService = eraService;
        this.eraValidatorService = eraValidatorService;
        this.rewardService = rewardService;
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
        return e instanceof DataIntegrityViolationException && e.getMessage().contains("UKIDX_EVENT_ID_BLOCK_HASH");
    }

    @NotNull
    private Block storeBlock(final EventInfo eventInfo) throws NoSuchAlgorithmException {

        final BlockAdded toStore = (BlockAdded) eventInfo.getData();

        logger.info("Processing BlockAdded event. BlockHash: {} ", toStore.getBlockHash());

        final Block block = this.blockRepository.save(
                Block.builder()
                        .blockHash(toStore.getBlockHash())
                        .parentHash(toStore.getBlock().getHeader().getParentHash())
                        .timestamp(toStore.getBlock().getHeader().getTimeStamp())
                        .state(toStore.getBlock().getHeader().getStateRootHash())
                        .deployCount(toStore.getBlock().getBody().getDeployHashes().size())
                        .transferCount(toStore.getBlock().getBody().getTransferHashes().size())
                        .eraId(toStore.getBlock().getHeader().getEraId())
                        .proposer(toStore.getBlock().getBody().getProposer())
                        .blockHeight(toStore.getBlock().getHeader().getHeight())
                        .eventId(eventInfo.getId())
                        .build()
        );

        if (isEraEnded(toStore)) {

            logger.debug("era ended {}", toStore.getBlock().getHeader().getEraId());

            eraService.create(
                    toStore.getBlock().getHeader().getEraId(),
                    toStore.getBlock().getHeader().getHeight(),
                    toStore.getBlock().getHeader().getTimeStamp(),
                    toStore.getBlock().getHeader().getProtocolVersion()
            );

            rewardService.createRewards(toStore);

            if (isVersionGreaterOrEqual(eventInfo.getVersion(), V1_2_0)) {
                createEraValidator(toStore);
            } else {
                // Process the JSON directly to obtain the EraValidator information in version prior to 1.2.0
                createEraValidatorFromJson(eventInfo);
            }

            this.updateEraValidators(eventInfo);
        }

        return block;
    }

    private void createEraValidator(final BlockAdded toStore) {
        for (ValidatorWeight validator : toStore.getBlock().getHeader().getEraEnd().getNextEraValidatorWeights()) {
            this.eraValidatorService.create(
                    toStore.getBlock().getHeader().getEraId() + 1,
                    validator.getValidator(),
                    validator.getWeight(),
                    BigInteger.valueOf(0),
                    false,
                    false
            );
        }
    }

    /**
     * Creates an EraValidator for versions prior to V1.2.0
     *
     * @param eventInfo the event info obtained from kafka
     */
    private void createEraValidatorFromJson(final EventInfo eventInfo) throws NoSuchAlgorithmException {

        final BlockAdded toStore = (BlockAdded) eventInfo.getData();
        final TreeNode nextEraValidatorWeights = eventInfo.getJsonData().at("/block/header/era_end/next_era_validator_weights");

        if (!nextEraValidatorWeights.isMissingNode() && nextEraValidatorWeights.isArray()) {

            for (int i = 0; i < nextEraValidatorWeights.size(); i++) {
                TreeNode validatorNode = nextEraValidatorWeights.get(i);
                this.eraValidatorService.create(
                        toStore.getBlock().getHeader().getEraId() + 1,
                        PublicKey.fromTaggedHexString(validatorNode.get("validator").toString()),
                        new BigInteger(validatorNode.get("weight").toString()),
                        BigInteger.valueOf(0L),
                        false,
                        false);
            }
        }
    }

    private boolean isEraEnded(final BlockAdded blockAdded) {
        return blockAdded.getBlock().getHeader().getEraEnd() != null;
    }

    private void updateEraValidators(final EventInfo eventInfo) {

        final BlockAdded blockAdded = (BlockAdded) eventInfo.getData();

        final List<PublicKey> updatedValidators = new ArrayList<>();

        if (VersionUtils.isVersionGreaterOrEqual(eventInfo.getVersion(), V1_2_0)) {
            updatedValidators.addAll(updateEraValidators(blockAdded));
        } else {
            updatedValidators.addAll(legacyUpdateEraValidators(blockAdded));
        }

        //noinspection SimplifyStreamApiCallChains
        updatedValidators.addAll(blockAdded.getBlock().getHeader().getEraEnd().getEraReport().getEquivocators()
                .stream()
                .filter(publicKey -> !updatedValidators.contains(publicKey))
                .map(publicKey -> {
                            this.eraValidatorService.updateHasEquivocationAndWasActive(
                                    blockAdded.getBlock().getHeader().getEraId(),
                                    publicKey,
                                    true,
                                    !blockAdded.getBlock().getHeader().getEraEnd().getEraReport().getInactiveValidators().contains(publicKey)
                            );
                            return publicKey;
                        }
                ).toList());

        blockAdded.getBlock().getHeader().getEraEnd().getEraReport().getInactiveValidators()
                .stream()
                .filter(publicKey -> !updatedValidators.contains(publicKey))
                .forEach(publicKey ->
                        this.eraValidatorService.updateWasActive(
                                blockAdded.getBlock().getHeader().getEraId(),
                                publicKey,
                                false
                        )
                );
    }

    private List<PublicKey> updateEraValidators(final BlockAdded blockAdded) {
        return blockAdded.getBlock().getHeader().getEraEnd().getEraReport().getRewards()
                .stream()
                .map(reward -> {
                            eraValidatorService.update(
                                    blockAdded.getBlock().getHeader().getEraId(),
                                    reward.getValidator(),
                                    BigInteger.valueOf(reward.getAmount()),
                                    blockAdded.getBlock().getHeader().getEraEnd().getEraReport().getEquivocators().contains(reward.getValidator()),
                                    blockAdded.getBlock().getHeader().getEraEnd().getEraReport().getInactiveValidators().contains(reward.getValidator())
                            );
                            return reward.getValidator();
                        }

                ).toList();
    }

    @SuppressWarnings({"java:S1135", "java:S125"}) // Suppress sonar TODO warning
    /* TODO
       for (let publicKeyHex in event.block.header.era_end.era_report.rewards) {
            updatedValidators.push(publicKeyHex);

            this.models.EraValidator.update({
                    rewards: event.block.header.era_end.era_report.rewards[publicKeyHex],
                    hasEquivocation: event.block.header.era_end.era_report.equivocators.includes(publicKeyHex),
                    wasActive: !event.block.header.era_end.era_report.inactive_validators.includes(publicKeyHex),
                }, {
                where: {
                    eraId: event.block.header.era_id,
                            publicKeyHex: publicKeyHex,
                }
            });
        }
     */
    @NotNull
    private List<PublicKey> legacyUpdateEraValidators(final BlockAdded blockAdded) {
        return Collections.emptyList();
    }
}
