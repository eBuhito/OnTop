package com.ontop.transfer.adapter.inbound.rest.controller.dto;

import com.ontop.transfer.core.domain.model.WithdrawResponse;
import lombok.experimental.UtilityClass;

@UtilityClass
public class WithdrawResponseBuilder {
    public static WithdrawResponseDto build(WithdrawResponse withdrawResponse) {
        return new WithdrawResponseDto(
                withdrawResponse.getDestinationBankAccountId(),
                withdrawResponse.getUserId(),
                withdrawResponse.getAmount(),
                withdrawResponse.getFeeAmount()
        );
    }
}
