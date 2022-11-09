package com.stormeye.event.repository;

import com.casper.sdk.model.key.PublicKey;
import com.stormeye.event.service.storage.domain.EraValidator;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.PagingAndSortingRepository;

import java.util.Optional;

/**
 * Spring Data Repository for {@link EraValidator} domain objects
 *
 * @author ian@meywood.com
 */
public interface EraValidatorRepository extends PagingAndSortingRepository<EraValidator, Long>, JpaRepository<EraValidator, Long> {
    Page<EraValidator> findByEraId(long eraId, final Pageable pageable);

    Optional<EraValidator> findByEraIdAndPublicKey(final long eraId, final PublicKey validator);
}
