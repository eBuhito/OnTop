package com.ontop.transfer.core.domain.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class WithdrawRequest {
    private String destinationBankAccountId;
    private long userId;
    private double amount;
}
