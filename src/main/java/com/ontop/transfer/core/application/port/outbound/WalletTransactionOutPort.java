package com.ontop.transfer.core.application.port.outbound;

public interface WalletTransactionOutPort {
    long discountFunds(long userId, double amount);
}
