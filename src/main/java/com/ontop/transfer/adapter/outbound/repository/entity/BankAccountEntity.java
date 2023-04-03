package com.ontop.transfer.adapter.outbound.repository.entity;

import com.ontop.transfer.core.domain.model.DestinationBankAccount;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class BankAccountEntity {
    private long id;
    private String name;
    private String routingNumber;
    private String accountNumber;
    private String currency;

    public DestinationBankAccount toDomain() {
        return new DestinationBankAccount(
                name,
                routingNumber,
                accountNumber,
                currency
        );
    }
}
