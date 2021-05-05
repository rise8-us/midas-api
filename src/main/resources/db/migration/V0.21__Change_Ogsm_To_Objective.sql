RENAME TABLE `ogsm` TO `objective`;

ALTER TABLE `assertion` CHANGE COLUMN `ogsm_id` `objective_id` BIGINT NOT NULL;
