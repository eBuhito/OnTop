package com.ontop.transfer.adapter.outbound.rest.wallet.dto.balance;

import com.ontop.transfer.core.domain.model.Balance;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BalanceResponseDto {
    private double balance;
    private long userId;

    public Balance toDomain() {
        return new Balance(userId, balance);
    }
}
