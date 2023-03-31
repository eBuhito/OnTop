package com.ontop.transfer.core.application.port.outbound;

import com.ontop.transfer.core.domain.model.BankAccount;
import com.ontop.transfer.core.domain.model.PaymentInfo;

public interface PaymentOutPort {
    PaymentInfo executePayment(BankAccount source, BankAccount destination, double netAmount);
}
