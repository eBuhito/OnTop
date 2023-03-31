package com.ontop.transfer.adapter.inbound.rest.controller.dto;

import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import com.ontop.transfer.core.domain.model.WithdrawRequest;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
public class WithdrawRequestDto {
    private String destinationBankAccountId;
    private long userId;
    private double amount;

    public WithdrawRequest toDomain() {
        return new WithdrawRequest(destinationBankAccountId, userId, amount);
    }
}
