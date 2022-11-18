package com.stormeye.event.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.PagingAndSortingRepository;
import com.casper.sdk.model.common.Digest;
import com.stormeye.event.service.storage.domain.Bid;

import java.util.List;

/**
 * The Spring Data JPA repository for {@link Bid} domain objects
 */
public interface BidRepository extends PagingAndSortingRepository<Bid, Long>, JpaRepository<Bid, Long> {
    List<Bid> findByDeployHash(final Digest deployHash);
}
