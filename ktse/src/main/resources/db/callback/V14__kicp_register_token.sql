CREATE TABLE IF NOT EXISTS kicp_register_token
(
    token               VARCHAR(255) NOT NULL,
    creator_identity_id VARCHAR(512) NOT NULL,
    expires_at_epoch_ms BIGINT       NOT NULL,
    PRIMARY KEY (token)
);
