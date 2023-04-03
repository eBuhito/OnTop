package com.ontop.transfer.core.application.port.outbound;

import com.ontop.transfer.core.domain.model.DestinationBankAccount;

public interface BankOutPort {
    DestinationBankAccount getDestinationBankAccountDetails(String bankAccount);
}
