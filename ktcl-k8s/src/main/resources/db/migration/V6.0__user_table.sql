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

-- 2. user_tokenの既存ユーザーをkeruta_userテーブルに移行（user_subjectカラムが存在する場合のみ）
DROP PROCEDURE IF EXISTS migrate_v6_user_token_to_keruta_user;
CREATE PROCEDURE migrate_v6_user_token_to_keruta_user()
BEGIN
    IF EXISTS (
        SELECT 1 FROM information_schema.COLUMNS
        WHERE TABLE_SCHEMA = DATABASE() AND TABLE_NAME = 'user_token' AND COLUMN_NAME = 'user_subject'
    ) THEN
        INSERT IGNORE INTO keruta_user (user_subject, user_issuer, user_audience)
        SELECT user_subject, user_issuer, COALESCE(user_audience, '')
        FROM user_token
        WHERE user_subject IS NOT NULL AND user_issuer IS NOT NULL;
    END IF;
END;
CALL migrate_v6_user_token_to_keruta_user();
DROP PROCEDURE IF EXISTS migrate_v6_user_token_to_keruta_user;

-- 3. user_claude_configの既存ユーザーをkeruta_userテーブルに移行（user_idカラムが存在する場合のみ）
DROP PROCEDURE IF EXISTS migrate_v6_user_claude_config_to_keruta_user;
CREATE PROCEDURE migrate_v6_user_claude_config_to_keruta_user()
BEGIN
    IF EXISTS (
        SELECT 1 FROM information_schema.COLUMNS
        WHERE TABLE_SCHEMA = DATABASE() AND TABLE_NAME = 'user_claude_config' AND COLUMN_NAME = 'user_id'
    ) AND EXISTS (
        SELECT 1 FROM information_schema.COLUMNS
        WHERE TABLE_SCHEMA = DATABASE() AND TABLE_NAME = 'user_claude_config' AND COLUMN_NAME = 'user_issuer'
    ) THEN
        INSERT IGNORE INTO keruta_user (user_subject, user_issuer, user_audience)
        SELECT user_id, user_issuer, COALESCE(user_audience, '')
        FROM user_claude_config
        WHERE user_id IS NOT NULL AND user_issuer IS NOT NULL;
    END IF;
END;
CALL migrate_v6_user_claude_config_to_keruta_user();
DROP PROCEDURE IF EXISTS migrate_v6_user_claude_config_to_keruta_user;

-- 4. user_tokenにuser_id_fkカラムを追加してFKを設定
ALTER TABLE user_token ADD COLUMN IF NOT EXISTS user_id_fk BIGINT NULL;

-- user_subjectが存在する場合のみJOINしてuser_id_fkを更新
DROP PROCEDURE IF EXISTS migrate_v6_update_user_token_fk;
CREATE PROCEDURE migrate_v6_update_user_token_fk()
BEGIN
    IF EXISTS (
        SELECT 1 FROM information_schema.COLUMNS
        WHERE TABLE_SCHEMA = DATABASE() AND TABLE_NAME = 'user_token' AND COLUMN_NAME = 'user_subject'
    ) THEN
        UPDATE user_token ut
            JOIN keruta_user u ON ut.user_subject = u.user_subject AND ut.user_issuer = u.user_issuer
            SET ut.user_id_fk = u.id
            WHERE ut.user_id_fk IS NULL;
    END IF;
END;
CALL migrate_v6_update_user_token_fk();
DROP PROCEDURE IF EXISTS migrate_v6_update_user_token_fk;

ALTER TABLE user_token MODIFY COLUMN user_id_fk BIGINT NOT NULL;
ALTER TABLE user_token DROP INDEX IF EXISTS idx_user_subject_issuer;
ALTER TABLE user_token ADD UNIQUE INDEX IF NOT EXISTS idx_user_token_user_id (user_id_fk);
ALTER TABLE user_token DROP FOREIGN KEY IF EXISTS fk_user_token_user;
ALTER TABLE user_token ADD CONSTRAINT fk_user_token_user FOREIGN KEY (user_id_fk) REFERENCES keruta_user(id);
ALTER TABLE user_token DROP COLUMN IF EXISTS user_subject;
ALTER TABLE user_token DROP COLUMN IF EXISTS user_issuer;
ALTER TABLE user_token DROP COLUMN IF EXISTS user_audience;
ALTER TABLE user_token RENAME COLUMN IF EXISTS user_id_fk TO user_id;

-- 5. user_claude_configにuser_id_fkカラムを追加してFKを設定
ALTER TABLE user_claude_config ADD COLUMN IF NOT EXISTS user_subject_tmp VARCHAR(255) NULL;

DROP PROCEDURE IF EXISTS migrate_v6_update_user_claude_config_tmp;
CREATE PROCEDURE migrate_v6_update_user_claude_config_tmp()
BEGIN
    IF EXISTS (
        SELECT 1 FROM information_schema.COLUMNS
        WHERE TABLE_SCHEMA = DATABASE() AND TABLE_NAME = 'user_claude_config' AND COLUMN_NAME = 'user_id'
    ) THEN
        UPDATE user_claude_config
            SET user_subject_tmp = user_id
            WHERE user_subject_tmp IS NULL AND user_id IS NOT NULL;
    END IF;
END;
CALL migrate_v6_update_user_claude_config_tmp();
DROP PROCEDURE IF EXISTS migrate_v6_update_user_claude_config_tmp;

ALTER TABLE user_claude_config ADD COLUMN IF NOT EXISTS user_id_fk BIGINT NULL;

DROP PROCEDURE IF EXISTS migrate_v6_update_user_claude_config_fk;
CREATE PROCEDURE migrate_v6_update_user_claude_config_fk()
BEGIN
    IF EXISTS (
        SELECT 1 FROM information_schema.COLUMNS
        WHERE TABLE_SCHEMA = DATABASE() AND TABLE_NAME = 'user_claude_config' AND COLUMN_NAME = 'user_issuer'
    ) THEN
        UPDATE user_claude_config ucc
            JOIN keruta_user u ON ucc.user_subject_tmp = u.user_subject AND ucc.user_issuer = u.user_issuer
            SET ucc.user_id_fk = u.id
            WHERE ucc.user_id_fk IS NULL;
    END IF;
END;
CALL migrate_v6_update_user_claude_config_fk();
DROP PROCEDURE IF EXISTS migrate_v6_update_user_claude_config_fk;

ALTER TABLE user_claude_config MODIFY COLUMN user_id_fk BIGINT NOT NULL;
ALTER TABLE user_claude_config DROP INDEX IF EXISTS idx_user_id_issuer;
ALTER TABLE user_claude_config ADD UNIQUE INDEX IF NOT EXISTS idx_user_claude_config_user_id (user_id_fk);
ALTER TABLE user_claude_config DROP FOREIGN KEY IF EXISTS fk_user_claude_config_user;
ALTER TABLE user_claude_config ADD CONSTRAINT fk_user_claude_config_user FOREIGN KEY (user_id_fk) REFERENCES keruta_user(id);
ALTER TABLE user_claude_config DROP COLUMN IF EXISTS user_id;
ALTER TABLE user_claude_config DROP COLUMN IF EXISTS user_issuer;
ALTER TABLE user_claude_config DROP COLUMN IF EXISTS user_audience;
ALTER TABLE user_claude_config DROP COLUMN IF EXISTS user_subject_tmp;
ALTER TABLE user_claude_config RENAME COLUMN IF EXISTS user_id_fk TO user_id;