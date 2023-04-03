package com.ontop.transfer.adapter.outbound.rest.wallet.dto.transaction;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class FundsRequestDto {
    private long userId;
    private double amount;

    public static FundsRequestDto from(long userId, double amount) {
        return new FundsRequestDto(userId, amount);
    }
}
