package com.ontop.transfer.adapter.outbound.rest.payment.dto;

import com.ontop.transfer.core.domain.model.BankAccount;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DestinationRequestDto {
    private String name;
    private AccountDto account;

    public static DestinationRequestDto from(BankAccount destination) {
        return new DestinationRequestDto(
                destination.getName(), AccountDto.from(destination.getAccount())
        );
    }
}
