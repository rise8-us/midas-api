CREATE TABLE `user_team` (
  `user_id` BIGINT NOT NULL,
  `team_id` BIGINT NOT NULL
 ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

 CREATE TABLE `teams` (
   `id` BIGINT NOT NULL,
   `name` VARCHAR(70) NOT NULL,
   `creation_date` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
   `is_archived` BIT(1) NOT NULL DEFAULT 0,
   `gitlab_group_id` BIGINT NOT NULL,
   PRIMARY KEY (`id`)
 ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE `products` (
  `id` BIGINT NOT NULL,
  `name` VARCHAR(70) NOT NULL,
  `team_id` BIGINT,
  `description` TEXT,
  `creation_date` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `is_archived` BIT(1) NOT NULL DEFAULT 0,
  `gitlab_project_id` BIGINT NOT NULL,
  PRIMARY KEY (`id`),
  FOREIGN KEY (`team_id`) REFERENCES teams(`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;
