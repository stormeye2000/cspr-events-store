package com.stormeye.event.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.PagingAndSortingRepository;
import com.casper.sdk.model.common.Digest;
import com.stormeye.event.service.storage.domain.Deploy;

/**
 * The Spring Data JPA repository for {@link Deploy} domain objects
 */
public interface DeployRepository extends PagingAndSortingRepository<Deploy, Long>, JpaRepository<Deploy, Long> {
    Deploy findByDeployHashAndEventId(final Digest deployHash, final long id);
}
