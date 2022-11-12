package com.stormeye.event.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.PagingAndSortingRepository;
import com.casper.sdk.model.common.Digest;
import com.stormeye.event.service.storage.domain.Withdrawals;

import java.util.List;
import java.util.Optional;

/**
 * The Spring Data JPA repository for {@link Withdrawals} domain objects
 */

public interface WithdrawalsRepository extends PagingAndSortingRepository<Withdrawals, Long>, JpaRepository<Withdrawals, Long> {
    Optional<List<Withdrawals>> findByDeployHash(final Digest deployHash);
}
