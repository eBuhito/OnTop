package com.ontop.transfer.adapter.outbound.rest.wallet.balance.dto;

import com.ontop.transfer.core.domain.model.Balance;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class BalanceResponseDto {
    private double balance;
    private long userId;

    public Balance toDomain() {
        return new Balance(userId, balance);
    }
}
