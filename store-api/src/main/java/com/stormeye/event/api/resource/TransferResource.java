package com.stormeye.event.api.resource;

import static com.stormeye.event.api.resource.ResourceUtils.buildPageRequest;

import org.springframework.data.domain.Sort;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import com.stormeye.event.api.common.PageResponse;
import com.stormeye.event.repository.TransferRepository;
import com.stormeye.event.service.storage.domain.Transfer;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;

/**
 * The Transfers REST API
 *
 */
@RestController
public class TransferResource {

    final TransferRepository transferRepository;
    public static final String TIMESTAMP = "timestamp";

    public TransferResource(final TransferRepository transferRepository) {
        this.transferRepository = transferRepository;
    }

    /**
     * Gets all the transfers
     *
     * @param page which page to return
     * @param size page size
     * @param orderBy which column (field) to sort on (defaults to the time)
     * @param orderDirection which sort direction (defaults to DESC)
     * @return A page of transfers in json
     */
    @GetMapping(value = "/transfers", produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(tags = "Transfers", summary = "Obtains a page of a transfers", description = "Obtains a page of transfers that are sortable by timestamp, from, to, transfer id and amount")
    ResponseEntity<PageResponse<Transfer>> getTransfers(@Parameter(description = "The number of the page to obtain, starting from 1")
                                                              @RequestParam(value = "page", defaultValue = "1", required = false) final int page,
                                                              @Parameter(description = "The number of transfers to retrieved in a page, defaults to 10")
                                                              @RequestParam(value = "size", defaultValue = "10", required = false) final int size,
                                                              @Parameter(description = "The name of the field to sort on")
                                                              @RequestParam(value = "order_by", defaultValue = TIMESTAMP, required = false) final TransfersSortableFields orderBy,
                                                              @Parameter(description = "The direction of the sort")
                                                              @RequestParam(value = "order_direction", defaultValue = "DESC", required = false) final Sort.Direction orderDirection) {


        return ResponseEntity.ok(new PageResponse<>(transferRepository.findAll(buildPageRequest(page, size, orderBy, orderDirection, TransfersSortableFields.timestamp))));

    }


}
