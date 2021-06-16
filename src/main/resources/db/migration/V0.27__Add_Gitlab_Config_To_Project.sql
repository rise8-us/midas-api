ALTER TABLE `project`
    ADD COLUMN `gitlab_config_id` BIGINT,
    ADD FOREIGN KEY (`gitlab_config_id`) REFERENCES gitlab_config(`id`);

