package com.stormeye.event.store.service.storage.impl.deploy;


import com.stormeye.event.service.storage.domain.Bids;
import com.stormeye.event.service.storage.domain.Transfers;
import com.stormeye.event.service.storage.domain.Withdrawals;

import java.math.BigInteger;
import java.util.List;
import lombok.*;

/**
 * Holds the DeployProcessed event execution results
 */
@Getter
@Setter
@AllArgsConstructor
@Builder
public class ExecutionResults {
    @NonNull
    private BigInteger cost;
    private String errorMessage;
    private List<Transfers> transfers;
    private List<Bids> bids;
    private List<Withdrawals> withdrawals;

}
