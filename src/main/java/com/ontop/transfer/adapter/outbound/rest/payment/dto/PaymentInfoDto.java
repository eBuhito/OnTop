package com.ontop.transfer.adapter.outbound.rest.payment.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PaymentInfoDto {
    private double amount;
    private String id;
}
