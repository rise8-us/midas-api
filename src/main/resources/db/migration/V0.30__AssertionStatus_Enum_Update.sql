ALTER TABLE `assertion`
    CHANGE COLUMN `status` `temp_status` BIGINT,
    ADD COLUMN `status` VARCHAR(70) DEFAULT "NOT_STARTED";

UPDATE `assertion`
    SET `status` = "NOT_STARTED" WHERE `temp_status` = 0;

UPDATE `assertion`
    SET `status` = "ON_TRACK" WHERE `temp_status` IN (1, 2);

UPDATE `assertion`
    SET `status` = "BLOCKED" WHERE `temp_status` = 3;

UPDATE `assertion`
    SET `status` = "AT_RISK" WHERE `temp_status` = 4;

UPDATE `assertion`
    SET `status` = "COMPLETED" WHERE `temp_status` = 5;

ALTER TABLE `assertion`
    DROP COLUMN `temp_status`;

UPDATE `comment`
    SET `text` = REGEXP_REPLACE(text, '###NEEDS_ATTENTION','###BLOCKED');

UPDATE `comment`
    SET `text` = REGEXP_REPLACE(text, '###STARTED','###ON_TRACK');
