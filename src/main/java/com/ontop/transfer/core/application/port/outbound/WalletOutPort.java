package com.ontop.transfer.core.application.port.outbound;

import com.ontop.transfer.core.domain.model.Balance;

public interface WalletOutPort {
    Balance getBalance(long userId);
}
