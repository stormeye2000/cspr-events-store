package com.stormeye.event.store.service.storage.impl.deploy;

import org.jetbrains.annotations.NotNull;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Component;
import com.casper.sdk.model.common.Digest;
import com.casper.sdk.model.deploy.Entry;
import com.casper.sdk.model.deploy.executionresult.Failure;
import com.casper.sdk.model.deploy.executionresult.Success;
import com.casper.sdk.model.deploy.transform.WriteTransfer;
import com.casper.sdk.model.event.deployprocessed.DeployProcessed;
import com.stormeye.event.exception.StoreConsumerException;
import com.stormeye.event.repository.DeployRepository;
import com.stormeye.event.repository.TransferRepository;
import com.stormeye.event.service.storage.domain.Deploy;
import com.stormeye.event.service.storage.domain.Transfer;
import com.stormeye.event.store.service.storage.EventInfo;
import com.stormeye.event.store.service.storage.StorageFactory;
import com.stormeye.event.store.service.storage.StorageService;
import com.stormeye.event.store.service.storage.impl.common.TransactionalRunner;

import java.math.BigInteger;
import java.time.Instant;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Component
public class DeployProcessedService implements StorageService<Deploy> {

    private final DeployRepository deployRepository;
    private final TransferRepository transferRepository;
    private final TransactionalRunner transactionalRunner;

    private record ExecutionDetails (BigInteger cost, String errorMessage) {}

    public DeployProcessedService(final DeployRepository deployRepository, final TransactionalRunner transactionalRunner, final StorageFactory storageFactory, final TransferRepository transferRepository) {
        this.deployRepository = deployRepository;
        this.transactionalRunner = transactionalRunner;
        this.transferRepository = transferRepository;
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
    private Deploy storeDeploy(final EventInfo eventInfo) {
        final DeployProcessed toStore = (DeployProcessed) eventInfo.getData();

        final Result result = getExecutionDetails(toStore);

        final Deploy deploy = deployRepository.save(new Deploy(
                toStore.getDeployHash(),
                new Digest(toStore.getBlockHash()),
                toStore.getAccount(),
                result.getCost(),
                result.getErrorMessage(),
                Date.from(Instant.from(ZonedDateTime.parse(toStore.getTimestamp()))),
                eventInfo.getId()
        ));

        transferRepository.saveAll(result.getTransfers());

        return deploy;

    }

    private Result getExecutionDetails(final DeployProcessed deployProcessed){

        final Result result;

        if (deployProcessed.getExecutionResult() instanceof final Failure failure){
            result = new Result(failure.getCost(), failure.getErrorMessage());
        } else {
            final Success success = (Success) deployProcessed.getExecutionResult();
            result = new Result(success.getCost());

            final List<Entry> transforms = ((Success) deployProcessed.getExecutionResult()).getEffect().getTransforms();

            List<Transfer> transfers = new ArrayList<>();

            for (Entry entry: transforms){

                if (entry.getTransform() instanceof final WriteTransfer writeTransfer){

                    Transfer transfer = new Transfer(
                            writeTransfer.getTransfer().getId(),
                            new Digest(entry.getKey().substring(9)),
                            new Digest(writeTransfer.getTransfer().getDeployHash()),
                            new Digest(deployProcessed.getBlockHash()),
                            new Digest(writeTransfer.getTransfer().getFrom().substring(13)),
                            new Digest(writeTransfer.getTransfer().getTo().substring(13)),
                            writeTransfer.getTransfer().getSource(),
                            writeTransfer.getTransfer().getTarget(),
                            writeTransfer.getTransfer().getAmount(),
                            Date.from(Instant.from(ZonedDateTime.parse(deployProcessed.getTimestamp())))
                    );

                    transfers.add(transfer);

                }

            }

            result.setTransfers(transfers);

        }

        return result;

    }

    static class Result {
        private BigInteger cost;
        private String errorMessage;
        private List<Transfer> transfers;

        public Result() {}
        public Result(final BigInteger cost, final String errorMessage, final List<Transfer> transfers) {
            this.cost = cost;
            this.errorMessage = errorMessage;
            this.transfers = transfers;
        }

        public Result(final BigInteger cost, final String errorMessage) {
            this.cost = cost;
            this.errorMessage = errorMessage;
        }

        public Result(final BigInteger cost) {
            this.cost = cost;
        }

        public BigInteger getCost() {
            return cost;
        }
        public String getErrorMessage() {
            return errorMessage;
        }
        public List<Transfer> getTransfers() {
            return transfers;
        }

        public void setTransfers(final List<Transfer> transfers) {
            this.transfers = transfers;
        }
    }
}
