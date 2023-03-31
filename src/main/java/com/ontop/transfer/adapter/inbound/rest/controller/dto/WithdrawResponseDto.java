package com.ontop.transfer.adapter.inbound.rest.controller.dto;

import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import com.ontop.transfer.core.domain.model.WithdrawResponse;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
public class WithdrawResponseDto {
    private String destinationBankAccountId;
    private long userId;
    private Double amount;
    private Double feeAmount;

    public static WithdrawResponseDto from(WithdrawResponse withdraw) {
        return new WithdrawResponseDto(
                withdraw.getDestinationBankAccountId(),
                withdraw.getUserId(),
                withdraw.getAmount(),
                withdraw.getFeeAmount()
        );
    }
}
