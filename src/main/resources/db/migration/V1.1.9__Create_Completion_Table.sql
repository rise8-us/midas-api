CREATE TABLE `completion` (
   `id` BIGINT NOT NULL,
   `creation_date` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
   `start_date` DATE,
   `due_date` DATE,
   `completed_at` DATETIME,
   `completion_type` VARCHAR(70) NOT NULL DEFAULT 'BINARY',
   `value` FLOAT NOT NULL DEFAULT 0,
   `target` FLOAT NOT NULL DEFAULT 1,
   `measure_id` BIGINT,
   `deliverable_id` BIGINT,
   `epic_id` BIGINT,
   PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE `completion_measure` (
    `completion_id` BIGINT NOT NULL,
    `measure_id` BIGINT NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE `completion_deliverable` (
    `completion_id` BIGINT NOT NULL,
    `deliverable_id` BIGINT NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE `completion_gitlab_epic` (
    `completion_id` BIGINT NOT NULL,
    `epic_id` BIGINT NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE `completion_gitlab_issue` (
    `completion_id` BIGINT NOT NULL,
    `issue_id` BIGINT NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

DROP FUNCTION IF EXISTS nextID;
    DELIMITER $$
    CREATE FUNCTION nextID()
        RETURNS BIGINT(20)
        NOT DETERMINISTIC
    BEGIN
        DECLARE response BIGINT(20);
        SET response = (SELECT `next_val` FROM `hibernate_sequence` LIMIT 1);
        UPDATE `hibernate_sequence` SET `next_val` = `next_val` + 1;
        RETURN (response);
    END $$
DELIMITER ;

INSERT INTO `completion` (id, start_date, due_date, completed_at, completion_type, value, target, measure_id)
    SELECT nextID(), start_date, due_date, completed_at, completion_type, value, target, id
    FROM `measure`;

INSERT INTO `completion` (id, completion_type, value, target, deliverable_id)
    SELECT nextID(), 'NUMBER', 0, 1, id
    FROM `deliverable`
    WHERE `epic_id` IS NULL;

INSERT INTO `completion` (id, completion_type, value, target, deliverable_id, epic_id)
    SELECT nextID(), 'NUMBER', (SELECT `completed_weight` FROM `epic` WHERE `id` = `epic_id`), (SELECT `total_weight` FROM `epic` WHERE `id` = `epic_id`), id, epic_id
    FROM `deliverable`
    WHERE `epic_id` IS NOT NULL;

INSERT INTO `completion_measure` (completion_id, measure_id)
    SELECT id, measure_id
    FROM `completion`
    WHERE `measure_id` IS NOT NULL;

INSERT INTO `completion_deliverable` (completion_id, deliverable_id)
    SELECT id, deliverable_id
    FROM `completion`
    WHERE `deliverable_id` IS NOT NULL;

INSERT INTO `completion_gitlab_epic` (completion_id, epic_id)
    SELECT id, epic_id
    FROM `completion`
    WHERE `epic_id` IS NOT NULL;

ALTER TABLE `completion`
    DROP COLUMN `measure_id`,
    DROP COLUMN `deliverable_id`,
    DROP COLUMN `epic_id`;

ALTER TABLE `measure`
    DROP COLUMN `start_date`,
    DROP COLUMN `due_date`,
    DROP COLUMN `completed_at`,
    DROP COLUMN `completion_type`,
    DROP COLUMN `value`,
    DROP COLUMN `target`;

ALTER TABLE `deliverable`
    DROP COLUMN `epic_id`;

ALTER TABLE `epic`
    DROP COLUMN `self_api`,
    DROP COLUMN `epic_issues_api`;
