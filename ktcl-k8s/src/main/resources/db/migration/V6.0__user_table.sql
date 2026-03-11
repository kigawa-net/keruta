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
DROP PROCEDURE IF EXISTS migrate_v6_step1;
CREATE PROCEDURE migrate_v6_step1()
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
CALL migrate_v6_step1();
DROP PROCEDURE IF EXISTS migrate_v6_step1;

-- 3. user_claude_configの既存ユーザーをkeruta_userテーブルに移行
DROP PROCEDURE IF EXISTS migrate_v6_step2;
CREATE PROCEDURE migrate_v6_step2()
BEGIN
    IF EXISTS (
        SELECT 1 FROM information_schema.COLUMNS
        WHERE TABLE_SCHEMA = DATABASE() AND TABLE_NAME = 'user_claude_config' AND COLUMN_NAME = 'user_issuer'
    ) THEN
        IF EXISTS (
            SELECT 1 FROM information_schema.COLUMNS
            WHERE TABLE_SCHEMA = DATABASE() AND TABLE_NAME = 'user_claude_config' AND COLUMN_NAME = 'user_id'
        ) THEN
            INSERT IGNORE INTO keruta_user (user_subject, user_issuer, user_audience)
            SELECT user_id, user_issuer, COALESCE(user_audience, '')
            FROM user_claude_config
            WHERE user_id IS NOT NULL AND user_issuer IS NOT NULL;
        END IF;
    END IF;
END;
CALL migrate_v6_step2();
DROP PROCEDURE IF EXISTS migrate_v6_step2;

-- 4. user_tokenにuser_id_fkカラムを追加・設定
ALTER TABLE user_token ADD COLUMN IF NOT EXISTS user_id_fk BIGINT NULL;

DROP PROCEDURE IF EXISTS migrate_v6_step3;
CREATE PROCEDURE migrate_v6_step3()
BEGIN
    -- user_subjectが存在する場合のみJOINしてuser_id_fkを更新
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
CALL migrate_v6_step3();
DROP PROCEDURE IF EXISTS migrate_v6_step3;

ALTER TABLE user_token MODIFY COLUMN user_id_fk BIGINT NOT NULL;
ALTER TABLE user_token DROP INDEX IF EXISTS idx_user_subject_issuer;
ALTER TABLE user_token ADD UNIQUE INDEX IF NOT EXISTS idx_user_token_user_id (user_id_fk);
ALTER TABLE user_token DROP FOREIGN KEY IF EXISTS fk_user_token_user;

DROP PROCEDURE IF EXISTS migrate_v6_step3b;
CREATE PROCEDURE migrate_v6_step3b()
BEGIN
    IF NOT EXISTS (
        SELECT 1 FROM information_schema.TABLE_CONSTRAINTS
        WHERE TABLE_SCHEMA = DATABASE() AND TABLE_NAME = 'user_token' AND CONSTRAINT_NAME = 'fk_user_token_user'
    ) THEN
        ALTER TABLE user_token ADD CONSTRAINT fk_user_token_user FOREIGN KEY (user_id_fk) REFERENCES keruta_user(id);
    END IF;
END;
CALL migrate_v6_step3b();
DROP PROCEDURE IF EXISTS migrate_v6_step3b;

ALTER TABLE user_token DROP COLUMN IF EXISTS user_subject;
ALTER TABLE user_token DROP COLUMN IF EXISTS user_issuer;
ALTER TABLE user_token DROP COLUMN IF EXISTS user_audience;

-- user_id_fk を user_id にリネーム（user_idが既に存在する場合はuser_id_fkを削除）
DROP PROCEDURE IF EXISTS migrate_v6_step4;
CREATE PROCEDURE migrate_v6_step4()
BEGIN
    DECLARE has_user_id INT DEFAULT 0;
    DECLARE has_user_id_fk INT DEFAULT 0;

    SELECT COUNT(*) INTO has_user_id
    FROM information_schema.COLUMNS
    WHERE TABLE_SCHEMA = DATABASE() AND TABLE_NAME = 'user_token' AND COLUMN_NAME = 'user_id';

    SELECT COUNT(*) INTO has_user_id_fk
    FROM information_schema.COLUMNS
    WHERE TABLE_SCHEMA = DATABASE() AND TABLE_NAME = 'user_token' AND COLUMN_NAME = 'user_id_fk';

    IF has_user_id = 0 AND has_user_id_fk = 1 THEN
        ALTER TABLE user_token RENAME COLUMN user_id_fk TO user_id;
    ELSEIF has_user_id = 1 AND has_user_id_fk = 1 THEN
        ALTER TABLE user_token DROP COLUMN user_id_fk;
    END IF;
