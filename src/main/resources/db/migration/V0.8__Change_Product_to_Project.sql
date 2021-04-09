RENAME TABLE `products` TO `projects`;
RENAME TABLE `product_tags` TO `project_tags`;
ALTER TABLE `project_tags` CHANGE `product_id` `project_id` BIGINT NOT NULL;
ALTER TABLE `projects` CHANGE `product_journey_map` `project_journey_map` BIGINT NOT NULL DEFAULT 0;