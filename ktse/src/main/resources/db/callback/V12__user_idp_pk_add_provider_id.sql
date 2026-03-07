-- Make provider_id NOT NULL (required for primary key)
ALTER TABLE user_idp MODIFY COLUMN provider_id bigint NOT NULL;

-- Drop current primary key (issuer, subject)
ALTER TABLE user_idp DROP PRIMARY KEY;

-- Add new primary key including provider_id
ALTER TABLE user_idp ADD PRIMARY KEY (issuer, subject, provider_id);