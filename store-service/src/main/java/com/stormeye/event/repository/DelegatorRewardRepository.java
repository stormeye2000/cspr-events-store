package com.stormeye.event.repository;

import com.casper.sdk.model.key.PublicKey;
import com.stormeye.event.service.storage.domain.DelegatorReward;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;

import java.math.BigInteger;
import java.util.Optional;

/**
 * The repository for rewards
 *
 * @author ian@meywood.com
 */
public interface DelegatorRewardRepository extends PagingAndSortingRepository<DelegatorReward, Long>, JpaRepository<DelegatorReward, Long> {

    Page<DelegatorReward> findByEraIdAndPublicKey(long eraId, PublicKey publicKey, final Pageable pageable);

    Optional<DelegatorReward> findByEraIdAndPublicKeyAndValidatorPublicKey(final long eraId,
                                                                           final PublicKey publicKey,
                                                                           final PublicKey validatorPublicKey);

    Page<DelegatorReward> findByEraId(final long eraId, final Pageable pageable);

    Page<DelegatorReward> findByPublicKey(final PublicKey publicKey, final Pageable request);

    @Query("SELECT sum(e.amount) from DelegatorReward e WHERE e.publicKey = :publicKey")
    BigInteger getTotalRewards(final PublicKey publicKey);

    @Query("SELECT sum(e.amount) from DelegatorReward e WHERE e.validatorPublicKey = :validatorPublicKey")
    BigInteger getTotalDelegatorRewards(final PublicKey validatorPublicKey);
}
