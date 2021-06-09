CREATE TABLE `gitlab_config` (
  `id` BIGINT NOT NULL,
  `name` VARCHAR(255) NOT NULL,
  `description` TEXT,
  `base_url` VARCHAR(255),
  `token` VARCHAR(255),
  `creation_date` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;
