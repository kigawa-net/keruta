-- Drop existing foreign key referencing provider.id dynamically
-- Look up actual FK name since auto-generated names may vary across TiDB/MySQL instances
SET @fk_to_drop = (
    SELECT kcu.CONSTRAINT_NAME
    FROM INFORMATION_SCHEMA.KEY_COLUMN_USAGE kcu
    JOIN INFORMATION_SCHEMA.TABLE_CONSTRAINTS tc
        ON kcu.CONSTRAINT_NAME = tc.CONSTRAINT_NAME
        AND kcu.TABLE_SCHEMA = tc.TABLE_SCHEMA
        AND kcu.TABLE_NAME = tc.TABLE_NAME
    WHERE kcu.TABLE_SCHEMA = DATABASE()
    AND kcu.TABLE_NAME = 'queue_provider'
    AND kcu.COLUMN_NAME = 'provider_id'
    AND kcu.REFERENCED_TABLE_NAME = 'provider'
    AND tc.CONSTRAINT_TYPE = 'FOREIGN KEY'
    LIMIT 1
);
SET @drop_fk_sql = IF(
    @fk_to_drop IS NOT NULL,
    CONCAT('ALTER TABLE queue_provider DROP FOREIGN KEY `', @fk_to_drop, '`'),
    'DO 0'
);
PREPARE fk_drop_stmt FROM @drop_fk_sql;
EXECUTE fk_drop_stmt;
DEALLOCATE PREPARE fk_drop_stmt;

-- Modify provider.id from integer to bigint
ALTER TABLE provider MODIFY COLUMN id bigint auto_increment;

-- Modify queue_provider.provider_id to bigint to match provider.id
ALTER TABLE queue_provider MODIFY COLUMN provider_id bigint;

-- Re-add foreign key constraint with explicit name
ALTER TABLE queue_provider ADD CONSTRAINT queue_provider_provider_id_fk
    FOREIGN KEY (provider_id) REFERENCES provider (id);

-- Handle user_idp.provider_id column
-- Drop if exists from failed V009 migration, then add fresh
ALTER TABLE user_idp DROP COLUMN IF EXISTS provider_id;

-- Add provider_id column with bigint type
ALTER TABLE user_idp ADD provider_id bigint null;

-- Add foreign key constraint
ALTER TABLE user_idp ADD CONSTRAINT user_idp_provider_id_fk
    FOREIGN KEY (provider_id) REFERENCES provider (id);
