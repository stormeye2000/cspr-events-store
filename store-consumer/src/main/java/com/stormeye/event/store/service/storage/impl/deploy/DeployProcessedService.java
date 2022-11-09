package com.stormeye.event.store.service.storage.impl.deploy;

import org.jetbrains.annotations.NotNull;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Component;
import com.casper.sdk.model.common.Digest;
import com.casper.sdk.model.deploy.executionresult.ExecutionResult;
import com.casper.sdk.model.deploy.executionresult.Failure;
import com.casper.sdk.model.deploy.executionresult.Success;
import com.casper.sdk.model.event.deployprocessed.DeployProcessed;
import com.stormeye.event.exception.StoreConsumerException;
import com.stormeye.event.repository.DeployRepository;
import com.stormeye.event.service.storage.domain.Deploy;
import com.stormeye.event.store.service.storage.EventInfo;
import com.stormeye.event.store.service.storage.StorageFactory;
import com.stormeye.event.store.service.storage.StorageService;
import com.stormeye.event.store.service.storage.impl.common.TransactionalRunner;

import java.math.BigInteger;
import java.time.Instant;
import java.time.ZonedDateTime;
import java.util.Date;

@Component
public class DeployProcessedService implements StorageService<Deploy> {

    private final DeployRepository deployRepository;
    private final TransactionalRunner transactionalRunner;

    private record ExecutionDetails (BigInteger cost, String errorMessage) {}

    public DeployProcessedService(final DeployRepository deployRepository, final TransactionalRunner transactionalRunner, final StorageFactory storageFactory) {
        this.deployRepository = deployRepository;
        this.transactionalRunner = transactionalRunner;
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

        final ExecutionDetails details = getExecutionDetails(toStore.getExecutionResult());

        return deployRepository.save(new Deploy(
                toStore.getDeployHash(),
                new Digest(toStore.getBlockHash()),
                toStore.getAccount(),
                details.cost,
                details.errorMessage(),
                Date.from(Instant.from(ZonedDateTime.parse(toStore.getTimestamp()))),
                eventInfo.getId()
        ));

    }

    private ExecutionDetails getExecutionDetails(final ExecutionResult result){

        final ExecutionDetails details;

        if (result instanceof final Failure failure){
            details = new ExecutionDetails(failure.getCost(), failure.getErrorMessage());
        } else {
            final Success success = (Success) result;
            details = new ExecutionDetails(success.getCost(), null);
        }

        return details;

    }

}
