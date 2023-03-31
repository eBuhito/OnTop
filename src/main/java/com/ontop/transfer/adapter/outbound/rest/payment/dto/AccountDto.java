package com.ontop.transfer.adapter.outbound.rest.payment.dto;

import com.ontop.transfer.core.domain.model.Account;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AccountDto {
    private String accountNumber;
    private String currency;
    private String routingNumber;

    public static AccountDto from(Account account) {
        return new AccountDto(
                String.valueOf(account.getAccountNumber()),
                account.getCurrency(),
                String.valueOf(account.getRoutingNumber())
        );
    }
}
