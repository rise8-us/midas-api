CREATE TABLE `user_team` (
  `user_id` BIGINT(20) NOT NULL,
  `team_id` BIGINT(20) NOT NULL
 ) ENGINE=InnoDB DEFAULT CHARSET=latin1;

CREATE TABLE `teams` (
  `id` BIGINT(20) NOT NULL,
  `name` VARCHAR(70) NOT NULL,
  `creation_date` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `is_archived` BIT(1) NOT NULL DEFAULT 0,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

ALTER TABLE `products`
   ADD COLUMN `team_id` BIGINT(20),
   ADD FOREIGN KEY (`team_id`) REFERENCES teams(`id`);
