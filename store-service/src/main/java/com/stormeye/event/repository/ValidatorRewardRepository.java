package com.stormeye.event.repository;

import com.casper.sdk.model.key.PublicKey;
import com.stormeye.event.service.storage.domain.ValidatorReward;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;

import java.util.Optional;

/**
 * The Spring Data Repository for ValidatorReward domain objects.
 *
 * @author ian@meywood.com
 */
public interface ValidatorRewardRepository extends PagingAndSortingRepository<ValidatorReward, Long>, JpaRepository<ValidatorReward, Long> {
    Optional<ValidatorReward> findByEraIdAndPublicKey(final long eraId, final PublicKey publicKey);

    Page<ValidatorReward> findByEraId(final long eraId, final Pageable pageable);

    Page<ValidatorReward> findByPublicKey(final PublicKey publicKey, final Pageable request);

    @Query("SELECT sum(e.amount) from ValidatorReward e  WHERE e.publicKey = :publicKey")
    long getTotalRewards(final PublicKey publicKey);
}
