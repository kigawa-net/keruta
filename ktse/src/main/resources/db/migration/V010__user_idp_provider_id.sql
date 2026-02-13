-- Drop FK constraints referencing provider(id) explicitly.
-- The queue table FK (from V001, unnamed) is auto-named fk_1 in TiDB.
-- The queue_provider table FK on provider_id (from V003, second unnamed FK) is auto-named fk_2.
ALTER TABLE queue DROP FOREIGN KEY fk_1;
ALTER TABLE queue_provider DROP FOREIGN KEY fk_2;

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