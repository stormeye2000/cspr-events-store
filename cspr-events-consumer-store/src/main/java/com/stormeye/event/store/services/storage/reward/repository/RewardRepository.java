package com.stormeye.event.store.services.storage.reward.repository;

import com.stormeye.event.store.services.storage.reward.domain.Reward;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.PagingAndSortingRepository;

/**
 * @author ian@meywood.com
 */
public interface RewardRepository extends PagingAndSortingRepository<Reward, Long>, JpaRepository<Reward, Long> {

}
