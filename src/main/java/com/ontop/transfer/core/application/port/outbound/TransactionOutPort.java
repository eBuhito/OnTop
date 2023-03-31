package com.ontop.transfer.core.application.port.outbound;

import com.ontop.transfer.core.domain.model.Transaction;
import com.ontop.transfer.core.domain.model.WithdrawRequest;

public interface TransactionOutPort {
    long registerTransaction(Transaction transaction);

    void updateTransaction(long transactionId, String status);
}
