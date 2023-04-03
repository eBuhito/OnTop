package com.ontop.transfer.adapter.outbound.repository;

import com.ontop.transfer.adapter.outbound.repository.entity.BankAccountEntity;
import com.ontop.transfer.core.application.exception.BankAccountNotFoundException;
import com.ontop.transfer.core.application.port.outbound.BankOutPort;
import com.ontop.transfer.core.domain.model.DestinationBankAccount;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcOperations;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;

@Repository
public class BankRepository implements BankOutPort {
    private static final String QUERY_SELECT_BANK_ACCOUNT_DETAILS =
            "SELECT * FROM bank_accounts " +
                    "WHERE id = :accountId";

    private static final Logger logger = LoggerFactory.getLogger(BankRepository.class);
    private final NamedParameterJdbcOperations jdbcTemplate;

    public BankRepository(NamedParameterJdbcOperations jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public DestinationBankAccount getDestinationBankAccountDetails(String bankAccountId) {
        logger.info("Start execution of query {} for id = {}",
                QUERY_SELECT_BANK_ACCOUNT_DETAILS, bankAccountId);

        try {
            return jdbcTemplate.query(
                            QUERY_SELECT_BANK_ACCOUNT_DETAILS,
                            new MapSqlParameterSource()
                                    .addValue("accountId", bankAccountId),
                            new BankAccountRowMapper())
                    .stream()
                    .map(BankAccountEntity::toDomain)
                    .findFirst()
                    .orElseThrow(BankAccountNotFoundException::new);
        } catch (Exception e){
            logger.error("getDestinationBankAccountDetails: Error on execution of query {} for id = {}",
                    QUERY_SELECT_BANK_ACCOUNT_DETAILS, bankAccountId);
            throw new BankAccountNotFoundException();
        }
    }

    private static class BankAccountRowMapper implements RowMapper<BankAccountEntity> {
        @Override
        public BankAccountEntity mapRow(ResultSet rs, int rowNum) throws SQLException {
            return new BankAccountEntity(
                    rs.getLong("id"),
                    rs.getString("name"),
                    rs.getString("routing_number"),
                    rs.getString("account_number"),
                    rs.getString("currency")
            );
        }
    }
}
