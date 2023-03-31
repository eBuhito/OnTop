package com.ontop.transfer.core.domain.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Transaction {
    private long userId;
    private double amount;
    private double feeAmount;
    private String status;
    private Long providerPaymentId;
    private long discountTransactionId;
}
