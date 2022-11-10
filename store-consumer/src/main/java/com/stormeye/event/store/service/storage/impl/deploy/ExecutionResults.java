package com.stormeye.event.store.service.storage.impl.deploy;


import com.stormeye.event.service.storage.domain.DeployBid;
import com.stormeye.event.service.storage.domain.Transfer;

import java.math.BigInteger;
import java.util.List;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@Builder
public class ExecutionResults {
    @NonNull
    private BigInteger cost;
    private String errorMessage;
    private List<Transfer> transfers;
    private List<DeployBid> deployBids;

}
