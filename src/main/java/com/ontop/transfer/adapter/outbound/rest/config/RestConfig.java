package com.ontop.transfer.adapter.outbound.rest.config;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Configuration
@Getter
public class RestConfig {
    @Value(value = "${api.payment.url}")
    private String paymentApiUrl;
    @Value(value = "${api.payment.path}")
    private String paymentPath;

    @Value(value = "${api.wallet.url}")
    private String walletApiUrl;
    @Value(value = "${api.wallet.balance.path}")
    private String balancePath;
    @Value(value = "${api.wallet.transaction.path}")
    private String transactionPath;

}
