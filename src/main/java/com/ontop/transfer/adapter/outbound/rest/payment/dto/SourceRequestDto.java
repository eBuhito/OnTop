package com.ontop.transfer.adapter.outbound.rest.payment.dto;

import com.ontop.transfer.core.domain.model.BankAccount;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SourceRequestDto {
    private String type;
    private SourceInformationDto sourceInformation;
    private AccountDto account;

    public static SourceRequestDto from(BankAccount source) {
        return new SourceRequestDto(
                source.getType(),
                SourceInformationDto.from(source.getName()),
                AccountDto.from(source.getAccount())
        );
    }
}
