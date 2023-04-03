package com.ontop.transfer.core.application.port.outbound;

import com.ontop.transfer.core.domain.model.Balance;
import com.ontop.transfer.core.domain.model.TransferError;
import io.vavr.control.Either;

public interface WalletOutPort {
    Either<TransferError, Balance> getBalance(long userId);
    Either<TransferError, Long> moveFunds(long userId, double amount);
}
