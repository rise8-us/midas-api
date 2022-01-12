CREATE TABLE `app_user_metrics` (
   `id` DATE,
   `unique_logins` BIGINT NOT NULL,
   PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;