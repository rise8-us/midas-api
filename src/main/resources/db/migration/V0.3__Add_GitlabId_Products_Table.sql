ALTER TABLE `products`
    ADD `gitlab_project_id` BIGINT(20) NOT NULL;

ALTER TABLE `products`
    RENAME COLUMN `is_disabled` TO `is_archived`;
