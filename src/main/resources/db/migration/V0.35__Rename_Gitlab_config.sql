RENAME TABLE `gitlab_config` TO `source_control`;

ALTER TABLE `product`
    CHANGE COLUMN gitlab_config_id source_control_id BIGINT;

ALTER TABLE `project`
    CHANGE COLUMN gitlab_config_id source_control_id BIGINT;
