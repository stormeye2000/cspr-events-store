package com.stormeye.event.api.resource;

import com.casper.sdk.model.key.PublicKey;
import com.stormeye.event.api.common.PageResponse;
import com.stormeye.event.repository.BlockRepository;
import com.stormeye.event.repository.ValidatorRewardRepository;
import com.stormeye.event.service.storage.domain.Block;
import com.stormeye.event.service.storage.domain.ValidatorReward;
import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.info.Contact;
import io.swagger.v3.oas.annotations.info.Info;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigInteger;
import java.security.NoSuchAlgorithmException;

import static com.stormeye.event.api.resource.PageUtils.getSort;

/**
 * Casper Validator REST API.
 *
 * @author ian@meywood.com
 */
@RestController
@OpenAPIDefinition(
        info = @Info(
                title = "Casper Validator REST API",
                description = "The Validator REST API",
                contact = @Contact(
                        name = "Stormeye2000",
                        url = "https://github.com/stormeye2000/cspr-producer-audit"
                )
        )
)
public class ValidatorResource {

    private enum ValidationRewardSortableFields {
        eraId,
        amount,
        timestamp
    }

    /** The timestamp filename used for default sorting */
    private static final String TIMESTAMP = "timestamp";
    private final Logger logger = LoggerFactory.getLogger(ValidatorResource.class);
    private final ValidatorRewardRepository validatorRewardRepository;
    private final BlockRepository blockRepository;

    public ValidatorResource(final BlockRepository blockRepository,
                             final ValidatorRewardRepository validatorRewardRepository) {
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
    @Operation(tags = "validator-rewards'", summary = "Obtains a page of validator rewards",
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

        var request = PageRequest.of(page - 1, size, getSort(orderBy, orderDirection));

        return ResponseEntity.ok(new PageResponse<>(validatorRewardRepository.findByPublicKey(
                PublicKey.fromTaggedHexString(publicKey), request)
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
    @Operation(tags = "validator-total-rewards'", summary = "Obtains the validator's total rewards",
            description = "Obtains the validator's total rewards")
    ResponseEntity<BigInteger> getTotalValidatorRewards(@Parameter(description = "The public key of the validator whose total rewards are to be obtained")
                                                        @PathVariable(value = "publicKey") final String publicKey) throws NoSuchAlgorithmException {

        logger.debug("getTotalValidatorRewards publicKey {}", publicKey);

        return ResponseEntity.ok(validatorRewardRepository.getTotalRewards(
                PublicKey.fromTaggedHexString(publicKey)
        ));
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
    @Operation(tags = "validator-blocks'",
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

        var request = PageRequest.of(page - 1, size, getSort(orderBy, orderDirection));

        return ResponseEntity.ok(new PageResponse<>(blockRepository.findByProposer(PublicKey.fromTaggedHexString(publicKey), request)));
    }

}
