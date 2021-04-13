CREATE TABLE `applications` (
   `id` BIGINT NOT NULL,
   `name` VARCHAR(70) NOT NULL,
   `creation_date` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
   `description` TEXT,
   `is_archived` BIT(1) NOT NULL DEFAULT 0,
   `product_manager_id` BIGINT,
   `portfolio_id` BIGINT,
   PRIMARY KEY (`id`),
   FOREIGN KEY (`product_manager_id`) REFERENCES users(`id`),
   FOREIGN KEY (`portfolio_id`) REFERENCES portfolios(`id`)
 ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

 ALTER TABLE `portfolios`
     ADD COLUMN `application_id` BIGINT,
     DROP FOREIGN KEY `portfolios_ibfk_1`,
     CHANGE COLUMN `lead` `portfolio_manager_id` BIGINT,
     ADD FOREIGN KEY (`portfolio_manager_id`) REFERENCES users(`id`),
     ADD FOREIGN KEY (`application_id`) REFERENCES applications(`id`);

 CREATE TABLE `applications_tags`(
 `tag_id` BIGINT NOT NULL,
  `application_id` BIGINT NOT NULL
   ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

   ALTER TABLE `projects`
    DROP FOREIGN KEY `projects_ibfk_2`,
    CHANGE COLUMN `portfolio_id` `application_id` BIGINT,
    ADD FOREIGN KEY (`application_id`) REFERENCES applications(`id`);

