ALTER TABLE `ogsm` ADD COLUMN `text` TEXT NOT NULL;
ALTER TABLE `assertion` ADD COLUMN `status` VARCHAR(70) NOT NULL DEFAULT 'NOT_STARTED';
DROP TABLE `assertion_tag`;