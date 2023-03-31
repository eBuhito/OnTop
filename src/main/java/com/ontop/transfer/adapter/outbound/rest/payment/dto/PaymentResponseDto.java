package com.ontop.transfer.adapter.outbound.rest.payment.dto;

import com.ontop.transfer.core.domain.model.PaymentInfo;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PaymentResponseDto {
    private RequestInfoDto requestInfo;
    private PaymentInfoDto paymentInfo;

    public PaymentInfo toPaymentInfo() {
        return new PaymentInfo(paymentInfo.getAmount(), paymentInfo.getId());
    }
}
