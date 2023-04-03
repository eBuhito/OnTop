CREATE TABLE bank_accounts
(
    id             BIGINT             NOT NULL PRIMARY KEY,
    name           VARCHAR_IGNORECASE NOT NULL,
    routing_number VARCHAR_IGNORECASE NOT NULL,
    account_number VARCHAR_IGNORECASE NOT NULL,
    currency       VARCHAR_IGNORECASE NOT NULL
);

create table transactions
(
    id                    IDENTITY PRIMARY KEY,
    user_id               BIGINT             NOT NULL,
    amount                NUMERIC(10, 2)     NOT NULL,
    currency              VARCHAR_IGNORECASE NOT NULL,
    type                  VARCHAR_IGNORECASE NOT NULL,
    status                VARCHAR_IGNORECASE NOT NULL,
    provider_payment_id   VARCHAR_IGNORECASE,
    wallet_transaction_id BIGINT             NOT NULL,
    transaction_datetime  TIMESTAMP          NOT NULL
);