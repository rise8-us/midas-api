ALTER TABLE `portfolios` MODIFY COLUMN `product_id` BIGINT;
ALTER TABLE `products_tags` MODIFY COLUMN `product_id` BIGINT;
ALTER TABLE `projects` MODIFY COLUMN `product_id` BIGINT;
