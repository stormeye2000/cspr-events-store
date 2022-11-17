package com.stormeye.event.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.PagingAndSortingRepository;
import com.casper.sdk.model.common.Digest;
import com.stormeye.event.service.storage.domain.Withdrawal;

import java.util.List;

/**
 * The Spring Data JPA repository for {@link Withdrawal} domain objects
 */

public interface WithdrawalRepository extends PagingAndSortingRepository<Withdrawal, Long>, JpaRepository<Withdrawal, Long> {
    List<Withdrawal> findByDeployHash(final Digest deployHash);
}
