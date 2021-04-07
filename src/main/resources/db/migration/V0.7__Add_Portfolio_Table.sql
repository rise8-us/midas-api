CREATE TABLE `portfolios` (
   `id` BIGINT NOT NULL,
   `name` VARCHAR(70) NOT NULL,
   `creation_date` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
   `description` TEXT,
   `is_archived` BIT(1) NOT NULL DEFAULT 0,
   `lead` BIGINT,
   PRIMARY KEY (`id`),
   FOREIGN KEY (`lead`) REFERENCES users(`id`)
 ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

 ALTER TABLE `products`
     ADD COLUMN `portfolio_id` BIGINT,
     ADD FOREIGN KEY (`portfolio_id`) REFERENCES portfolios(`id`);
