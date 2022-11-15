package com.stormeye.event.api.resource;

import com.casper.sdk.model.common.Digest;
import com.stormeye.event.api.common.PageResponse;
import com.stormeye.event.exception.NotFoundException;
import com.stormeye.event.repository.BlockRepository;
import com.stormeye.event.repository.EraValidatorRepository;
import com.stormeye.event.service.storage.domain.Block;
import com.stormeye.event.service.storage.domain.EraValidator;
import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.info.Contact;
import io.swagger.v3.oas.annotations.info.Info;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * The Blocks REST API
 *
 * @author ian@meywood.com
 */
@RestController
@OpenAPIDefinition(
        info = @Info(
                title = "Casper Block REST API",
                description = "The Blocks REST API",
                contact = @Contact(
                        name = "Stormeye2000",
                        url = "https://github.com/stormeye2000/cspr-producer-audit"
                )
        )
)
public class BlockResource {

    /** Enumeration of fields that a block can be sored on */
    private enum BlockSortableFields {
        blockHeight,
        eraId,
        timestamp
    }

    /** Enumeration of fields that a block can be sored on */
    private enum EraValidatorSortableFields {
        eraId,
        publicKet,
        weight,
        rewards
    }

    /** The timestamp filename used for default sorting */
    public static final String TIMESTAMP = "timestamp";
    private final BlockRepository blockRepository;
    private final EraValidatorRepository eraValidatorRepository;
    private final Logger logger = LoggerFactory.getLogger(BlockResource.class);

    @Autowired
    public BlockResource(final BlockRepository blockRepository,
                         final EraValidatorRepository eraValidatorRepository) {
        this.blockRepository = blockRepository;
        this.eraValidatorRepository = eraValidatorRepository;
    }

    /**
     * Obtains a page of blocks
     *
     * @param page           the page number
     * @param size           the size of the request page
     * @param orderBy        the name of the field to order on
     * @param orderDirection can be ASC or DESC
     * @return a page of blocks as JSON
     */
    @GetMapping(value = "/blocks", produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(tags = "blocks", summary = "Obtains a page of blocks",
            description = "Obtains a page of block that are sortable by timestamp, blockHeight and eraId")
    ResponseEntity<PageResponse<Block>> getBlocks(@Parameter(description = "The number of the page to obtain, starting from 1")
                                                  @RequestParam(value = "page", defaultValue = "1", required = false) final int page,
                                                  @Parameter(description = "The number of blocks to retrieved in a page, defaults to 10")
                                                  @RequestParam(value = "size", defaultValue = "10", required = false) final int size,
                                                  @Parameter(description = "The name of the field to sort on")
                                                  @RequestParam(value = "order_by", defaultValue = TIMESTAMP, required = false) final BlockSortableFields orderBy,
                                                  @Parameter(description = "The direction of the sort")
                                                  @RequestParam(value = "order_direction", defaultValue = "DESC", required = false) final Sort.Direction orderDirection) {

        logger.debug("getBlocks page {}, size {}, orderBy {}, orderDirection {}", page, size, orderBy, orderDirection);

        var request = PageRequest.of(page - 1, size, getSort(orderBy.name(), orderDirection));
        return ResponseEntity.ok(new PageResponse<>(blockRepository.findAll(request)));
    }

    /**
     * Obtains a single by blockHash.
     *
     * @param blockHash the blockHash of the block to obtain
     * @return response entity with a block as its body
     */
    @Operation(tags = "blocks", summary = "Obtains a single block by its block hash",
            description = "Obtains a block by blockHash")
    @GetMapping(value = "/blocks/{blockHash}", produces = MediaType.APPLICATION_JSON_VALUE)
    ResponseEntity<Block> getBlock(@Parameter(description = "The blockHash of the block to obtain")
                                   @PathVariable("blockHash") final Digest blockHash) {

        logger.debug("getBlock blockHash {}", blockHash);

        var block = blockRepository.findByBlockHash(blockHash)
                .orElseThrow(() -> new NotFoundException("Unable to find block with hash: " + blockHash));
        return ResponseEntity.ok(block);

    }

    @GetMapping(value = "/era-validators", produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(tags = "era-validators'", summary = "Obtains a page of era validators",
            description = "Obtains a page of era validators that are sortable by timestamp, blockHeight and eraId")
    ResponseEntity<PageResponse<EraValidator>> getEraValidators(@Parameter(description = "The number of the page to obtain, starting from 1")
                                                                @RequestParam(value = "page", defaultValue = "1", required = false) final int page,
                                                                @Parameter(description = "The number of era validators to retrieved in a page, defaults to 10")
                                                                @RequestParam(value = "size", defaultValue = "10", required = false) final int size,
                                                                @Parameter(description = "The name of the field to sort on")
                                                                @RequestParam(value = "order_by", defaultValue = TIMESTAMP, required = false) final EraValidatorSortableFields orderBy,
                                                                @Parameter(description = "The direction of the sort")
                                                                @RequestParam(value = "order_direction", defaultValue = "DESC", required = false) final Sort.Direction orderDirection) {

        logger.debug("getEraValidators page {}, size {}, orderBy {}, orderDirection {}", page, size, orderBy, orderDirection);

        var request = PageRequest.of(page - 1, size, getSort(orderBy.name(), orderDirection));
        return ResponseEntity.ok(new PageResponse<>(eraValidatorRepository.findAll(request)));
    }


    static Sort getSort(String orderBy, Sort.Direction orderDirection) {
        if (TIMESTAMP.equals(orderBy)) {
            return Sort.by(orderDirection, orderBy);
        } else return Sort.by(
                new Sort.Order(orderDirection, orderBy),
                new Sort.Order(Sort.Direction.ASC, TIMESTAMP)
        );
    }

}
