ALTER TABLE `teams`
    ADD COLUMN `description` TEXT,
    MODIFY COLUMN `gitlab_group_id` BIGINT;
