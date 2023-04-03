package com.ontop.transfer.adapter.outbound.rest.payment;

import com.ontop.transfer.adapter.outbound.rest.config.RestConfig;
import com.ontop.transfer.adapter.outbound.rest.payment.dto.PaymentInfoDto;
import com.ontop.transfer.adapter.outbound.rest.payment.dto.PaymentRequestDto;
import com.ontop.transfer.adapter.outbound.rest.payment.dto.PaymentResponseDto;
import com.ontop.transfer.adapter.outbound.rest.payment.dto.RequestInfoDto;
import com.ontop.transfer.core.domain.model.Account;
import com.ontop.transfer.core.domain.model.BankAccount;
import com.ontop.transfer.core.domain.model.PaymentInfo;
import com.ontop.transfer.core.domain.model.TransferError;
import io.vavr.control.Either;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.*;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PaymentClientTest {

    @Mock
    private RestTemplate restTemplate;
    @Mock
    private RestConfig config;

    @InjectMocks
    private PaymentClient paymentClient;

    private static RequestInfoDto buildSuccessRequestInfo() {
        return RequestInfoDto.builder()
                .status("Processing")
                .build();
    }

    private static PaymentInfoDto buildPaymentInfo() {
        return PaymentInfoDto.builder()
                .id("algun-token-de-id")
                .amount(123.45)
                .build();
    }

    private static BankAccount buildDestination(Account destAccount) {
        return BankAccount.builder()
                .name("Banco destino")
                .type("Personal")
                .account(destAccount)
                .build();
    }

    private static Account buildDestAccount() {
        return Account.builder()
                .routingNumber("999999999")
                .currency("USD")
                .accountNumber("333")
                .build();
    }

    private static BankAccount buildSource(Account sourceAccount) {
        return BankAccount.builder()
                .account(sourceAccount)
                .name("Banco origen")
                .type("Empresa")
                .build();
    }

    private static Account buildSourceAccount() {
        return Account.builder()
                .accountNumber("55555")
                .currency("USD")
                .routingNumber("666666")
                .build();
    }

    private HttpEntity<PaymentRequestDto> buildEntity(BankAccount source, BankAccount destination, double netAmount) {
        return new HttpEntity<>(PaymentRequestDto.from(source, destination, netAmount), buildHeaders());
    }

    private HttpHeaders buildHeaders() {
        final HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        return headers;
    }

    @Test
    void successPayment() {
        Account sourceAccount = buildSourceAccount();
        BankAccount source = buildSource(sourceAccount);
        Account destAccount = buildDestAccount();
        BankAccount destination = buildDestination(destAccount);
        double netAmount = 123.45;
        PaymentInfoDto paymentInfo = buildPaymentInfo();
        RequestInfoDto requestInfo = buildSuccessRequestInfo();
        PaymentResponseDto paymentResponseDto = PaymentResponseDto.builder()
                .paymentInfo(paymentInfo)
                .requestInfo(requestInfo)
                .build();
        when(config.getPaymentApiUrl()).thenReturn("http://localhost:9999");
        when(config.getPaymentPath()).thenReturn("/testing/payment");
        when(restTemplate.exchange(
                "http://localhost:9999/testing/payment",
                HttpMethod.POST,
                buildEntity(source, destination, netAmount),
                PaymentResponseDto.class)
        ).thenReturn(ResponseEntity.ok(paymentResponseDto));

        Either<TransferError, PaymentInfo> result = paymentClient.executePayment(source, destination, netAmount);

        assertTrue(result.isRight());
        PaymentInfo resultedPaymentInfo = result.get();
        assertEquals(123.45, resultedPaymentInfo.getAmount());
        assertEquals("algun-token-de-id", resultedPaymentInfo.getId());
    }

    @Test
    void failPaymentWithHttpStatusCodeException() {
        BankAccount source = buildSource(buildSourceAccount());
        BankAccount destination = buildDestination(buildDestAccount());
        double netAmount = 123.45;
        when(config.getPaymentApiUrl()).thenReturn("http://localhost:9999");
        when(config.getPaymentPath()).thenReturn("/testing/payment");
        byte[] responseBody = "error al pagar".getBytes();
        when(restTemplate.exchange(
                "http://localhost:9999/testing/payment",
                HttpMethod.POST,
                buildEntity(source, destination, netAmount),
                PaymentResponseDto.class)
        ).thenThrow(new HttpClientErrorException(HttpStatus.BAD_REQUEST, HttpStatus.BAD_REQUEST.getReasonPhrase(), responseBody, null));

        Either<TransferError, PaymentInfo> result = paymentClient.executePayment(source, destination, netAmount);

        assertTrue(result.isLeft());
        TransferError expectedTransferError = result.getLeft();
        assertEquals(HttpStatus.BAD_REQUEST, expectedTransferError.getCode());
        assertEquals("error al pagar", expectedTransferError.getError());
    }

    @Test
    void failPaymentWithOtherException() {
        BankAccount source = buildSource(buildSourceAccount());
        BankAccount destination = buildDestination(buildDestAccount());
        double netAmount = 123.45;
        when(config.getPaymentApiUrl()).thenReturn("http://localhost:9999");
        when(config.getPaymentPath()).thenReturn("/testing/payment");
        when(restTemplate.exchange(
                "http://localhost:9999/testing/payment",
                HttpMethod.POST,
                buildEntity(source, destination, netAmount),
                PaymentResponseDto.class)
        ).thenThrow(new RuntimeException("otro error"));

        Either<TransferError, PaymentInfo> result = paymentClient.executePayment(source, destination, netAmount);

        assertTrue(result.isLeft());
        TransferError expectedTransferError = result.getLeft();
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, expectedTransferError.getCode());
        assertEquals("otro error", expectedTransferError.getError());
    }
}