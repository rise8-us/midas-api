ALTER TABLE `assertion`
    ADD COLUMN `parent_id` BIGINT,
    ADD FOREIGN KEY (`parent_id`) REFERENCES assertion(`id`);
