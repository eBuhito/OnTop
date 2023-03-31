create table bank_accounts
(
    id             long not null primary key,
    name           varchar not null,
    routing_number varchar not null,
    account_number varchar not null,
    currency       varchar not null
);

create table transactions
(
    id                      long not null primary key,
    user_id                 long not null ,
    amount                  decimal not null,
    amount_fee              decimal not null,
    status                  varchar not null,
    provider_payment_id     varchar,
    wallet_transaction_id   long not null,
    transaction_datetime    timestamp not null
)