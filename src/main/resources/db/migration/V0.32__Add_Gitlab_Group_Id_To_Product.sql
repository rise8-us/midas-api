ALTER TABLE `product`
    ADD COLUMN `gitlab_group_id` INT;

ALTER TABLE `project`
    MODIFY `gitlab_project_id` INT;
