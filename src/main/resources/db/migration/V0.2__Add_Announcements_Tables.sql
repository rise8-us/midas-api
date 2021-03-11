 CREATE TABLE `announcements` (
   `id` BIGINT NOT NULL,
   `message` TEXT NOT NULL,
   `creation_date` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
   PRIMARY KEY (`id`)
 ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

ALTER TABLE `users`
    ADD COLUMN `last_login` DATETIME;