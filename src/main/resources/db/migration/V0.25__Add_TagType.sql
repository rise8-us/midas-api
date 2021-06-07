ALTER TABLE `tag`
    ADD COLUMN `tag_type` VARCHAR(70) DEFAULT 'ALL' NOT NULL;

UPDATE `tag` SET tag_type = 'ALL' where tag_type IS NULL;