CREATE TABLE user_mfa
(
    id         BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id    BIGINT       NOT NULL,
    secret     VARCHAR(512) NOT NULL,
    enabled    BOOLEAN      NOT NULL DEFAULT FALSE,
    created_at TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_user_mfa_user
        FOREIGN KEY (user_id) REFERENCES users (id)
            ON DELETE CASCADE
);

CREATE INDEX ix_user_mfa_user_id ON user_mfa (user_id);


CREATE TABLE mfa_challenge
(
    id          VARCHAR(36) NOT NULL,
    user_id     BIGINT      NOT NULL,
    purpose     VARCHAR(20) NOT NULL,
    expires_at  DATETIME    NOT NULL,
    attempts    INT         NOT NULL,
    verified_at DATETIME    NULL,
    created_at  DATETIME    NOT NULL,

    CONSTRAINT pk_mfa_challenge PRIMARY KEY (id),

    CONSTRAINT fk_mfa_challenge_user
        FOREIGN KEY (user_id)
            REFERENCES users (id)
            ON DELETE CASCADE
);


CREATE INDEX ix_mfa_challenge_user_id
    ON mfa_challenge (user_id);

CREATE INDEX ix_mfa_challenge_expires_at
    ON mfa_challenge (expires_at)