package com.ontop.transfer.adapter.outbound.rest.wallet;


import com.ontop.transfer.adapter.outbound.rest.config.RestConfig;
import com.ontop.transfer.adapter.outbound.rest.wallet.dto.balance.BalanceResponseDto;
import com.ontop.transfer.adapter.outbound.rest.wallet.dto.transaction.FundsRequestDto;
import com.ontop.transfer.adapter.outbound.rest.wallet.dto.transaction.WalletTransactionResponseDto;
import com.ontop.transfer.core.domain.model.Balance;
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
class WalletClientTest {

    @Mock
    private RestTemplate restTemplate;
    @Mock
    private RestConfig config;

    @InjectMocks
    private WalletClient walletClient;

    private HttpEntity<?> buildEntity() {
        final HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        return new HttpEntity<>(headers);
    }

    private HttpEntity<?> buildTransactionEntity(FundsRequestDto fundsRequestDto) {
        final HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        return new HttpEntity<>(fundsRequestDto, headers);
    }

    @Test
    void successGetBalance() {
        long userId = 333;
        BalanceResponseDto balanceResponseDto = BalanceResponseDto.builder()
                .balance(123.45)
                .userId(333)
                .build();
        when(config.getWalletApiUrl()).thenReturn("http://localhost:9999");
        when(config.getBalancePath()).thenReturn("/testing/balance");
        when(restTemplate.exchange(
                "http://localhost:9999/testing/balance",
                HttpMethod.GET,
                buildEntity(),
                BalanceResponseDto.class)
        ).thenReturn(ResponseEntity.ok(balanceResponseDto));

        Either<TransferError, Balance> result = walletClient.getBalance(userId);

        assertTrue(result.isRight());
        assertEquals(123.45, result.get().getBalance());
        assertEquals(333, result.get().getUserId());
    }

    @Test
    void failGetBalanceWithHttpStatusCodeException() {
        long userId = 333;
        byte[] responseBody = "usuario no existe".getBytes();
        when(config.getWalletApiUrl()).thenReturn("http://localhost:9999");
        when(config.getBalancePath()).thenReturn("/testing/balance");
        when(restTemplate.exchange(
                "http://localhost:9999/testing/balance",
                HttpMethod.GET,
                buildEntity(),
                BalanceResponseDto.class)
        ).thenThrow(new HttpClientErrorException(HttpStatus.NOT_FOUND, HttpStatus.NOT_FOUND.getReasonPhrase(), responseBody, null));

        Either<TransferError, Balance> result = walletClient.getBalance(userId);

        assertTrue(result.isLeft());
        assertEquals(HttpStatus.NOT_FOUND, result.getLeft().getCode());
        assertEquals("usuario no existe", result.getLeft().getError());
    }

    @Test
    void failGetBalanceWithOtherException() {
        long userId = 333;
        when(config.getWalletApiUrl()).thenReturn("http://localhost:9999");
        when(config.getBalancePath()).thenReturn("/testing/balance");
        when(restTemplate.exchange(
                "http://localhost:9999/testing/balance",
                HttpMethod.GET,
                buildEntity(),
                BalanceResponseDto.class)
        ).thenThrow(new RuntimeException("otro error"));

        Either<TransferError, Balance> result = walletClient.getBalance(userId);

        assertTrue(result.isLeft());
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, result.getLeft().getCode());
        assertEquals("otro error", result.getLeft().getError());
    }

    @Test
    void successMoveFunds() {
        long userId = 333;
        double amount = 123.45;
        WalletTransactionResponseDto walletTransactionResponseDto = WalletTransactionResponseDto.builder()
                .walletTransactionId(55555)
                .amount(123.45)
                .userId(333)
                .build();
        when(config.getWalletApiUrl()).thenReturn("http://localhost:9999");
        when(config.getTransactionPath()).thenReturn("/testing/transaction");
        when(restTemplate.exchange(
                "http://localhost:9999/testing/transaction",
                HttpMethod.POST,
                buildTransactionEntity(FundsRequestDto.from(userId, amount)),
                WalletTransactionResponseDto.class)
        ).thenReturn(ResponseEntity.ok(walletTransactionResponseDto));

        Either<TransferError, Long> result = walletClient.moveFunds(userId, amount);

        assertTrue(result.isRight());
        assertEquals(55555, result.get().longValue());
    }

    @Test
    void failMoveFundsWithHttpStatusCodeException() {
        long userId = 333;
        double amount = 123.45;
        byte[] responseBody = "usuario no existe".getBytes();
        when(config.getWalletApiUrl()).thenReturn("http://localhost:9999");
        when(config.getTransactionPath()).thenReturn("/testing/transaction");
        when(restTemplate.exchange(
                "http://localhost:9999/testing/transaction",
                HttpMethod.POST,
                buildTransactionEntity(FundsRequestDto.from(userId, amount)),
                WalletTransactionResponseDto.class)
        ).thenThrow(new HttpClientErrorException(HttpStatus.NOT_FOUND, HttpStatus.NOT_FOUND.getReasonPhrase(), responseBody, null));

        Either<TransferError, Long> result = walletClient.moveFunds(userId, amount);

        assertTrue(result.isLeft());
        assertEquals(HttpStatus.NOT_FOUND, result.getLeft().getCode());
        assertEquals("usuario no existe", result.getLeft().getError());
    }

    @Test
    void failMoveFundsWithOtherException() {
        long userId = 333;
        double amount = 123.45;
        when(config.getWalletApiUrl()).thenReturn("http://localhost:9999");
        when(config.getTransactionPath()).thenReturn("/testing/transaction");
        when(restTemplate.exchange(
                "http://localhost:9999/testing/transaction",
                HttpMethod.POST,
                buildTransactionEntity(FundsRequestDto.from(userId, amount)),
                WalletTransactionResponseDto.class)
        ).thenThrow(new RuntimeException("otro error"));

        Either<TransferError, Long> result = walletClient.moveFunds(userId, amount);

        assertTrue(result.isLeft());
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, result.getLeft().getCode());
        assertEquals("otro error", result.getLeft().getError());
    }
}