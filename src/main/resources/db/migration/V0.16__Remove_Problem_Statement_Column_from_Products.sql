ALTER TABLE `products`
    DROP COLUMN `problem_statement`;

RENAME TABLE `products` TO `product`;
RENAME TABLE `portfolios` TO `portfolio`;
RENAME TABLE `products_tags` TO `product_tag`;
RENAME TABLE `project_tags` TO `project_tag`;
RENAME TABLE `projects` TO `project`;
RENAME TABLE `tags` TO `tag`;
RENAME TABLE `teams` TO `team`;
RENAME TABLE `users` TO `user`;
RENAME TABLE `announcements` TO `announcement`;

CREATE TABLE `problem` (
   `id` BIGINT NOT NULL,
   `problem` TEXT NOT NULL,
   `creation_date` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
   `is_current` BIT(1) NOT NULL DEFAULT 1,
   `created_by_id` BIGINT,
   PRIMARY KEY (`id`),
   FOREIGN KEY (`created_by_id`) REFERENCES user(`id`)
 ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE `product_problem` (
    `product_id` BIGINT NOT NULL,
    `problem_id` BIGINT NOT NULL
    ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE `portfolio_problem` (
   `portfolio_id` BIGINT NOT NULL,
   `problem_id` BIGINT NOT NULL
  ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;