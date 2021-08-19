ALTER TABLE `roadmap`
    CHANGE COLUMN `status` `status` VARCHAR(70) DEFAULT 'FUTURE' NOT NULL;

UPDATE `roadmap` SET `status` = 'FUTURE' WHERE status = 'NOT_STARTED';
UPDATE `roadmap` SET `status` = 'COMPLETED' WHERE status = 'COMPLETE';