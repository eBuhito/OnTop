package com.ontop.transfer.adapter.outbound.rest.wallet.transaction.dto;

import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
public class DiscountRequestDto {
    private long userId;
    private double amount;

    public static DiscountRequestDto from(long userId, double amount) {
        return new DiscountRequestDto(userId, amount);
    }
}
