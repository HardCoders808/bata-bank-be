CREATE TABLE refresh_token
(
    id               VARCHAR(36) PRIMARY KEY,
    user_id          VARCHAR(255) NOT NULL,
    device_id        VARCHAR(255) NOT NULL,
    token_hash       VARCHAR(255) NOT NULL,
    expires_at       TIMESTAMP    NOT NULL,
    revoked_at       TIMESTAMP    NULL,
    replaced_by_hash VARCHAR(255) NULL,
    created_at       TIMESTAMP    NOT NULL
);

CREATE UNIQUE INDEX ux_refresh_token_token_hash
    ON refresh_token (token_hash);

CREATE INDEX ix_refresh_token_user_id
    ON refresh_token (user_id);

CREATE INDEX ix_refresh_token_expires_at
    ON refresh_token (expires_at);

CREATE INDEX ix_refresh_token_revoked_at
    ON refresh_token (revoked_at);