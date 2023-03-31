package com.ontop.transfer.adapter.outbound.rest.payment.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PaymentInfoDto {
    private double amount;
    private String id;
}
