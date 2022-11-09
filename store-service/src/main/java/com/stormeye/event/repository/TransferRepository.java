package com.stormeye.event.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.PagingAndSortingRepository;
import com.casper.sdk.model.common.Digest;
import com.stormeye.event.service.storage.domain.Transfer;

public interface TransferRepository extends PagingAndSortingRepository<Transfer, Long>, JpaRepository<Transfer, Long> {

    Transfer findByDeployHashAndBlockHash(final Digest deployHash, final Digest blockHash);

}
