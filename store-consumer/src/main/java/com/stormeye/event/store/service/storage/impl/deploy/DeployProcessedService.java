package com.stormeye.event.store.service.storage.impl.deploy;

import org.jetbrains.annotations.NotNull;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Component;
import com.casper.sdk.model.common.Digest;
import com.casper.sdk.model.deploy.Entry;
import com.casper.sdk.model.deploy.executionresult.Failure;
import com.casper.sdk.model.deploy.executionresult.Success;
import com.casper.sdk.model.deploy.transform.WriteBid;
import com.casper.sdk.model.deploy.transform.WriteTransfer;
import com.casper.sdk.model.deploy.transform.WriteWithdraw;
import com.casper.sdk.model.event.deployprocessed.DeployProcessed;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.stormeye.event.exception.StoreConsumerException;
import com.stormeye.event.repository.BidsRepository;
import com.stormeye.event.repository.DeployRepository;
import com.stormeye.event.repository.TransfersRepository;
import com.stormeye.event.repository.WithdrawalsRepository;
import com.stormeye.event.service.storage.domain.Bids;
import com.stormeye.event.service.storage.domain.Deploy;
import com.stormeye.event.service.storage.domain.Transfers;
import com.stormeye.event.service.storage.domain.Withdrawals;
import com.stormeye.event.store.service.storage.EventInfo;
import com.stormeye.event.store.service.storage.StorageFactory;
import com.stormeye.event.store.service.storage.StorageService;
import com.stormeye.event.store.service.storage.impl.common.TransactionalRunner;

import java.time.Instant;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Processes the DeployProcessedEvent
 * The event will have a Success or Failure result
 * When Failure just store the error message
 * When Success store all details along with Transfers, Bids and Withdrawals
 */
@Component
public class DeployProcessedService implements StorageService<Deploy> {

    private final DeployRepository deployRepository;
    private final TransfersRepository transfersRepository;
    private final BidsRepository bidsRepository;
    private final WithdrawalsRepository withdrawalsRepository;
    private final TransactionalRunner transactionalRunner;
    private final ObjectMapper mapper;

    public DeployProcessedService(final DeployRepository deployRepository, final TransactionalRunner transactionalRunner, final StorageFactory storageFactory, final TransfersRepository transfersRepository, final BidsRepository bidsRepository, final WithdrawalsRepository withdrawalsRepository, final ObjectMapper mapper) {
        this.deployRepository = deployRepository;
        this.transactionalRunner = transactionalRunner;
        this.transfersRepository = transfersRepository;
        this.bidsRepository = bidsRepository;
        this.withdrawalsRepository = withdrawalsRepository;
        this.mapper = mapper;
        storageFactory.register(DeployProcessed.class, this);
    }

    @Override
    public Deploy store(final EventInfo eventInfo) {

        final DeployProcessed toStore = (DeployProcessed) eventInfo.getData();

        try {
            return transactionalRunner.runInTransaction(() -> storeDeploy(eventInfo));
        } catch (Exception e) {
            if (isDuplicateEventException(e)) {
                return deployRepository.findByDeployHashAndEventId(toStore.getDeployHash(), eventInfo.getId());
            } else {
                throw StoreConsumerException.getRuntimeException(e);
            }
        }
    }

    private boolean isDuplicateEventException(Exception e) {
        return e instanceof DataIntegrityViolationException && e.getMessage().contains("UKIDXE_EVENT_ID_DEPLOY_HASH");
    }

    @NotNull
    private Deploy storeDeploy(final EventInfo eventInfo) throws JsonProcessingException {
        final DeployProcessed toStore = (DeployProcessed) eventInfo.getData();

        final ExecutionResults result = getExecutionDetails(toStore);

        final Deploy deploy = deployRepository.save(
                Deploy.builder()
                        .deployHash(toStore.getDeployHash())
                        .blockHash(new Digest(toStore.getBlockHash()))
                        .account(toStore.getAccount())
                        .cost(result.getCost())
                        .errorMessage(result.getErrorMessage())
                        .timestamp(Date.from(Instant.from(ZonedDateTime.parse(toStore.getTimestamp()))))
                        .eventId(eventInfo.getId())
                        .build()
        );

        if (result.getTransfers() != null && !result.getTransfers().isEmpty()){
            transfersRepository.saveAll(result.getTransfers());
        }
        if (result.getBids() != null && !result.getBids().isEmpty()){
            bidsRepository.saveAll(result.getBids());
        }
        if (result.getWithdrawals() != null && !result.getWithdrawals().isEmpty()){
            withdrawalsRepository.saveAll(result.getWithdrawals());
        }

        return deploy;

    }

