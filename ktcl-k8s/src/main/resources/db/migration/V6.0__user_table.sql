-- Create user table and migrate user identification to FK relationships

-- 1. userテーブルを作成
CREATE TABLE IF NOT EXISTS `user` (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_subject VARCHAR(255) NOT NULL,
    user_issuer VARCHAR(512) NOT NULL,
    user_audience VARCHAR(512) NOT NULL DEFAULT '',
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    UNIQUE INDEX idx_user_subject_issuer (user_subject, user_issuer)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- 2. user_tokenの既存ユーザーをuserテーブルに移行
INSERT INTO `user` (user_subject, user_issuer, user_audience)
SELECT user_subject, user_issuer, COALESCE(user_audience, '')
FROM user_token
ON DUPLICATE KEY UPDATE id = id;

-- 3. user_claude_configの既存ユーザーをuserテーブルに移行（重複はスキップ）
INSERT INTO `user` (user_subject, user_issuer, user_audience)
SELECT user_id, user_issuer, COALESCE(user_audience, '')
FROM user_claude_config
ON DUPLICATE KEY UPDATE id = id;

-- 4. user_tokenにuser_idカラムを追加してFKを設定
ALTER TABLE user_token ADD COLUMN user_id_fk BIGINT NULL;
UPDATE user_token ut
    JOIN `user` u ON ut.user_subject = u.user_subject AND ut.user_issuer = u.user_issuer
    SET ut.user_id_fk = u.id;
ALTER TABLE user_token MODIFY COLUMN user_id_fk BIGINT NOT NULL;
ALTER TABLE user_token DROP INDEX idx_user_subject_issuer;
ALTER TABLE user_token ADD UNIQUE INDEX idx_user_id (user_id_fk);
ALTER TABLE user_token ADD CONSTRAINT fk_user_token_user FOREIGN KEY (user_id_fk) REFERENCES `user`(id);
ALTER TABLE user_token DROP COLUMN user_subject;
ALTER TABLE user_token DROP COLUMN user_issuer;
ALTER TABLE user_token DROP COLUMN user_audience;
ALTER TABLE user_token CHANGE COLUMN user_id_fk user_id BIGINT NOT NULL;

-- 5. user_claude_configにuser_idカラムを追加してFKを設定
ALTER TABLE user_claude_config ADD COLUMN user_id_fk BIGINT NULL;
UPDATE user_claude_config ucc
    JOIN `user` u ON ucc.user_id = u.user_subject AND ucc.user_issuer = u.user_issuer
    SET ucc.user_id_fk = u.id;
ALTER TABLE user_claude_config MODIFY COLUMN user_id_fk BIGINT NOT NULL;
ALTER TABLE user_claude_config DROP INDEX idx_user_id_issuer;
ALTER TABLE user_claude_config ADD UNIQUE INDEX idx_user_id (user_id_fk);
ALTER TABLE user_claude_config ADD CONSTRAINT fk_user_claude_config_user FOREIGN KEY (user_id_fk) REFERENCES `user`(id);
ALTER TABLE user_claude_config DROP COLUMN user_id;
ALTER TABLE user_claude_config DROP COLUMN user_issuer;
ALTER TABLE user_claude_config DROP COLUMN user_audience;
ALTER TABLE user_claude_config CHANGE COLUMN user_id_fk user_id BIGINT NOT NULL;
