package com.stormeye.event.store.services.storage.block.resource.resource;

import com.stormeye.event.store.services.storage.block.domain.Block;
import com.stormeye.event.store.services.storage.block.repository.BlockRepository;
import com.stormeye.event.store.services.storage.common.PageResponse;
import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.info.Contact;
import io.swagger.v3.oas.annotations.info.Info;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
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

    /** Enumeration of fields that can be sored on */
    private enum SortableFields {
        blockHeight,
        eraId,
        timestamp
    }

    /** The timestamp filename used for default sorting */
    public static final String TIMESTAMP = "timestamp";
    private final BlockRepository blockRepository;


    @Autowired
    public BlockResource(final BlockRepository blockRepository) {
        this.blockRepository = blockRepository;
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
    @Operation(tags = "blocks", summary = "Obtains the blocks",
            description = "Obtains a page of block that are sortable by timestamp, blockHeight and eraId")
    ResponseEntity<PageResponse<Block>> getBlocks(@Parameter(description = "The number of the page to obtain, starting from 1")
                                                  @RequestParam(value = "page", defaultValue = "1", required = false) final int page,
                                                  @Parameter(description = "The number of blocks to retrieved in a page, defaults to 10")
                                                  @RequestParam(value = "size", defaultValue = "10", required = false) final int size,
                                                  @Parameter(description = "The name of the field to sort on")
                                                  @RequestParam(value = "order_by", defaultValue = TIMESTAMP, required = false) final SortableFields orderBy,
                                                  @Parameter(description = "The direction of the sort")
                                                  @RequestParam(value = "order_direction", defaultValue = "DESC", required = false) final Sort.Direction orderDirection) {

        var request = PageRequest.of(page - 1, size, getSort(orderBy.name(), orderDirection));
        return ResponseEntity.ok(new PageResponse<>(blockRepository.findAll(request)));
    }

    @NotNull
    static Sort getSort(String orderBy, Sort.Direction orderDirection) {
        if (TIMESTAMP.equals(orderBy)) {
            return Sort.by(orderDirection, orderBy);
        } else return Sort.by(
                new Sort.Order(orderDirection, orderBy),
                new Sort.Order(Sort.Direction.ASC, TIMESTAMP)
        );
    }

}