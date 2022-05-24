ALTER TABLE `epic` ADD `portfolio_id` BIGINT NULL;
ALTER TABLE `epic` ADD FOREIGN KEY (`portfolio_id`) REFERENCES portfolio(`id`);