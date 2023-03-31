package com.ontop.transfer.adapter.outbound.rest.payment.dto;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import com.ontop.transfer.core.domain.model.BankAccount;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonNaming(PropertyNamingStrategies.LowerCamelCaseStrategy.class)
public class PaymentRequestDto {
    private SourceRequestDto source;
    private DestinationRequestDto destination;
    private double amount;

    public static PaymentRequestDto from(BankAccount source, BankAccount destination, double netAmount) {
        return new PaymentRequestDto(
                SourceRequestDto.from( source),
                DestinationRequestDto.from(destination),
                netAmount);
    }
}
