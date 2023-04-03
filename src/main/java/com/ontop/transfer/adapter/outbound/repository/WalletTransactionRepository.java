package com.ontop.transfer.adapter.outbound.repository;

import com.ontop.transfer.core.application.port.outbound.TransactionOutPort;
import com.ontop.transfer.core.domain.model.Transaction;
import com.ontop.transfer.core.domain.model.TransferError;
import com.ontop.transfer.core.domain.model.TransferStatus;
import io.vavr.control.Either;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcOperations;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;

@Repository
public class WalletTransactionRepository implements TransactionOutPort {
    private static final String QUERY_INSERT_TRANSACTION =
            "INSERT INTO transactions " +
                    "(user_id, amount, currency, type, status, provider_payment_id, wallet_transaction_id, " +
                    "transaction_datetime) " +
                    "VALUES(:userId, :amount, :currency, :type, :status, :providerPaymentId, :walletTransactionId, " +
                    ":transactionDatetime)";
    private static final String QUERY_UPDATE_TRANSACTION =
            "UPDATE transactions " +
                    "SET status = :status, " +
                    "provider_payment_id = :provider_payment_id " +
                    "WHERE id = :id";
    private static final Logger logger = LoggerFactory.getLogger(WalletTransactionRepository.class);
    private final NamedParameterJdbcOperations jdbcTemplate;
    private final KeyHolderFactory keyHolderFactory;

    public WalletTransactionRepository(NamedParameterJdbcOperations jdbcTemplate, KeyHolderFactory keyHolderFactory) {
        this.jdbcTemplate = jdbcTemplate;
        this.keyHolderFactory = keyHolderFactory;
    }

    @Override
    public Either<TransferError, Long> registerTransaction(Transaction transaction) {
        logger.info("Start execution of query {} for transaction = {}",
                QUERY_INSERT_TRANSACTION, transaction);
        KeyHolder keyHolder = keyHolderFactory.getGeneratedKeyHolder();
        try {
            jdbcTemplate.update(
                    QUERY_INSERT_TRANSACTION,
                    new MapSqlParameterSource()
                            .addValue("userId", transaction.getUserId())
                            .addValue("amount", transaction.getAmount())
                            .addValue("currency", transaction.getCurrency())
                            .addValue("type", transaction.getType().name())
                            .addValue("status", transaction.getStatus().name())
                            .addValue("providerPaymentId", transaction.getProviderPaymentId())
                            .addValue("walletTransactionId", transaction.getDiscountTransactionId())
                            .addValue("transactionDatetime", LocalDateTime.now()),
                    keyHolder
            );
            return Either.right(keyHolder.getKey().longValue());
        } catch (Exception e) {
            logger.error("registerTransaction: Error on execution of query {} for transaction = {}", QUERY_INSERT_TRANSACTION, transaction, e);
            return Either.left(new TransferError(HttpStatus.UNPROCESSABLE_ENTITY, e.getMessage()));
        }
    }

    @Override
    public void updateTransaction(long transactionId, TransferStatus status, String paymentId) {
        logger.info("Start execution of query {} for transactionId = {}",
                QUERY_UPDATE_TRANSACTION, transactionId);

        jdbcTemplate.update(
                QUERY_UPDATE_TRANSACTION,
                new MapSqlParameterSource()
                        .addValue("id", transactionId)
                        .addValue("status", status.name())
                        .addValue("provider_payment_id", paymentId)
        );
    }
}
