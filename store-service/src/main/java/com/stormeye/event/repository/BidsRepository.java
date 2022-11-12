package com.stormeye.event.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.PagingAndSortingRepository;
import com.casper.sdk.model.common.Digest;
import com.stormeye.event.service.storage.domain.Bids;

import java.util.List;
import java.util.Optional;

/**
 * The Spring Data JPA repository for {@link Bids} domain objects
 */
public interface BidsRepository extends PagingAndSortingRepository<Bids, Long>, JpaRepository<Bids, Long> {
    Optional<List<Bids>> findByDeployHash(final Digest deployHash);
}
