CREATE TABLE provider_add_token
(
    token      VARCHAR(36)  NOT NULL,
    user_id    INT          NOT NULL,
    name       VARCHAR(50)  NOT NULL,
    issuer     VARCHAR(255) NOT NULL,
    audience   VARCHAR(50)  NOT NULL,
    expires_at DATETIME     NOT NULL,
    PRIMARY KEY (token),
    FOREIGN KEY (user_id) REFERENCES user (id)
);
