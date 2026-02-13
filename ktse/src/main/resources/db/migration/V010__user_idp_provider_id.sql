-- Disable foreign key constraint checks to allow column type modification.
-- This avoids needing to know auto-generated FK constraint names in TiDB.
SET FOREIGN_KEY_CHECKS=0;

-- Modify provider.id from integer to bigint
ALTER TABLE provider MODIFY COLUMN id bigint auto_increment;

-- Modify referencing columns to bigint to match provider.id
ALTER TABLE queue MODIFY COLUMN provider_id bigint;
ALTER TABLE queue_provider MODIFY COLUMN provider_id bigint;

-- Re-enable foreign key constraint checks
SET FOREIGN_KEY_CHECKS=1;

-- Handle user_idp.provider_id column
ALTER TABLE user_idp ADD provider_id bigint null;
ALTER TABLE user_idp ADD CONSTRAINT user_idp_provider_id_fk
    FOREIGN KEY (provider_id) REFERENCES provider (id);