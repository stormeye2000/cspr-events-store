package com.stormeye.event.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.PagingAndSortingRepository;
import com.casper.sdk.model.common.Digest;
import com.stormeye.event.service.storage.domain.DeployBid;

import java.util.List;

/**
 * @author carl
 */
public interface DeployBidRepository  extends PagingAndSortingRepository<DeployBid, Long>, JpaRepository<DeployBid, Long> {

    DeployBid findByValidatorPublicKey(final Digest validatorPublicKey);
    List<DeployBid> findByDeployHash(final Digest deployHash);

}
