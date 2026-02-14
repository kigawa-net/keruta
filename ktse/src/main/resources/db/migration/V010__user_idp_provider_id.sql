-- Drop foreign key constraint for queue.provider_id referencing provider.id
SET @fk_name = (
    SELECT CONSTRAINT_NAME
    FROM INFORMATION_SCHEMA.KEY_COLUMN_USAGE
    WHERE TABLE_SCHEMA = DATABASE()
      AND TABLE_NAME = 'queue'
      AND COLUMN_NAME = 'provider_id'
      AND REFERENCED_TABLE_NAME = 'provider'
    LIMIT 1
);
SET @drop_sql = CONCAT('ALTER TABLE `queue` DROP FOREIGN KEY `', @fk_name, '`');
PREPARE drop_stmt FROM @drop_sql;
EXECUTE drop_stmt;
DEALLOCATE PREPARE drop_stmt;

-- Drop foreign key constraint for queue_provider.provider_id referencing provider.id
SET @fk_name = (
    SELECT CONSTRAINT_NAME
    FROM INFORMATION_SCHEMA.KEY_COLUMN_USAGE
    WHERE TABLE_SCHEMA = DATABASE()
      AND TABLE_NAME = 'queue_provider'
      AND COLUMN_NAME = 'provider_id'
      AND REFERENCED_TABLE_NAME = 'provider'
    LIMIT 1
);
SET @drop_sql = CONCAT('ALTER TABLE `queue_provider` DROP FOREIGN KEY `', @fk_name, '`');
PREPARE drop_stmt FROM @drop_sql;
EXECUTE drop_stmt;
DEALLOCATE PREPARE drop_stmt;

-- Modify provider.id from integer to bigint
ALTER TABLE provider MODIFY COLUMN id bigint auto_increment;

-- Modify referencing columns to bigint to match provider.id
ALTER TABLE queue MODIFY COLUMN provider_id bigint;
ALTER TABLE queue_provider MODIFY COLUMN provider_id bigint;

-- Re-add FK constraints with explicit names
ALTER TABLE queue ADD CONSTRAINT queue_provider_id_fk FOREIGN KEY (provider_id) REFERENCES provider (id);
ALTER TABLE queue_provider ADD CONSTRAINT queue_provider_provider_id_fk FOREIGN KEY (provider_id) REFERENCES provider (id);

-- Add provider_id column to user_idp
ALTER TABLE user_idp ADD provider_id bigint null;
ALTER TABLE user_idp ADD CONSTRAINT user_idp_provider_id_fk
    FOREIGN KEY (provider_id) REFERENCES provider (id);