    private ExecutionResults getExecutionDetails(final DeployProcessed deployProcessed) throws JsonProcessingException {

        final ExecutionResults result;

        if (deployProcessed.getExecutionResult() instanceof final Failure failure){
            result = ExecutionResults.builder()
                                    .cost(failure.getCost())
                                    .errorMessage(failure.getErrorMessage()).build();

        } else {

            result = getSuccessResults(deployProcessed);

        }

        return result;

    }

    private ExecutionResults getSuccessResults(final DeployProcessed deployProcessed) throws JsonProcessingException {

        final Success success = (Success) deployProcessed.getExecutionResult();

        final ExecutionResults result = ExecutionResults.builder()
                .cost(success.getCost()).build();

        final List<Entry> transforms = ((Success) deployProcessed.getExecutionResult()).getEffect().getTransforms();

        final List<Transfers> transfers = new ArrayList<>();
        final List<Bids> bids = new ArrayList<>();
        final List<Withdrawals> withdrawals = new ArrayList<>();

        for (Entry entry: transforms){

            if (entry.getTransform() instanceof final WriteTransfer writeTransfer){

                final Transfers transfer = Transfers.builder()
                        .transferId(writeTransfer.getTransfer().getId())
                        .transferHash(new Digest(entry.getKey().substring(9)))
                        .deployHash(new Digest(writeTransfer.getTransfer().getDeployHash()))
                        .blockHash(new Digest(deployProcessed.getBlockHash()))
                        .toAccount(new Digest(writeTransfer.getTransfer().getTo().substring(13)))
                        .fromAccount(new Digest(writeTransfer.getTransfer().getFrom().substring(13)))
                        .sourcePurse(writeTransfer.getTransfer().getSource())
                        .targetPurse(writeTransfer.getTransfer().getTarget())
                        .amount(writeTransfer.getTransfer().getAmount())
                        .build();

                transfers.add(transfer);

            }

            if (entry.getTransform() instanceof final WriteBid bid) {

                final Bids deployBid = Bids.builder()
                        .bondingPurse(bid.getBid().getBondingPurse().getJsonURef())
                        .validatorPublicKey(bid.getBid().getValidatorPublicKey())
                        .delegators(mapper.writeValueAsString(bid.getBid().getDelegators()))
                        .stakedAmount(bid.getBid().getStakedAmount())
                        .delegationRate(bid.getBid().getDelegationRate())
                        .inactive(bid.getBid().isInactive())
                        .bidKey(entry.getKey())
                        .timestamp(Date.from(Instant.from(ZonedDateTime.parse(deployProcessed.getTimestamp()))))
                        .deployHash(deployProcessed.getDeployHash())
                        .vestingSchedule(mapper.writeValueAsString(bid.getBid().getVestingSchedule()))
                        .build();

                bids.add(deployBid);

            }

            if (entry.getTransform() instanceof final WriteWithdraw withdraws) {

                withdraws.getPurses().forEach(
                        p -> {
                            final Withdrawals withdrawal = Withdrawals.builder()
                                    .deployHash(deployProcessed.getDeployHash())
                                    .withdrawalKey(entry.getKey())
                                    .amount(p.getUnbondingAmount())
                                    .createdAt(Date.from(Instant.from((ZonedDateTime.now()))))
                                    .updatedAt(Date.from(Instant.from(ZonedDateTime.now())))
                                    .bondingPurse(p.getBondingPurse().getJsonURef())
                                    .eraOfCreation(p.getEraOfCreation())
                                    .timestamp(Date.from(Instant.from(ZonedDateTime.parse(deployProcessed.getTimestamp()))))
                                    .validatorPublicKey(p.getValidatorPublicKey())
                                    .ubonderPublicKey(p.getUnbonderPublicKey())
                                    .build();

                            withdrawals.add(withdrawal);

                        }

                );

            }

        }

        result.setTransfers(transfers);
        result.setBids(bids);
        result.setWithdrawals(withdrawals);

        return result;

    }

}
