CREATE TABLE `epic` (
    `id` BIGINT NOT NULL,
    `epic_uid` BIGINT NOT NULL,
    `title` TEXT NOT NULL,
    `description` TEXT,
    `creation_date` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    `start_date` DATE,
    `start_date_from_inherited_source` DATE,
    `due_date` DATE,
    `due_date_from_inherited_source` DATE,
    `closed_at` DATETIME,
    `synced_at` DATETIME,
    `epic_iid` INT,
    `state` TEXT,
    `web_url` TEXT,
    `self_api` TEXT,
    `epic_issues_api` TEXT,
    `product_id` BIGINT NOT NULL,
    FOREIGN KEY (`product_id`) REFERENCES product(`id`),
    PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE `mission_thread` (
    `id` BIGINT NOT NULL,
    `title` TEXT NOT NULL,
    `is_archived` BIT(1) DEFAULT 0,
    `creation_date` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE `capability` (
    `id` BIGINT NOT NULL,
    `title` TEXT NOT NULL,
    `description` TEXT,
    `is_archived` BIT(1) DEFAULT 0,
    `creation_date` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    `reference_id` INT,
    `mission_thread_id` BIGINT,
    FOREIGN KEY (`mission_thread_id`) REFERENCES mission_thread(`id`),
    PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE `performance_measure` (
    `id` BIGINT NOT NULL,
    `title` TEXT NOT NULL,
    `is_archived` BIT(1) DEFAULT 0,
    `creation_date` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    `reference_id` INT,
    `capability_id` BIGINT,
    FOREIGN KEY (`capability_id`) REFERENCES capability(`id`),
    PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE `deliverable` (
    `id` BIGINT NOT NULL,
    `title` TEXT NOT NULL,
    `is_archived` BIT(1) DEFAULT 0,
    `creation_date` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    `status` VARCHAR(70) DEFAULT 'NOT_STARTED',
    `reference_id` INT,
    `position` INT,
    `parent_id` BIGINT,
    `product_id` BIGINT,
    `epic_id` BIGINT,
    `performance_measure_id` BIGINT,
    `capability_id` BIGINT,
    `assigned_to_id` BIGINT,
    FOREIGN KEY (`performance_measure_id`) REFERENCES performance_measure(`id`),
    FOREIGN KEY (`parent_id`) REFERENCES deliverable(`id`),
    FOREIGN KEY (`capability_id`) REFERENCES capability(`id`),
    FOREIGN KEY (`product_id`) REFERENCES product(`id`),
    PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE `releases` (
    `id` BIGINT NOT NULL,
    `title` TEXT NOT NULL,
    `is_archived` BIT(1) DEFAULT 0,
    `creation_date` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    `target_date` DATETIME,
    `status` VARCHAR(70) DEFAULT 'NOT_STARTED',
    PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE `release_deliverable` (
    `release_id` BIGINT NOT NULL,
    `deliverable_id` BIGINT NOT NULL
)  ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

ALTER TABLE `product`
    CHANGE COLUMN `product_manager_id` `owner_id` BIGINT;

ALTER TABLE `project`
    ADD COLUMN `owner_id` BIGINT;

ALTER TABLE `feature`
    CHANGE COLUMN `title` `title` VARCHAR(255) NOT NULL;

ALTER TABLE `roadmap`
    CHANGE COLUMN `title` `title`  VARCHAR(255) NOT NULL;