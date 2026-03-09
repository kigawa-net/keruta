-- Add user_issuer and user_audience columns for multi-issuer user identification

-- user_token: rename user_id to user_subject, add user_issuer/user_audience, change unique constraint
ALTER TABLE user_token CHANGE COLUMN user_id user_subject VARCHAR(255) NOT NULL;
ALTER TABLE user_token DROP INDEX user_id;
ALTER TABLE user_token ADD COLUMN user_issuer VARCHAR(512) NOT NULL DEFAULT '';
ALTER TABLE user_token ADD COLUMN user_audience VARCHAR(512) NOT NULL DEFAULT '';
ALTER TABLE user_token ADD UNIQUE INDEX idx_user_subject_issuer (user_subject, user_issuer);

-- user_claude_config: add user_issuer/user_audience, change unique constraint
ALTER TABLE user_claude_config DROP INDEX user_id;
ALTER TABLE user_claude_config ADD COLUMN user_issuer VARCHAR(512) NOT NULL DEFAULT '';
ALTER TABLE user_claude_config ADD COLUMN user_audience VARCHAR(512) NOT NULL DEFAULT '';
ALTER TABLE user_claude_config ADD UNIQUE INDEX idx_user_id_issuer (user_id, user_issuer);
