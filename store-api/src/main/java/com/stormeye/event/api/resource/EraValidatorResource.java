package com.stormeye.event.api.resource;

import static com.stormeye.event.api.resource.ResourceUtils.buildPageRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.stormeye.event.api.common.PageResponse;
import com.stormeye.event.repository.EraValidatorRepository;
import com.stormeye.event.service.storage.domain.EraValidator;
import com.stormeye.network.CrossOriginConfig;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;

/**
 * The REST APIs for {@link com.stormeye.event.service.storage.domain.EraValidator} domain objects
 *
 * @author ian@meywood.com
 */
@RestController
@RequestMapping("/api/v1")
class EraValidatorResource implements CrossOriginConfig {

    /** Enumeration of fields that a block can be sored on */
    @SuppressWarnings("java:S115") // Suppress: Rename this constant name to match the regular expression ‘^[A-Z][A-Z0-9]*(_[A-Z0-9]+)*$’
    private enum EraValidatorSortableFields {
        id,
        eraId,
        publicKey,
        weight,
        rewards
    }

    private final Logger logger = LoggerFactory.getLogger(EraValidatorResource.class);
    private final EraValidatorRepository eraValidatorRepository;

    @Autowired
    public EraValidatorResource(final EraValidatorRepository eraValidatorRepository) {
        this.eraValidatorRepository = eraValidatorRepository;
    }

    /**
     * Obtains a page of era validators.
     *
     * @param page           the number of the page to obtain, starting from 1
     * @param size           the size of the request page
     * @param orderBy        the name of the field to order on
     * @param orderDirection can be ASC or DESC
     * @return a PageResponse of EraValidators
     */
    @GetMapping(value = "/era-validators", produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(tags = "Era Validators", summary = "Obtains a page of era validators",
            description = "Obtains a page of era validators that are sortable by eraId, publicKey, weight, and rewards")
    ResponseEntity<PageResponse<EraValidator>> getEraValidators(@Parameter(description = "The number of the page to obtain, starting from 1")
                                                                @RequestParam(value = "page", defaultValue = "1", required = false) final int page,
                                                                @Parameter(description = "The number of era validators to retrieved in a page, defaults to 10")
                                                                @RequestParam(value = "size", defaultValue = "10", required = false) final int size,
                                                                @Parameter(description = "The name of the field to sort on")
                                                                @RequestParam(value = "order_by", defaultValue = "id", required = false) final EraValidatorSortableFields orderBy,
                                                                @Parameter(description = "The direction of the sort")
                                                                @RequestParam(value = "order_direction", defaultValue = "DESC", required = false) final Sort.Direction orderDirection) {

        logger.debug("getEraValidators page {}, size {}, orderBy {}, orderDirection {}", page, size, orderBy, orderDirection);

        return ResponseEntity.ok(
                new PageResponse<>(eraValidatorRepository.findAll(buildPageRequest(
                                page,
                                size,
                                orderBy,
                                orderDirection,
                                EraValidatorSortableFields.eraId
                        )
                ))
        );
    }

    /**
     * Obtains a page of era validators for a specific era.
     *
     * @param eraId          The ID of the era to obtain validators for
     * @param page           the number of the page to obtain, starting from 1
     * @param size           the size of the request page
     * @param orderBy        the name of the field to order on
     * @param orderDirection can be ASC or DESC
     * @return a PageResponse of EraValidators
     */
    @GetMapping(value = "/era-validators/{eraId}", produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(tags = "Era Validators", summary = "Obtains a page of era validators for a specific era",
            description = "Obtains a page of era validators that are sortable by eraId, publicKey, weight, and rewards")
    ResponseEntity<PageResponse<EraValidator>> getEraValidatorsByEraId(@Parameter(description = "The ID of the era to obtain validators for")
                                                                       @PathVariable(name = "eraId") final long eraId,
                                                                       @Parameter(description = "The number of the page to obtain, starting from 1")
                                                                       @RequestParam(value = "page", defaultValue = "1", required = false) final int page,
                                                                       @Parameter(description = "The number of era validators to retrieved in a page, defaults to 10")
                                                                       @RequestParam(value = "size", defaultValue = "10", required = false) final int size,
                                                                       @Parameter(description = "The name of the field to sort on")
                                                                       @RequestParam(value = "order_by", defaultValue = "id", required = false) final EraValidatorSortableFields orderBy,
                                                                       @Parameter(description = "The direction of the sort")
                                                                       @RequestParam(value = "order_direction", defaultValue = "DESC", required = false) final Sort.Direction orderDirection) {

        logger.debug("getEraValidators page {}, size {}, orderBy {}, orderDirection {}", page, size, orderBy, orderDirection);

        return ResponseEntity.ok(
                new PageResponse<>(eraValidatorRepository.findByEraId(
                        eraId,
                        buildPageRequest(page, size, orderBy, orderDirection, EraValidatorSortableFields.eraId))
                )
        );
    }

}
