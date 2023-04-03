package com.ontop.transfer.core.domain.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Transaction {
    private long userId;
    private double amount;
    private String currency;
    private TransactionType type;
    private TransferStatus status;
    private Long providerPaymentId;
    private long discountTransactionId;
}
