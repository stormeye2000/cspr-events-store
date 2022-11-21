package com.stormeye.event.api.resource;

import com.casper.sdk.model.key.PublicKey;
import com.stormeye.event.api.common.PageResponse;
import com.stormeye.event.repository.DelegatorRewardRepository;
import com.stormeye.event.service.storage.domain.DelegatorReward;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Sort;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigInteger;
import java.security.NoSuchAlgorithmException;

import static com.stormeye.event.api.resource.ResourceUtils.buildPageRequest;
import static com.stormeye.event.api.resource.ResourceUtils.zeroIfNull;

/**
 * Casper Delegator REST API.
 *
 * @author ian@meywood.com
 */
@RestController
public class DelegatorResource {

    private enum DelegatorRewardSortableFields {
        eraId,
        amount,
        timestamp
    }

    public static final String TIMESTAMP = "timestamp";
    private final Logger logger = LoggerFactory.getLogger(DelegatorResource.class);

    private final DelegatorRewardRepository delegatorRewardRepository;

    public DelegatorResource(DelegatorRewardRepository delegatorRewardRepository) {
        this.delegatorRewardRepository = delegatorRewardRepository;
    }


    /**
     * Obtains a page of delegator rewards.
     *
     * @param publicKey      the public key of the delegator whose rewards are to be obtained
     * @param page           the page number
     * @param size           the size of the request page
     * @param orderBy        the name of the field to order on
     * @param orderDirection can be ASC or DESC
     * @return a page of delegator rewards as JSON
     */
    @GetMapping(value = "/delegators/{publicKey}/rewards", produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(tags = "Delegators", summary = "Obtains a page of delegator rewards",
            description = "Obtains a page of delegator rewards that are sortable by timestamp, blockHeight and eraId")
    ResponseEntity<PageResponse<DelegatorReward>> getDelegatorRewards(@Parameter(description = "The public key of the delegator whose rewards are to be obtained")
                                                                      @PathVariable(value = "publicKey") final String publicKey,
                                                                      @Parameter(description = "The number of the page to obtain, starting from 1")
                                                                      @RequestParam(value = "page", defaultValue = "1", required = false) final int page,
                                                                      @Parameter(description = "The number of delegator rewards to retrieved in a page, defaults to 10")
                                                                      @RequestParam(value = "size", defaultValue = "10", required = false) final int size,
                                                                      @Parameter(description = "The name of the field to sort on")
                                                                      @RequestParam(value = "order_by", defaultValue = TIMESTAMP, required = false) final DelegatorRewardSortableFields orderBy,
                                                                      @Parameter(description = "The direction of the sort")
                                                                      @RequestParam(value = "order_direction", defaultValue = "DESC", required = false) final Sort.Direction orderDirection) throws NoSuchAlgorithmException {

        logger.debug("getDelegatorRewards publicKey {}, page {}, size {}, orderBy {}, orderDirection {}",
                publicKey,
                page,
                size,
                orderBy,
                orderDirection
        );

        return ResponseEntity.ok(new PageResponse<>(delegatorRewardRepository.findByPublicKey(
                PublicKey.fromTaggedHexString(publicKey),
                buildPageRequest(page, size, orderBy, orderDirection, DelegatorRewardSortableFields.timestamp))
        ));
    }

    /**
     * Obtains the total rewards of a delegator.
     *
     * @param publicKey the delegator's public key
     * @return the total rewards
     * @throws NoSuchAlgorithmException on invalid key
     */
    @GetMapping(value = "/delegators/{publicKey}/total-rewards", produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(tags = "Delegators", summary = "Obtains the total delegator rewards",
            description = "Obtains the total delegator rewards")
    ResponseEntity<BigInteger> getTotalDelegatorRewards(@Parameter(description = "The public key of the delegator whose rewards are to be obtained")
                                                        @PathVariable(value = "publicKey") final String publicKey) throws NoSuchAlgorithmException {

        logger.debug("getTotalDelegatorRewards publicKey {}", publicKey);

        var totalRewards = delegatorRewardRepository.getTotalRewards(PublicKey.fromTaggedHexString(publicKey));
        return ResponseEntity.ok(zeroIfNull(totalRewards));
    }
}
