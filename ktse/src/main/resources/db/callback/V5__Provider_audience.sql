SET @s = IF(
    NOT EXISTS(SELECT 1 FROM INFORMATION_SCHEMA.COLUMNS
               WHERE TABLE_SCHEMA = DATABASE() AND TABLE_NAME = 'provider' AND COLUMN_NAME = 'audience'),
    'ALTER TABLE provider ADD audience varchar(50) null',
    'SELECT 1'
);
PREPARE stmt FROM @s;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;