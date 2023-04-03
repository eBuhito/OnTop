package com.ontop.transfer.adapter.outbound.repository;

import com.ontop.transfer.core.domain.model.Transaction;
import com.ontop.transfer.core.domain.model.TransactionType;
import com.ontop.transfer.core.domain.model.TransferError;
import com.ontop.transfer.core.domain.model.TransferStatus;
import io.vavr.control.Either;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.DataAccessResourceFailureException;
import org.springframework.http.HttpStatus;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.support.KeyHolder;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class WalletTransactionRepositoryTest {
    @Mock
    NamedParameterJdbcTemplate jdbcTemplate;
    @Mock
    KeyHolderFactory keyHolderFactory;
    @Mock
    KeyHolder keyHolder;
    @InjectMocks
    private WalletTransactionRepository walletTransactionRepository;
    private Transaction transaction;

    @Test
    void successRegisterTransaction() {
        transaction = Transaction.builder()
                .userId(123L)
                .currency("USD")
                .status(TransferStatus.IN_PROGRESS)
                .type(TransactionType.TRANSFER)
                .discountTransactionId(987L)
                .amount(123.45)
                .build();
        when(keyHolderFactory.getGeneratedKeyHolder()).thenReturn(keyHolder);
        when(keyHolder.getKey()).thenReturn(1);
        when(jdbcTemplate.update(anyString(), any(MapSqlParameterSource.class), any(KeyHolder.class)))
                .thenReturn(1);

        Either<TransferError, Long> result = walletTransactionRepository.registerTransaction(transaction);

        assertTrue(result.isRight());
        assertEquals(1L, result.get().longValue());
    }

    @Test
    void failRegisterTransaction() {
        transaction = Transaction.builder()
                .userId(123L)
                .currency("USD")
                .status(TransferStatus.IN_PROGRESS)
                .type(TransactionType.TRANSFER)
                .discountTransactionId(987L)
                .amount(123.45)
                .build();
        when(keyHolderFactory.getGeneratedKeyHolder()).thenReturn(keyHolder);
        when(jdbcTemplate.update(anyString(), any(MapSqlParameterSource.class), any(KeyHolder.class)))
                .thenThrow(new DataAccessResourceFailureException("no hay transaccion"));

        Either<TransferError, Long> result = walletTransactionRepository.registerTransaction(transaction);

        assertTrue(result.isLeft());
        assertEquals(HttpStatus.UNPROCESSABLE_ENTITY, result.getLeft().getCode());
        assertEquals("no hay transaccion", result.getLeft().getError());
    }

}