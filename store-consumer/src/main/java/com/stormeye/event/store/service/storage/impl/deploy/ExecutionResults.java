package com.stormeye.event.store.service.storage.impl.deploy;


import com.stormeye.event.service.storage.domain.Bid;
import com.stormeye.event.service.storage.domain.Transfer;
import com.stormeye.event.service.storage.domain.Withdrawal;

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
    private List<Transfer> transfers;
    private List<Bid> bids;
    private List<Withdrawal> withdrawals;

}
