package com.stormeye.event.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.PagingAndSortingRepository;
import com.casper.sdk.model.common.Digest;
import com.stormeye.event.service.storage.domain.Transfer;
/**
 * The Spring Data JPA repository for {@link Transfer} domain objects
 */
public interface TransferRepository extends PagingAndSortingRepository<Transfer, Long>, JpaRepository<Transfer, Long> {
    Page<Transfer> findByDeployHash(final Digest deployHash, final Pageable pageable);

}
