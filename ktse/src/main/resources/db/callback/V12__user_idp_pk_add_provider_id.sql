-- Make subject NOT NULL (required for primary key)
ALTER TABLE user_idp MODIFY COLUMN subject varchar(50) NOT NULL;

-- Make provider_id NOT NULL (required for primary key)
ALTER TABLE user_idp MODIFY COLUMN provider_id bigint NOT NULL;

-- Drop old primary key (user_id, issuer) and add new one
ALTER TABLE user_idp DROP PRIMARY KEY, ADD PRIMARY KEY (issuer, subject, provider_id);