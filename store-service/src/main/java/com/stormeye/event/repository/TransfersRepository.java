package com.stormeye.event.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.PagingAndSortingRepository;
import com.casper.sdk.model.common.Digest;
import com.stormeye.event.service.storage.domain.Transfers;

import java.util.Optional;
/**
 * The Spring Data JPA repository for {@link Transfers} domain objects
 */
public interface TransfersRepository extends PagingAndSortingRepository<Transfers, Long>, JpaRepository<Transfers, Long> {
    Optional<Transfers> findByDeployHash(final Digest deployHash);
}
