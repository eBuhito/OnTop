package com.ontop.transfer.core.domain.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class WithdrawResponse {
    private String destinationBankAccountId;
    private long userId;
    private Double amount;
    private Double feeAmount;

    public static WithdrawResponse from(WithdrawRequest withdraw) {
        return new WithdrawResponse(
                withdraw.getDestinationBankAccountId(),
                withdraw.getUserId(),
                withdraw.getNetAmount(),
                withdraw.getGrossAmount() - withdraw.getNetAmount()
        );
    }
}
