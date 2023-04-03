package com.ontop.transfer.core.application.port.outbound;

import com.ontop.transfer.core.domain.model.Transaction;
import com.ontop.transfer.core.domain.model.TransferError;
import com.ontop.transfer.core.domain.model.TransferStatus;
import io.vavr.control.Either;

public interface TransactionOutPort {
    Either<TransferError, Long> registerTransaction(Transaction transaction);

    void updateTransaction(long transactionId, TransferStatus status, String paymentId);
}
