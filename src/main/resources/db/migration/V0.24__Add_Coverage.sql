CREATE TABLE `coverage` (
  `id` BIGINT NOT NULL,
  `job_id` INT,
  `project_id` BIGINT NOT NULL,
  `test_coverage` FLOAT NOT NULL,
  `coverage_change` FLOAT NOT NULL,
  `maintainability_rating` VARCHAR(100) NOT NULL,
  `reliability_rating` VARCHAR(100) NOT NULL,
  `security_rating` VARCHAR(100) NOT NULL,
  `creation_date` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  FOREIGN KEY (`project_id`) REFERENCES project(`id`),
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;
