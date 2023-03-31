package com.ontop.transfer.adapter.outbound.rest.payment.dto;

import com.ontop.transfer.core.domain.model.BankAccount;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SourceInformationDto {
    private String name;

    public static SourceInformationDto from(String name) {
        return new SourceInformationDto(name);
    }
}
