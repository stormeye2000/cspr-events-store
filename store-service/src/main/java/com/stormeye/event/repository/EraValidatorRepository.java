package com.stormeye.event.repository;

import com.casper.sdk.model.key.PublicKey;
import com.stormeye.event.service.storage.domain.EraValidator;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;

import java.math.BigInteger;
import java.util.Optional;

/**
 * Spring Data Repository for {@link EraValidator} domain objects
 *
 * @author ian@meywood.com
 */
public interface EraValidatorRepository extends PagingAndSortingRepository<EraValidator, Long>, JpaRepository<EraValidator, Long> {

    Page<EraValidator> findByEraId(long eraId, final Pageable pageable);

    Optional<EraValidator> findByEraIdAndPublicKey(final long eraId, final PublicKey validator);

    @Modifying
    @Query("UPDATE EraValidator e SET e.rewards = :rewards," +
            " e.hasEquivocation = :hasEquivocation," +
            " e.wasActive = :wasActive " +
            "WHERE e.eraId = :eraId AND e.publicKey = :validator ")
    void update(@Param("eraId") final long eraId,
                @Param("validator") final PublicKey validator,
                @Param("rewards") final BigInteger rewards,
                @Param("hasEquivocation") final boolean hasEquivocation,
                @Param("wasActive") final boolean wasActive);


    @Modifying
    @Query("UPDATE EraValidator e SET e.hasEquivocation = :hasEquivocation, e.wasActive = :wasActive " +
            "WHERE e.eraId = :eraId AND e.publicKey = :validator ")
    void updateHasEquivocationAndWasActive(@Param("eraId") final long eraId,
                                           @Param("validator") final PublicKey validator,
                                           @Param("hasEquivocation") final boolean hasEquivocation,
                                           @Param("wasActive") final boolean wasActive);

    @Modifying
    @Query("UPDATE EraValidator e SET e.wasActive = :wasActive " +
            "WHERE e.eraId = :eraId AND e.publicKey = :validator ")
    void updateWasActive(@Param("eraId") final long eraId,
                         @Param("validator") final PublicKey validator,
                         @Param("wasActive") final boolean wasActive);
}


