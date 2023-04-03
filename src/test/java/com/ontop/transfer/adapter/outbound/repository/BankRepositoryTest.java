package com.ontop.transfer.adapter.outbound.repository;

import com.ontop.transfer.adapter.outbound.repository.entity.BankAccountEntity;
import com.ontop.transfer.core.application.exception.BankAccountNotFoundException;
import com.ontop.transfer.core.domain.model.DestinationBankAccount;
import org.assertj.core.util.Lists;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.DataAccessResourceFailureException;
import org.springframework.http.HttpStatus;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class BankRepositoryTest {
    @Mock
    NamedParameterJdbcTemplate jdbcTemplate;

    @InjectMocks
    private BankRepository bankRepository;
    private String bankAccountId;

    @Test
    void successGetBankDetails() {
        bankAccountId = "333";
        List<BankAccountEntity> bankAccount = Lists.list(new BankAccountEntity(
                333L, "banco", "123456", "9876", "USD"
        ));
        when(jdbcTemplate.query(anyString(), any(MapSqlParameterSource.class), any(RowMapper.class)))
                .thenReturn(bankAccount);

        DestinationBankAccount result = bankRepository.getDestinationBankAccountDetails(bankAccountId);

        assertEquals("USD", result.getCurrency());
        assertEquals("banco", result.getName());
        assertEquals("9876", result.getAccountNumber());
        assertEquals("123456", result.getRoutingNumber());
    }

    @Test
    void throwBankNotFoundExceptionWhenBankDoesntExist() {
        bankAccountId = "333";
        when(jdbcTemplate.query(anyString(), any(MapSqlParameterSource.class), any(RowMapper.class)))
                .thenReturn(new ArrayList<>());
//                .thenThrow(new DataAccessResourceFailureException("no existe"));

        BankAccountNotFoundException exception = assertThrows(BankAccountNotFoundException.class, () -> {
            bankRepository.getDestinationBankAccountDetails(bankAccountId);
        });

        assertEquals(HttpStatus.BAD_REQUEST, exception.getError().getCode());
        assertEquals("Bank account not found", exception.getError().getError());
    }

    @Test
    void throwBankNotFoundExceptionWhenGetBankDetailsFails() {
        bankAccountId = "333";
        when(jdbcTemplate.query(anyString(), any(MapSqlParameterSource.class), any(RowMapper.class)))
                .thenThrow(new DataAccessResourceFailureException("no existe"));

        BankAccountNotFoundException exception = assertThrows(BankAccountNotFoundException.class, () -> {
            bankRepository.getDestinationBankAccountDetails(bankAccountId);
        });

        assertEquals(HttpStatus.BAD_REQUEST, exception.getError().getCode());
        assertEquals("Bank account not found", exception.getError().getError());
    }

}