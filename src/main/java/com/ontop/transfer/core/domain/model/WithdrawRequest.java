package com.ontop.transfer.core.domain.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class WithdrawRequest {
    private String destinationBankAccountId;
    private Long userId;
    private Double netAmount;
    private Double grossAmount;
}
