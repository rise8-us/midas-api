ALTER TABLE `assertion`
  ADD COLUMN `is_archived` BIT(1) DEFAULT 0,
  ADD COLUMN `completion_type` VARCHAR(70) DEFAULT 'STRING',
  ADD COLUMN `start_date` DATETIME,
  ADD COLUMN `due_date` DATETIME,
  ADD COLUMN `assigned_person_id` BIGINT,
  ADD FOREIGN KEY (`assigned_person_id`) REFERENCES user(`id`);

ALTER TABLE `product`
   ADD COLUMN `vision` TEXT,
   ADD COLUMN `mission` TEXT,
   ADD COLUMN `problem_statement` TEXT;

ALTER TABLE `comment`
    ADD COLUMN `edited_by_id` BIGINT;

ALTER TABLE `team`
  ADD COLUMN `product_manager_id` BIGINT,
  ADD COLUMN `designer_id` BIGINT,
  ADD COLUMN `tech_lead_id` BIGINT,
  ADD FOREIGN KEY (`product_manager_id`) REFERENCES user(`id`),
  ADD FOREIGN KEY (`designer_id`) REFERENCES user(`id`),
  ADD FOREIGN KEY (`tech_lead_id`) REFERENCES user(`id`);

CREATE TABLE `product_team` (
   `product_id` BIGINT NOT NULL,
   `team_id` BIGINT NOT NULL
  )  ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

  CREATE TABLE `persona` (
     `id` BIGINT NOT NULL,
     `title` VARCHAR(70) NOT NULL,
     `position` INT,
     `creation_date` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
     `description` TEXT,
     `is_supported` BIT(1) NOT NULL DEFAULT 0,
     `product_id` BIGINT NOT NULL,
     PRIMARY KEY (`id`),
     FOREIGN KEY (`product_id`) REFERENCES product(`id`)
   ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

   CREATE TABLE `roadmap` (
      `id` BIGINT NOT NULL,
      `title` VARCHAR(70) NOT NULL,
      `position` INT,
      `creation_date` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
      `target_date` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
      `description` TEXT,
      `status` VARCHAR(70) DEFAULT "NOT_STARTED",
      `product_id` BIGINT NOT NULL,
      PRIMARY KEY (`id`),
      FOREIGN KEY (`product_id`) REFERENCES product(`id`)
    ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

    CREATE TABLE `feature` (
       `id` BIGINT NOT NULL,
       `title` VARCHAR(70) NOT NULL,
       `position` INT,
       `creation_date` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
       `description` TEXT,
       `product_id` BIGINT NOT NULL,
       PRIMARY KEY (`id`),
       FOREIGN KEY (`product_id`) REFERENCES product(`id`)
     ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;
