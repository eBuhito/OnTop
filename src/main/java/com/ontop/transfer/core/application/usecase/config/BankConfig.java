package com.ontop.transfer.core.application.usecase.config;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Configuration
@Getter
public class BankConfig {
    @Value(value = "${withdraw.fee}")
    private double percentageFee;
    @Value(value = "${source.type}")
    private String sourceType;
    @Value(value = "${source.name}")
    private String sourceName;
    @Value(value = "${source.account.number}")
    private String sourceAccountNumber;
    @Value(value = "${source.account.currency}")
    private String sourceAccountCurrency;
    @Value(value = "${source.account.routing-number}")
    private String sourceAccountRoutingNumber;

}
