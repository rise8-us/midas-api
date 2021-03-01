CREATE TABLE `products` (
  `id` BIGINT(20) NOT NULL,
  `name` VARCHAR(70) NOT NULL,
  `description` TEXT,
  `creation_date` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `is_disabled` BIT(1) NOT NULL DEFAULT 0,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;
