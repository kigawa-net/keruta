-- Drop existing foreign key referencing provider.id
-- Only queue_provider.fk_2 references provider.id (confirmed via INFORMATION_SCHEMA)
ALTER TABLE queue_provider DROP FOREIGN KEY fk_2;

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
