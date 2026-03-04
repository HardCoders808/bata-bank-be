ALTER DATABASE bata_bank
    CHARACTER SET utf8mb4
    COLLATE utf8mb4_cz_0900_ai_ci;

ALTER TABLE users
    CONVERT TO CHARACTER SET utf8mb4
        COLLATE utf8mb4_cz_0900_ai_ci;

ALTER TABLE users
    MODIFY COLUMN role VARCHAR(40) NOT NULL;

ALTER TABLE transactions
    ADD COLUMN source_account_id BIGINT DEFAULT NULL AFTER target_account_id;

ALTER TABLE transactions
    ADD CONSTRAINT fk_trx_source_account
        FOREIGN KEY (source_account_id) REFERENCES accounts(id);