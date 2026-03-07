-- TiDB does not support DROP PRIMARY KEY on clustered index tables.
-- Recreate the table with the new primary key instead.

-- Remove rows that cannot satisfy the new primary key constraints
DELETE FROM user_idp WHERE provider_id IS NULL OR subject IS NULL;

-- Disable foreign key checks for table recreation
SET FOREIGN_KEY_CHECKS = 0;

-- Create new table with desired primary key
CREATE TABLE user_idp_new
(
    user_id     integer      NOT NULL,
    issuer      varchar(50)  NOT NULL,
    subject     varchar(50)  NOT NULL,
    create_at   timestamp    NULL,
    audience    varchar(50)  NULL,
    provider_id bigint       NOT NULL,
    PRIMARY KEY (issuer, subject, provider_id)
);

-- Copy all data to new table
INSERT INTO user_idp_new
SELECT user_id, issuer, subject, create_at, audience, provider_id
FROM user_idp;

-- Drop original table and rename new one
DROP TABLE user_idp;
RENAME TABLE user_idp_new TO user_idp;

-- Re-enable foreign key checks
SET FOREIGN_KEY_CHECKS = 1;

-- Restore foreign key constraints
ALTER TABLE user_idp
    ADD CONSTRAINT user_idp_user_id_fk FOREIGN KEY (user_id) REFERENCES user (id),
    ADD CONSTRAINT user_idp_provider_id_fk FOREIGN KEY (provider_id) REFERENCES provider (id);