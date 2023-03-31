package com.ontop.transfer.core.domain.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DestinationBankAccount {
    private String name;
    private String routingNumber;
    private String accountNumber;
    private String currency;
}
