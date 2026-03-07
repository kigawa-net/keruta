-- Make provider_id NOT NULL, drop old primary key, and add new primary key in a single statement
ALTER TABLE user_idp
    MODIFY COLUMN provider_id bigint NOT NULL,
    DROP PRIMARY KEY,
    ADD PRIMARY KEY (issuer, subject, provider_id);