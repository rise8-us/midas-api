CREATE TABLE `issue` (
   `id` BIGINT NOT NULL,
   `title` TEXT NOT NULL,
   `description` TEXT,
   `creation_date` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
   `start_date` DATE,
   `due_date` DATE,
   `completed_at` DATETIME,
   `updated_at` DATETIME,
   `synced_at` DATETIME,
   `issue_iid` INT NOT NULL,
   `issue_uid` VARCHAR(255),
   `state` TEXT,
   `web_url` TEXT,
   `weight` BIGINT NOT NULL DEFAULT 1,
   `project_id` BIGINT,
   PRIMARY KEY (`id`),
   FOREIGN KEY (`project_id`) REFERENCES project(`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

DROP TABLE `epic`;

DELETE FROM deliverable WHERE epic_id IS NOT NULL;

CREATE TABLE `epic` (
    `id` BIGINT NOT NULL,
    `title` TEXT NOT NULL,
    `description` TEXT,
    `is_hidden` BIT(1) DEFAULT 0,
    `creation_date` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    `start_date` DATE,
    `start_date_from_inherited_source` DATE,
    `due_date` DATE,
    `due_date_from_inherited_source` DATE,
    `completed_at` DATETIME,
    `synced_at` DATETIME,
    `epic_iid` INT,
    `state` TEXT,
    `web_url` TEXT,
    `self_api` TEXT,
    `epic_issues_api` TEXT,
    `epic_uid` VARCHAR(255),
    `total_weight` BIGINT NOT NULL DEFAULT 0,
    `completed_weight` BIGINT NOT NULL DEFAULT 0,
    `product_id` BIGINT,
    PRIMARY KEY (`id`),
    FOREIGN KEY (`product_id`) REFERENCES product(`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;
