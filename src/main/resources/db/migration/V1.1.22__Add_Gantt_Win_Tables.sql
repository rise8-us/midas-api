CREATE TABLE `gantt_win` (
    `id` BIGINT NOT NULL,
    `creation_date` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    `due_date` DATE,
    `title` VARCHAR(120) NOT NULL,
    `description` TEXT,
    PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE `gantt_portfolio_win` (
    `portfolio_id` BIGINT NOT NULL,
    `win_id` BIGINT NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;