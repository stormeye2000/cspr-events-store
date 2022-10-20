package com.stormeye.event.store.services.storage.reward;

import com.casper.sdk.model.deploy.Delegator;
import com.casper.sdk.model.deploy.SeigniorageAllocation;
import com.casper.sdk.model.deploy.Validator;
import com.casper.sdk.model.event.blockadded.BlockAdded;
import com.stormeye.event.store.services.storage.StorageFactory;
import com.stormeye.event.store.services.storage.StorageService;
import com.stormeye.event.store.services.storage.reward.domain.DelegatorReward;
import com.stormeye.event.store.services.storage.reward.domain.Reward;
import com.stormeye.event.store.services.storage.reward.domain.ValidatorReward;
import com.stormeye.event.store.services.storage.reward.repository.RewardRepository;
import org.springframework.stereotype.Component;

/**
 * @author ian@meywood.com
 */
@Component
public class RewardStorage implements StorageService<BlockAdded, SeigniorageAllocation, Reward> {

    private final RewardRepository repository;

    public RewardStorage(final StorageFactory storageFactory, final RewardRepository repository) {
        this.repository = repository;
        storageFactory.register(Reward.class, this);
    }

    @Override
    public Reward store(final String source, final BlockAdded root,  final SeigniorageAllocation toStore) {

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

        if (reward != null) {
            repository.save(reward);
        }

        return reward;
    }
}
