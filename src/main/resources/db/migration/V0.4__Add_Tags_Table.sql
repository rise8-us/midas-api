CREATE TABLE `tags` (
   `id` BIGINT NOT NULL,
   `label` TINYTEXT NOT NULL,
   `description` TEXT,
   `color` TINYTEXT DEFAULT ('#969696'),
   `creation_date` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
   PRIMARY KEY (`id`)
 ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

 CREATE TABLE `product_tags` (
   `tag_id` BIGINT NOT NULL,
   `product_id` BIGINT NOT NULL
  ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;