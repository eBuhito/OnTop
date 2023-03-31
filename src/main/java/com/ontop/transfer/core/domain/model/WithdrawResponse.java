package com.ontop.transfer.core.domain.model;

import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
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

    public static WithdrawResponse from(WithdrawRequest withdrawRequest, double feeAmount) {
        return new WithdrawResponse(
                withdrawRequest.getDestinationBankAccountId(),
                withdrawRequest.getUserId(),
                withdrawRequest.getAmount(),
                feeAmount
        );
    }
}
