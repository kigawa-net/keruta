-- Create keruta_user table and migrate user identification to FK relationships

-- 1. keruta_userテーブルを作成
CREATE TABLE IF NOT EXISTS keruta_user (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_subject VARCHAR(255) NOT NULL,
    user_issuer VARCHAR(512) NOT NULL,
    user_audience VARCHAR(512) NOT NULL DEFAULT '',
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    UNIQUE INDEX idx_user_subject_issuer (user_subject, user_issuer)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- 2. user_tokenの既存ユーザーをkeruta_userテーブルに移行
INSERT IGNORE INTO keruta_user (user_subject, user_issuer, user_audience)
SELECT user_subject, user_issuer, COALESCE(user_audience, '')
FROM user_token
WHERE user_subject IS NOT NULL AND user_issuer IS NOT NULL;

-- 3. user_claude_configの既存ユーザーをkeruta_userテーブルに移行
INSERT IGNORE INTO keruta_user (user_subject, user_issuer, user_audience)
SELECT user_id, user_issuer, COALESCE(user_audience, '')
FROM user_claude_config
WHERE user_id IS NOT NULL AND user_issuer IS NOT NULL;

-- 4. user_tokenにuser_id_fkカラムを追加してFKを設定
ALTER TABLE user_token ADD COLUMN IF NOT EXISTS user_id_fk BIGINT NULL;

UPDATE user_token ut
    JOIN keruta_user u ON ut.user_subject = u.user_subject AND ut.user_issuer = u.user_issuer
    SET ut.user_id_fk = u.id
    WHERE ut.user_id_fk IS NULL;

ALTER TABLE user_token MODIFY COLUMN user_id_fk BIGINT NOT NULL;
ALTER TABLE user_token DROP INDEX IF EXISTS idx_user_subject_issuer;
ALTER TABLE user_token ADD UNIQUE INDEX IF NOT EXISTS idx_user_token_user_id (user_id_fk);
ALTER TABLE user_token ADD CONSTRAINT IF NOT EXISTS fk_user_token_user FOREIGN KEY (user_id_fk) REFERENCES keruta_user(id);
ALTER TABLE user_token DROP COLUMN IF EXISTS user_subject;
ALTER TABLE user_token DROP COLUMN IF EXISTS user_issuer;
ALTER TABLE user_token DROP COLUMN IF EXISTS user_audience;
ALTER TABLE user_token RENAME COLUMN user_id_fk TO user_id;

-- 5. user_claude_configにuser_id_fkカラムを追加してFKを設定
-- user_idカラムはVARCHAR(255)のためuser_subject_tmpとしてバックアップ
ALTER TABLE user_claude_config ADD COLUMN IF NOT EXISTS user_subject_tmp VARCHAR(255) NULL;

UPDATE user_claude_config
    SET user_subject_tmp = user_id
    WHERE user_subject_tmp IS NULL AND user_id IS NOT NULL;

ALTER TABLE user_claude_config ADD COLUMN IF NOT EXISTS user_id_fk BIGINT NULL;

UPDATE user_claude_config ucc
    JOIN keruta_user u ON ucc.user_subject_tmp = u.user_subject AND ucc.user_issuer = u.user_issuer
    SET ucc.user_id_fk = u.id
    WHERE ucc.user_id_fk IS NULL;

ALTER TABLE user_claude_config MODIFY COLUMN user_id_fk BIGINT NOT NULL;
ALTER TABLE user_claude_config DROP INDEX IF EXISTS idx_user_id_issuer;
ALTER TABLE user_claude_config ADD UNIQUE INDEX IF NOT EXISTS idx_user_claude_config_user_id (user_id_fk);
ALTER TABLE user_claude_config ADD CONSTRAINT IF NOT EXISTS fk_user_claude_config_user FOREIGN KEY (user_id_fk) REFERENCES keruta_user(id);
ALTER TABLE user_claude_config DROP COLUMN IF EXISTS user_id;
ALTER TABLE user_claude_config DROP COLUMN IF EXISTS user_issuer;
ALTER TABLE user_claude_config DROP COLUMN IF EXISTS user_audience;
ALTER TABLE user_claude_config DROP COLUMN IF EXISTS user_subject_tmp;
ALTER TABLE user_claude_config RENAME COLUMN user_id_fk TO user_id;
