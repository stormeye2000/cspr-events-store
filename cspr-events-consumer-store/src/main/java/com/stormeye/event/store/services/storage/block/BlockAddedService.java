package com.stormeye.event.store.services.storage.block;

import com.casper.sdk.identifier.block.HeightBlockIdentifier;
import com.casper.sdk.model.deploy.Delegator;
import com.casper.sdk.model.deploy.SeigniorageAllocation;
import com.casper.sdk.model.deploy.Validator;
import com.casper.sdk.model.event.blockadded.BlockAdded;
import com.casper.sdk.service.CasperService;
import com.stormeye.event.store.exceptions.EventConsumerException;
import com.stormeye.event.store.services.storage.StorageFactory;
import com.stormeye.event.store.services.storage.StorageService;
import com.stormeye.event.store.services.storage.block.domain.Block;
import com.stormeye.event.store.services.storage.block.repository.BlockRepository;
import com.stormeye.event.store.services.storage.era.EraService;
import com.stormeye.event.store.services.storage.era.domain.Era;
import com.stormeye.event.store.services.storage.reward.RewardStorage;
import com.stormeye.event.store.services.storage.reward.domain.DelegatorReward;
import com.stormeye.event.store.services.storage.reward.domain.Reward;
import com.stormeye.event.store.services.storage.reward.domain.ValidatorReward;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;

/**
 * @author ian@meywood.com
 */
@Component
class BlockAddedService implements StorageService<BlockAdded, Block> {

    private final Logger logger = LoggerFactory.getLogger(BlockAddedService.class);
    private final BlockRepository blockRepository;
    private final StorageFactory storageFactory;

    private final EraService eraService;

    private final RewardStorage rewardStorage;

    BlockAddedService(final BlockRepository blockRepository,
                      final StorageFactory storageFactory,
                      final EraService eraService,
                      final RewardStorage rewardStorage) {
        this.blockRepository = blockRepository;
        this.storageFactory = storageFactory;
        this.eraService = eraService;
        this.rewardStorage = rewardStorage;
        this.storageFactory.register(BlockAdded.class, this);
    }

    @Override
    @Transactional
    public Block store(final String source, final BlockAdded toStore) {

        var block = this.blockRepository.save(
                new Block(toStore.getBlockHash(),
                        toStore.getBlock().getHeader().getParentHash(),
                        toStore.getBlock().getHeader().getTimeStamp(),
                        toStore.getBlock().getHeader().getStateRootHash(),
                        toStore.getBlock().getBody().getDeployHashes().size(),
                        toStore.getBlock().getBody().getTransferHashes().size(),
                        toStore.getBlock().getHeader().getEraId(),
                        toStore.getBlock().getBody().getProposer(),
                        toStore.getBlock().getHeader().getHeight()
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
            throw new EventConsumerException(e);
        }
    }
}
