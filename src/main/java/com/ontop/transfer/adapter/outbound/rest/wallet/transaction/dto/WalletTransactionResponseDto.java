package com.ontop.transfer.adapter.outbound.rest.wallet.transaction.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class WalletTransactionResponseDto {
    private long walletTransactionId;
    private long userId;
    private double amount;
}
