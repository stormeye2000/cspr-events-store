package com.stormeye.event.store.service.storage.impl.reward;


import com.casper.sdk.identifier.block.HeightBlockIdentifier;
import com.casper.sdk.model.deploy.Delegator;
import com.casper.sdk.model.deploy.SeigniorageAllocation;
import com.casper.sdk.model.deploy.Validator;
import com.casper.sdk.model.era.EraInfoData;
import com.casper.sdk.model.event.blockadded.BlockAdded;
import com.casper.sdk.model.key.PublicKey;
import com.casper.sdk.service.CasperService;
import com.stormeye.event.repository.DelegatorRewardRepository;
import com.stormeye.event.repository.ValidatorRewardRepository;
import com.stormeye.event.service.storage.domain.DelegatorReward;
import com.stormeye.event.service.storage.domain.Reward;
import com.stormeye.event.service.storage.domain.ValidatorReward;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.math.BigInteger;
import java.util.Date;
import java.util.Optional;

/**
 * The service for creating and retrieving rewards
 *
 * @author ian@meywood.com
 */
@Component
public class RewardService {

    private final Logger logger = LoggerFactory.getLogger(RewardService.class);
    private final CasperService casperService;
    private final DelegatorRewardRepository delegatorRewardRepository;
    private final ValidatorRewardRepository validatorRewardRepository;

    public RewardService(final CasperService casperService,
                         final DelegatorRewardRepository delegatorRewardRepository,
                         final ValidatorRewardRepository validatorRewardRepository) {
        this.casperService = casperService;
        this.delegatorRewardRepository = delegatorRewardRepository;
        this.validatorRewardRepository = validatorRewardRepository;
    }

    public void createRewards(final BlockAdded blockAdded) {
        try {
            // What to do if unable to contact node. we need a queue here
            var eraInfo = casperService.getEraInfoBySwitchBlock(
                    new HeightBlockIdentifier(blockAdded.getBlock().getHeader().getHeight())
            );

            if (hasSeigniorageAllocations(eraInfo)) {
                eraInfo.getEraSummary().getStoredValue().getValue().getSeigniorageAllocations().forEach(allocation ->
                        createReward(
                                eraInfo.getEraSummary().getEraId(),
                                allocation,
                                blockAdded.getBlock().getHeader().getTimeStamp()
                        )
                );
            } else {
                logger.debug(
                        "No rewards for era {} block height {}",
                        blockAdded.getBlock().getHeader().getEraId(),
                        blockAdded.getBlock().getHeader().getHeight()
                );
            }

        } catch (Exception e) {
            logger.error("Error getEraInfoBySwitchBlock for height {}", blockAdded.getBlock().getHeader().getHeight(), e);
        }
    }

    @SuppressWarnings("unchecked")
    public <T extends Reward> T createReward(final long eraId,
                                             final SeigniorageAllocation allocation,
                                             final Date timestamp) {
        if (allocation instanceof Validator) {
            return (T) this.createValidatorReward(
                    eraId,
                    ((Validator) allocation).getValidatorPublicKey(),
                    allocation.getAmount(),
                    timestamp
            );
        } else if (allocation instanceof Delegator) {
            return (T) createDelegatorReward(
                    eraId,
                    ((Delegator) allocation).getDelegatorPublicKey(),
                    ((Delegator) allocation).getValidatorPublicKey(),
                    allocation.getAmount(),
                    timestamp
            );
        } else {
            throw new IllegalArgumentException("Null or unsupported Reward Type" + allocation);
        }
    }

    public DelegatorReward createDelegatorReward(final long eraId,
                                                 final PublicKey delegatorPublicKey,
                                                 final PublicKey validatorPublicKey,
                                                 final BigInteger amount,
                                                 final Date timeStamp) {
        if (logger.isDebugEnabled()) {
            logger.debug(
                    "createDelegatorReward eraId {}, delegatorPublicKey, {}, validatorPublicKey {}, amount {}, timestamp {}",
                    eraId,
                    delegatorPublicKey.getAlgoTaggedHex(),
                    validatorPublicKey.getAlgoTaggedHex(),
                    amount,
                    timeStamp
            );
        }

        return delegatorRewardRepository.save(
                new DelegatorReward(eraId, delegatorPublicKey, validatorPublicKey, amount, timeStamp)
        );
    }

    public ValidatorReward createValidatorReward(final long eraId,
                                                 final PublicKey validatorPublicKey,
                                                 final BigInteger amount,
                                                 final Date timeStamp) {

        if (logger.isDebugEnabled()) {
            logger.debug(
                    "createValidatorReward eraId {}, validatorPublicKey {}, amount {}, timestamp {}",
                    eraId,
                    validatorPublicKey.getAlgoTaggedHex(),
                    amount,
                    timeStamp
            );
        }


        return validatorRewardRepository.save(new ValidatorReward(eraId, validatorPublicKey, amount, timeStamp));
    }

    public Optional<ValidatorReward> findValidatorRewardByEraIdAndPublicKey(long eraId, PublicKey publicKey) {
        return validatorRewardRepository.findByEraIdAndPublicKey(eraId, publicKey);
    }

    public Optional<DelegatorReward> findDelegatorRewardByEraIdAndPublicKeyAndValidatorPublicKey(
            long eraId,
            final PublicKey publicKey,
            final PublicKey validatorPublicKey) {
        return delegatorRewardRepository.findByEraIdAndPublicKeyAndValidatorPublicKey(eraId, publicKey, validatorPublicKey);
    }

    public Page<DelegatorReward> findDelegatorRewardsByEraId(final long eraId, final Pageable pageable) {
        return delegatorRewardRepository.findByEraId(eraId, pageable);
    }

    public Page<ValidatorReward> findValidatorRewardsByEraId(final long eraId, final Pageable pageable) {
        return validatorRewardRepository.findByEraId(eraId, pageable);
    }

    private boolean hasSeigniorageAllocations(final EraInfoData eraInfo) {
        return eraInfo != null
                && eraInfo.getEraSummary() != null
                && eraInfo.getEraSummary().getStoredValue() != null
                && eraInfo.getEraSummary().getStoredValue().getValue() != null
                && !CollectionUtils.isEmpty(eraInfo.getEraSummary().getStoredValue().getValue().getSeigniorageAllocations());
    }
}
