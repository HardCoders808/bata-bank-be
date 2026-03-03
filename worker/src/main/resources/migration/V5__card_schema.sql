CREATE TABLE cards (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    account_id BIGINT NOT NULL,

    card_number VARCHAR(19) NOT NULL UNIQUE,
    card_holder_name VARCHAR(100) NOT NULL,
    expiry_date DATE NOT NULL,
    cvv VARCHAR(4) NOT NULL,

    pin_hash VARCHAR(255) NOT NULL,
    card_type VARCHAR(20) NOT NULL,
    status VARCHAR(20) NOT NULL,

    daily_limit DECIMAL(19, 4) DEFAULT 1000.00,
    monthly_limit DECIMAL(19, 4) DEFAULT 5000.00,

    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,

    CONSTRAINT fk_card_account FOREIGN KEY (account_id) REFERENCES accounts(id)
);