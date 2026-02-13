-- Drop all foreign keys referencing provider(id) before modifying the column type.
-- Uses a stored procedure with a cursor to handle auto-generated FK names dynamically.
-- CHAR(96) is used instead of backtick literals to avoid Flyway SQL parser issues
-- with backtick characters inside string literals.

DROP PROCEDURE IF EXISTS keruta_v010_drop_provider_fks;

CREATE PROCEDURE keruta_v010_drop_provider_fks()
BEGIN
    DECLARE v_done INT DEFAULT FALSE;
    DECLARE v_table_name VARCHAR(255);
    DECLARE v_constraint_name VARCHAR(255);

    DECLARE fk_cursor CURSOR FOR
        SELECT kcu.TABLE_NAME, kcu.CONSTRAINT_NAME
        FROM INFORMATION_SCHEMA.KEY_COLUMN_USAGE kcu
        JOIN INFORMATION_SCHEMA.TABLE_CONSTRAINTS tc
            ON kcu.CONSTRAINT_NAME = tc.CONSTRAINT_NAME
            AND kcu.TABLE_SCHEMA = tc.TABLE_SCHEMA
            AND kcu.TABLE_NAME = tc.TABLE_NAME
        WHERE kcu.TABLE_SCHEMA = DATABASE()
          AND kcu.REFERENCED_TABLE_NAME = 'provider'
          AND kcu.REFERENCED_COLUMN_NAME = 'id'
          AND tc.CONSTRAINT_TYPE = 'FOREIGN KEY';

    DECLARE CONTINUE HANDLER FOR NOT FOUND SET v_done = TRUE;

    OPEN fk_cursor;

    drop_loop: LOOP
        FETCH fk_cursor INTO v_table_name, v_constraint_name;
        IF v_done THEN
            LEAVE drop_loop;
        END IF;
        SET @drop_fk_sql = CONCAT(
            'ALTER TABLE ', CHAR(96), v_table_name, CHAR(96),
            ' DROP FOREIGN KEY ', CHAR(96), v_constraint_name, CHAR(96)
        );
        PREPARE drop_fk_stmt FROM @drop_fk_sql;
        EXECUTE drop_fk_stmt;
        DEALLOCATE PREPARE drop_fk_stmt;
    END LOOP;

    CLOSE fk_cursor;
END;

CALL keruta_v010_drop_provider_fks();
DROP PROCEDURE keruta_v010_drop_provider_fks;

-- Modify provider.id from integer to bigint
ALTER TABLE provider MODIFY COLUMN id bigint auto_increment;

-- Modify referencing columns to bigint to match provider.id
ALTER TABLE queue MODIFY COLUMN provider_id bigint;
ALTER TABLE queue_provider MODIFY COLUMN provider_id bigint;

-- Re-add FK constraints with explicit names
ALTER TABLE queue ADD CONSTRAINT queue_provider_id_fk
    FOREIGN KEY (provider_id) REFERENCES provider (id);
ALTER TABLE queue_provider ADD CONSTRAINT queue_provider_provider_id_fk
    FOREIGN KEY (provider_id) REFERENCES provider (id);

-- Handle user_idp.provider_id column
-- Add provider_id column with bigint type
ALTER TABLE user_idp ADD provider_id bigint null;

-- Add foreign key constraint
ALTER TABLE user_idp ADD CONSTRAINT user_idp_provider_id_fk
    FOREIGN KEY (provider_id) REFERENCES provider (id);