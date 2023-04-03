package com.ontop.transfer.core.application.port.outbound;

import com.ontop.transfer.core.domain.model.BankAccount;
import com.ontop.transfer.core.domain.model.PaymentInfo;
import com.ontop.transfer.core.domain.model.TransferError;
import io.vavr.control.Either;

public interface PaymentOutPort {
    Either<TransferError, PaymentInfo> executePayment(BankAccount source, BankAccount destination, double netAmount);
}
