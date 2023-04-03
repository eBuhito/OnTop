package com.ontop.transfer.core.domain.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class WithdrawRequested {
    private String destinationBankAccountId;
    private long userId;
    private Double amount;
}
