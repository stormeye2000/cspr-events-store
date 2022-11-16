package com.stormeye.event.api.resource;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import com.casper.sdk.model.common.Digest;
import com.stormeye.event.api.common.PageResponse;
import com.stormeye.event.api.exception.ApiBadRequestException;
import com.stormeye.event.api.exception.ApiNotFoundException;
import com.stormeye.event.repository.DeployRepository;
import com.stormeye.event.repository.TransferRepository;
import com.stormeye.event.service.storage.domain.Deploy;
import com.stormeye.event.service.storage.domain.Transfer;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.info.Contact;
import io.swagger.v3.oas.annotations.info.Info;

/**
 * The Deploys REST API
 *
 * @author ian@meywood.com
 */
@RestController
@OpenAPIDefinition(
        info = @Info(
                title = "Casper Deploys REST API",
                description = "The Deploys REST API",
                contact = @Contact(
                        name = "Stormeye2000",
                        url = "https://github.com/stormeye2000/cspr-events-store"
                )
        )
)
public class DeployResource {

    private enum TransfersSortableFields {
        timestamp,
        fromAccount,
        toAccount,
        transferId,
        amount
    }
    private enum DeploysSortableFields {
        timestamp
    }


    final DeployRepository deployRepository;
    final TransferRepository transferRepository;
    public static final String TIMESTAMP = "timestamp";

    public DeployResource(final DeployRepository deployRepository, final TransferRepository transferRepository) {
        this.deployRepository = deployRepository;
        this.transferRepository = transferRepository;
    }

    @GetMapping(value = "/deploys", produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(tags = "deploys", summary = "Obtains a page of deploys", description = "Obtains a page of deploys that are sortable by timestamp")
    ResponseEntity<PageResponse<Deploy>> getDeploys(@Parameter(description = "The number of the page to obtain, starting from 1")
                                                  @RequestParam(value = "page", defaultValue = "1", required = false) final int page,
                                                   @Parameter(description = "The number of deploys to retrieved in a page, defaults to 10")
                                                  @RequestParam(value = "size", defaultValue = "10", required = false) final int size,
                                                    @Parameter(description = "The name of the field to sort on")
                                                    @RequestParam(value = "order_by", defaultValue = TIMESTAMP, required = false) final DeploysSortableFields orderBy,
                                                   @Parameter(description = "The direction of the sort")
                                                    @RequestParam(value = "order_direction", defaultValue = "DESC", required = false) final Sort.Direction orderDirection) {

        var request = PageRequest.of(page - 1, size, PageUtils.getSort(orderBy, orderDirection));
        return ResponseEntity.ok(new PageResponse<>(deployRepository.findAll(request)));
    }

    @Operation(tags = "deploys", summary = "Obtains a single deploy by its deploy hash",
            description = "Obtains a deploy by deployHash")
    @GetMapping(value = "/deploys/{deployHash}", produces = MediaType.APPLICATION_JSON_VALUE)
    ResponseEntity<Deploy> getDeploy(@Parameter(description = "The deployHash of the deploy to obtain")
                                   @PathVariable("deployHash") final String deployHash) {

        final Digest deployDigest = new Digest(deployHash);

        if (!deployDigest.isValid()){
            throw new ApiBadRequestException();
        }

        var deploy = deployRepository.findByDeployHash(deployDigest)
                .orElseThrow(ApiNotFoundException::new);

        return ResponseEntity.ok(deploy);

    }

    @GetMapping(value = "/deploys/{deployHash}/transfers", produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(tags = "deploys", summary = "Obtains a page of a deploys transfers", description = "Obtains a page of deploys transfers that are sortable by timestamp, from, to, transfer id and amount")
    ResponseEntity<PageResponse<Transfer>> getDeployTransfers(@Parameter(description = "The deployHash of the deploy to obtain")
                                                                @PathVariable("deployHash") final String deployHash,
                                                                @Parameter(description = "The number of the page to obtain, starting from 1")
                                                                @RequestParam(value = "page", defaultValue = "1", required = false) final int page,
                                                                @Parameter(description = "The number of transfers to retrieved in a page, defaults to 10")
                                                                @RequestParam(value = "size", defaultValue = "10", required = false) final int size,
                                                                @Parameter(description = "The name of the field to sort on")
                                                                @RequestParam(value = "order_by", defaultValue = TIMESTAMP, required = false) final TransfersSortableFields orderBy,
                                                                @Parameter(description = "The direction of the sort")
                                                                @RequestParam(value = "order_direction", defaultValue = "DESC", required = false) final Sort.Direction orderDirection) {


        final Digest deployDigest = new Digest(deployHash);
        if (!deployDigest.isValid()){
            throw new ApiBadRequestException();
        }

        var deploy = deployRepository.findByDeployHash(deployDigest)
                .orElseThrow(ApiNotFoundException::new);

        var request = PageRequest.of(page - 1, size, PageUtils.getSort(orderBy, orderDirection));

        return ResponseEntity.ok(new PageResponse<>(transferRepository.findByDeployHash(deployDigest, request)));

    }

}
