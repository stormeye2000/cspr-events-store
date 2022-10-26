package com.stormeye.event.store.service.storage.impl.reward;


import com.stormeye.event.store.service.storage.StorageFactory;
import com.stormeye.event.service.storage.domain.Reward;
import com.stormeye.event.repository.RewardRepository;
import org.springframework.stereotype.Component;

/**
 * @author ian@meywood.com
 */
@Component
public class RewardStorage {

    private final RewardRepository repository;

    public RewardStorage(final StorageFactory storageFactory, final RewardRepository repository) {
        this.repository = repository;
    }

    public Reward store(final Reward reward) {
        return repository.save(reward);
    }
}
