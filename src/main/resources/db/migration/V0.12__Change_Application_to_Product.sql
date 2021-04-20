RENAME TABLE `applications` TO `products`;
ALTER TABLE `portfolios` CHANGE `application_id` `product_id` BIGINT;
ALTER TABLE `applications_tags` CHANGE `application_id` `product_id` BIGINT;
RENAME TABLE `applications_tags` TO `products_tags`;
ALTER TABLE `projects` CHANGE `application_id` `product_id` BIGINT;
