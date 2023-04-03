package com.ontop.transfer.adapter.inbound.rest.controller.dto;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import com.ontop.transfer.core.domain.model.WithdrawRequest;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;

@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class WithdrawRequestDto {
    @NotNull(message = "destination_bank_account_id field is required.")
    private String destinationBankAccountId;
    @NotNull(message = "user_id field is required.")
    private Long userId;
    @NotNull(message = "amount field is required.")
    private Double amount;

    public WithdrawRequest toWithdraw() {
        return new WithdrawRequest(destinationBankAccountId, userId, amount, null);
    }
}