END;
CALL migrate_v6_step4();
DROP PROCEDURE IF EXISTS migrate_v6_step4;

-- 5. user_claude_configにuser_id_fkカラムを追加してFKを設定
ALTER TABLE user_claude_config ADD COLUMN IF NOT EXISTS user_subject_tmp VARCHAR(255) NULL;

DROP PROCEDURE IF EXISTS migrate_v6_step5;
CREATE PROCEDURE migrate_v6_step5()
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
CALL migrate_v6_step5();
DROP PROCEDURE IF EXISTS migrate_v6_step5;

ALTER TABLE user_claude_config ADD COLUMN IF NOT EXISTS user_id_fk BIGINT NULL;

DROP PROCEDURE IF EXISTS migrate_v6_step6;
CREATE PROCEDURE migrate_v6_step6()
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
CALL migrate_v6_step6();
DROP PROCEDURE IF EXISTS migrate_v6_step6;

ALTER TABLE user_claude_config MODIFY COLUMN user_id_fk BIGINT NOT NULL;
ALTER TABLE user_claude_config DROP INDEX IF EXISTS idx_user_id_issuer;
ALTER TABLE user_claude_config ADD UNIQUE INDEX IF NOT EXISTS idx_user_claude_config_user_id (user_id_fk);
ALTER TABLE user_claude_config DROP FOREIGN KEY IF EXISTS fk_user_claude_config_user;

DROP PROCEDURE IF EXISTS migrate_v6_step6b;
CREATE PROCEDURE migrate_v6_step6b()
BEGIN
    IF NOT EXISTS (
        SELECT 1 FROM information_schema.TABLE_CONSTRAINTS
        WHERE TABLE_SCHEMA = DATABASE() AND TABLE_NAME = 'user_claude_config' AND CONSTRAINT_NAME = 'fk_user_claude_config_user'
    ) THEN
        ALTER TABLE user_claude_config ADD CONSTRAINT fk_user_claude_config_user FOREIGN KEY (user_id_fk) REFERENCES keruta_user(id);
    END IF;
END;
CALL migrate_v6_step6b();
DROP PROCEDURE IF EXISTS migrate_v6_step6b;

ALTER TABLE user_claude_config DROP COLUMN IF EXISTS user_id;
ALTER TABLE user_claude_config DROP COLUMN IF EXISTS user_issuer;
ALTER TABLE user_claude_config DROP COLUMN IF EXISTS user_audience;
ALTER TABLE user_claude_config DROP COLUMN IF EXISTS user_subject_tmp;

-- user_id_fk を user_id にリネーム（user_idが既に存在する場合はuser_id_fkを削除）
DROP PROCEDURE IF EXISTS migrate_v6_step7;
CREATE PROCEDURE migrate_v6_step7()
BEGIN
    DECLARE has_user_id INT DEFAULT 0;
    DECLARE has_user_id_fk INT DEFAULT 0;

    SELECT COUNT(*) INTO has_user_id
    FROM information_schema.COLUMNS
    WHERE TABLE_SCHEMA = DATABASE() AND TABLE_NAME = 'user_claude_config' AND COLUMN_NAME = 'user_id';

    SELECT COUNT(*) INTO has_user_id_fk
    FROM information_schema.COLUMNS
    WHERE TABLE_SCHEMA = DATABASE() AND TABLE_NAME = 'user_claude_config' AND COLUMN_NAME = 'user_id_fk';

    IF has_user_id = 0 AND has_user_id_fk = 1 THEN
        ALTER TABLE user_claude_config RENAME COLUMN user_id_fk TO user_id;
    ELSEIF has_user_id = 1 AND has_user_id_fk = 1 THEN
        ALTER TABLE user_claude_config DROP COLUMN user_id_fk;
    END IF;
END;
CALL migrate_v6_step7();
DROP PROCEDURE IF EXISTS migrate_v6_step7;