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
import com.casper.sdk.model.event.deployprocessed.DeployProcessed;
import com.fasterxml.jackson.core.TreeNode;
import com.stormeye.event.exception.StoreConsumerException;
import com.stormeye.event.repository.DeployBidRepository;
import com.stormeye.event.repository.DeployRepository;
import com.stormeye.event.repository.TransferRepository;
import com.stormeye.event.service.storage.domain.Deploy;
import com.stormeye.event.service.storage.domain.DeployBid;
import com.stormeye.event.service.storage.domain.Transfer;
import com.stormeye.event.store.service.storage.EventInfo;
import com.stormeye.event.store.service.storage.StorageFactory;
import com.stormeye.event.store.service.storage.StorageService;
import com.stormeye.event.store.service.storage.impl.common.TransactionalRunner;

import java.time.Instant;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Component
public class DeployProcessedService implements StorageService<Deploy> {

    private final DeployRepository deployRepository;
    private final TransferRepository transferRepository;
    private final DeployBidRepository deployBidRepository;
    private final TransactionalRunner transactionalRunner;

    public DeployProcessedService(final DeployRepository deployRepository, final TransactionalRunner transactionalRunner, final StorageFactory storageFactory, final TransferRepository transferRepository, final DeployBidRepository deployBidRepository) {
        this.deployRepository = deployRepository;
        this.transactionalRunner = transactionalRunner;
        this.transferRepository = transferRepository;
        this.deployBidRepository = deployBidRepository;
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

        final ExecutionResults result = getExecutionDetails(toStore, eventInfo);

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
        deployBidRepository.saveAll(result.getDeployBids());

        return deploy;

    }

    private ExecutionResults getExecutionDetails(final DeployProcessed deployProcessed, final EventInfo eventInfo){

        final ExecutionResults result;

        if (deployProcessed.getExecutionResult() instanceof final Failure failure){
            result = ExecutionResults.builder()
                                    .cost(failure.getCost())
                                    .errorMessage(failure.getErrorMessage()).build();

        } else {
            final Success success = (Success) deployProcessed.getExecutionResult();

            result = ExecutionResults.builder()
                    .cost(success.getCost()).build();

            final List<Entry> transforms = ((Success) deployProcessed.getExecutionResult()).getEffect().getTransforms();

            List<Transfer> transfers = new ArrayList<>();
            List<DeployBid> deployBids = new ArrayList<>();

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

                if (entry.getTransform() instanceof final WriteBid bid) {

                    final TreeNode delegators = eventInfo.getJsonData().at("/DeployProcessed/execution_result/Success/effect/transforms/transform/WriteBid/delegators");

                    final DeployBid deployBid = DeployBid.builder()
                            .bondingPurse(bid.getBid().getBondingPurse().toString())
                            .validatorPublicKey(bid.getBid().getValidatorPublicKey())
                            .delegators(bid.getBid().getDelegators().toString())
                            .stakedAmount(bid.getBid().getStakedAmount())
                            .delegationRate(bid.getBid().getDelegationRate())
                            .inactive(bid.getBid().isInactive())
                            .key(entry.getKey())
                            .timestamp(Date.from(Instant.from(ZonedDateTime.parse(deployProcessed.getTimestamp()))))
                            .deployHash(deployProcessed.getDeployHash())
                            .vestingSchedule(bid.getBid().getVestingSchedule()).build();

                    deployBids.add(deployBid);

                }

            }

            result.setTransfers(transfers);
            result.setDeployBids(deployBids);

        }

        return result;

    }

}
