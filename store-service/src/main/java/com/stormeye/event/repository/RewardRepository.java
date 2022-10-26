package com.stormeye.event.repository;

import com.stormeye.event.service.storage.domain.Reward;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.PagingAndSortingRepository;

/**
 * @author ian@meywood.com
 */
public interface RewardRepository extends PagingAndSortingRepository<Reward, Long>, JpaRepository<Reward, Long> {

}
