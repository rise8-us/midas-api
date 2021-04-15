   ALTER TABLE `tags`
    ADD COLUMN `created_by_id` BIGINT,
    ADD FOREIGN KEY (`created_by_id`) REFERENCES users(`id`);

