package com.ontop.transfer.adapter.outbound.repository;

import com.ontop.transfer.core.application.port.outbound.TransactionOutPort;
import com.ontop.transfer.core.domain.model.Transaction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcOperations;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

@Repository
public class WalletTransactionRepository implements TransactionOutPort {
    private static final String QUERY_INSERT_TRANSACTION =
            "INSERT INTO transactions " +
                    "(user_id, amount, amount_fee, status, provider_payment_id, wallet_transaction_id, " +
                    "transaction_datetime) " +
                    "VALUES(:userId, :amount, :feeAmount, :status, :providerPaymentId, :walletTransactionId, " +
                    ":transactionDatetime)";
    private static final String QUERY_UPDATE_TRANSACTION =
            "UPDATE transactions " +
                    "SET status = :status, " +
                    "provider_payment_id = : provider_payment_id " +
                    "WHERE id = :id";
    private static final Logger logger = LoggerFactory.getLogger(WalletTransactionRepository.class);
    private final NamedParameterJdbcOperations jdbcTemplate;

    public WalletTransactionRepository(NamedParameterJdbcOperations jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public long registerTransaction(Transaction transaction) {
        logger.info("Start execution of query {} for transaction = {}",
                QUERY_INSERT_TRANSACTION, transaction);
        KeyHolder keyholder = new GeneratedKeyHolder();
        return 0;
//        return jdbcTemplate.update(
//                QUERY_INSERT_TRANSACTION,
//                new MapSqlParameterSource()
//                        .addValue("userId", userId)
//                        .addValue("amount", withdrawRequest.getAmount())
//                        .addValue("feeAmount", feeAmount)
//                        .addValue("status", "INITIAL")
//                        .addValue("providerPaymentId", null)
//                        .addValue("walletTransactionId", discountTransactionId)
//                        .addValue("transactionDatetime", LocalDateTime.now()),
//                keyholder.getKey().longValue(),
//
//                new TransactionRowMapper(). .mapRow());

    }

    @Override
    public void updateTransaction(long transactionId, String status) {
        logger.info("Start execution of query {} for transactionId = {}",
                QUERY_UPDATE_TRANSACTION, transactionId);
    }

}
