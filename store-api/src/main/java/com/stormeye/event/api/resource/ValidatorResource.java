package com.stormeye.event.api.resource;

import com.stormeye.event.api.common.PageResponse;
import com.stormeye.event.repository.BlockRepository;
import com.stormeye.event.repository.DelegatorRewardRepository;
import com.stormeye.event.repository.ValidatorRewardRepository;
import com.stormeye.event.service.storage.domain.Block;
import com.stormeye.event.service.storage.domain.ValidatorReward;
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

import static com.casper.sdk.model.key.PublicKey.fromTaggedHexString;
import static com.stormeye.event.api.resource.ResourceUtils.buildPageRequest;
import static com.stormeye.event.api.resource.ResourceUtils.zeroIfNull;

/**
 * Casper Validator REST API.
 *
 * @author ian@meywood.com
 */
@RestController
public class ValidatorResource {

    private enum ValidationRewardSortableFields {
        eraId,
        amount,
        timestamp
    }

    /** The timestamp filename used for default sorting */
    private static final String TIMESTAMP = "timestamp";
    private final Logger logger = LoggerFactory.getLogger(ValidatorResource.class);
    private final BlockRepository blockRepository;
    private final DelegatorRewardRepository delegatorRewardRepository;
    private final ValidatorRewardRepository validatorRewardRepository;

    public ValidatorResource(final BlockRepository blockRepository,
                             final DelegatorRewardRepository delegatorRewardRepository,
                             final ValidatorRewardRepository validatorRewardRepository) {
        this.delegatorRewardRepository = delegatorRewardRepository;
        this.validatorRewardRepository = validatorRewardRepository;
        this.blockRepository = blockRepository;
    }

    /**
     * Obtains a page of validator rewards.
     *
     * @param publicKey      the public key of the validator whose rewards are to be obtained
     * @param page           the page number
     * @param size           the size of the request page
     * @param orderBy        the name of the field to order on
     * @param orderDirection can be ASC or DESC
     * @return a page of validator rewards as JSON
     */
    @GetMapping(value = "/validators/{publicKey}/rewards", produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(tags = "Validators", summary = "Obtains a page of validator rewards",
            description = "Obtains a page of validator rewards that are sortable by timestamp, blockHeight and eraId")
    ResponseEntity<PageResponse<ValidatorReward>> getValidatorRewards(@Parameter(description = "The public key of the validator whose rewards are to be obtained")
                                                                      @PathVariable(value = "publicKey") final String publicKey,
                                                                      @Parameter(description = "The number of the page to obtain, starting from 1")
                                                                      @RequestParam(value = "page", defaultValue = "1", required = false) final int page,
                                                                      @Parameter(description = "The number of validator rewards to retrieved in a page, defaults to 10")
                                                                      @RequestParam(value = "size", defaultValue = "10", required = false) final int size,
                                                                      @Parameter(description = "The name of the field to sort on")
                                                                      @RequestParam(value = "order_by", defaultValue = TIMESTAMP, required = false) final ValidationRewardSortableFields orderBy,
                                                                      @Parameter(description = "The direction of the sort")
                                                                      @RequestParam(value = "order_direction", defaultValue = "DESC", required = false) final Sort.Direction orderDirection) throws NoSuchAlgorithmException {

        logger.debug("getValidatorRewards publicKey {}, page {}, size {}, orderBy {}, orderDirection {}",
                publicKey,
                page,
                size,
                orderBy,
                orderDirection
        );

        return ResponseEntity.ok(new PageResponse<>(validatorRewardRepository.findByPublicKey(
                fromTaggedHexString(publicKey), buildPageRequest(page, size, orderBy, orderDirection))
        ));
    }

    /**
     * Obtains the total rewards of a validator.
     *
     * @param publicKey the validators public key
     * @return the total rewards
     * @throws NoSuchAlgorithmException on invalid key
     */
    @GetMapping(value = "/validators/{publicKey}/total-rewards", produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(tags = "Validators", summary = "Obtains the validator's total rewards",
            description = "Obtains the validator's total rewards")
    ResponseEntity<BigInteger> getTotalValidatorRewards(@Parameter(description = "The public key of the validator whose total rewards are to be obtained")
                                                        @PathVariable(value = "publicKey") final String publicKey) throws NoSuchAlgorithmException {

        logger.debug("getTotalValidatorRewards publicKey {}", publicKey);

        var totalRewards = validatorRewardRepository.getTotalRewards(fromTaggedHexString(publicKey));
        return ResponseEntity.ok(zeroIfNull(totalRewards));
    }

    /**
     * Obtains the total rewards of a validator delegator rewards
     *
     * @param publicKey the validators public key
     * @return the total rewards
     * @throws NoSuchAlgorithmException on invalid key
     */
    @GetMapping(value = "/validators/{publicKey}/total-delegator-rewards", produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(tags = "Validators", summary = "Obtains the total rewards of a validator delegator rewards",
            description = "Obtains the total rewards of a validator delegator rewards")
    ResponseEntity<BigInteger> getTotalValidatorDelegatorRewards(@Parameter(description = "The public key of the validator whose total rewards are to be obtained")
                                                                 @PathVariable(value = "publicKey") final String publicKey) throws NoSuchAlgorithmException {

        logger.debug("getTotalValidatorRewards publicKey {}", publicKey);

        var totalRewards = delegatorRewardRepository.getTotalDelegatorRewards(fromTaggedHexString(publicKey));
        return ResponseEntity.ok(zeroIfNull(totalRewards));
    }

    /**
     * Obtains a page of validator blocks.
     *
     * @param publicKey      the public key of the validator whose blocks are to be obtained
     * @param page           the page number
     * @param size           the size of the request page
     * @param orderBy        the name of the field to order on
     * @param orderDirection can be ASC or DESC
     * @return a page of validator blocks as JSON
     */
    @GetMapping(value = "/validators/{publicKey}/blocks", produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(tags = "Validators",
            summary = "Obtains a page of blocked proposed by the validator",
            description = "Obtains a page of blocked proposed by the validator that are sortable by blockHeight, deployCount, transferCount, timestamp, eraId")
    ResponseEntity<PageResponse<Block>> getValidatorBlocks(@Parameter(description = "The public key of the validator whose rewards are to be obtained")
                                                           @PathVariable(value = "publicKey") final String publicKey,
                                                           @RequestParam(value = "page", defaultValue = "1", required = false) final int page,
                                                           @Parameter(description = "The number of validator rewards to retrieved in a page, defaults to 10")
                                                           @RequestParam(value = "size", defaultValue = "10", required = false) final int size,
                                                           @Parameter(description = "The name of the field to sort on")
                                                           @RequestParam(value = "order_by", defaultValue = TIMESTAMP, required = false) final BlockSortableFields orderBy,
                                                           @Parameter(description = "The direction of the sort")
                                                           @RequestParam(value = "order_direction", defaultValue = "DESC", required = false) final Sort.Direction orderDirection) throws NoSuchAlgorithmException {

        logger.debug("getValidatorBlocks publicKey {}, page {}, size {}, orderBy {}, orderDirection {}",
                publicKey,
                page,
                size,
                orderBy,
                orderDirection
        );

        return ResponseEntity.ok(new PageResponse<>(blockRepository.findByProposer(
                fromTaggedHexString(publicKey),
                buildPageRequest(page, size, orderBy, orderDirection))
        ));
    }

}
