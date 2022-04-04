CREATE TABLE `gantt_target` (
     `id` BIGINT NOT NULL,
     `creation_date` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
     `start_date` DATE,
     `due_date` DATE,
     `title` VARCHAR(120) NOT NULL,
     `description` TEXT,
     PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE `gantt_milestone` (
     `id` BIGINT NOT NULL,
     `creation_date` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
     `due_date` DATE,
     `title` VARCHAR(120) NOT NULL,
     `description` TEXT,
     PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE `gantt_event` (
    `id` BIGINT NOT NULL,
    `creation_date` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    `start_date` DATE,
    `due_date` DATE,
    `title` VARCHAR(120) NOT NULL,
    `description` TEXT,
    `location` TEXT,
    PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE `gantt_event_user` (
    `event_id` BIGINT NOT NULL,
    `user_id` BIGINT NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE `gantt_portfolio_target` (
    `portfolio_id` BIGINT NOT NULL,
    `target_id` BIGINT NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE `gantt_portfolio_milestone` (
     `portfolio_id` BIGINT NOT NULL,
     `milestone_id` BIGINT NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE `gantt_portfolio_event` (
     `portfolio_id` BIGINT NOT NULL,
     `event_id` BIGINT NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;
