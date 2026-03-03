CREATE TABLE transactions (
    terminal_dttm DATETIME(6) NOT NULL,
    trx_uuid VARCHAR(36) NOT NULL,

    source_card_id BIGINT NOT NULL,
    target_account_id BIGINT,
    amount DECIMAL(19, 4) NOT NULL,
    currency VARCHAR(3) NOT NULL,
    transaction_type VARCHAR(30) NOT NULL,
    status VARCHAR(20) NOT NULL,
    description VARCHAR(255),
    metadata JSON,

    PRIMARY KEY (terminal_dttm, trx_uuid),

    CONSTRAINT fk_trx_source FOREIGN KEY (source_card_id) REFERENCES accounts(id),
    CONSTRAINT fk_trx_target FOREIGN KEY (target_account_id) REFERENCES accounts(id)
);