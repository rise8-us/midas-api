DROP PROCEDURE IF EXISTS COMPLETION_CONVERSION;

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

DELIMITER //
CREATE PROCEDURE COMPLETION_CONVERSION()
    BEGIN
        DECLARE finished INTEGER DEFAULT 0;
        DECLARE v_id BIGINT;
        DECLARE v_start_date DATE;
        DECLARE v_due_date DATE;
        DECLARE v_completed_at DATETIME;
        DECLARE v_completion_type VARCHAR(70);
        DECLARE v_value FLOAT;
        DECLARE v_target FLOAT;
        DECLARE v_measure_id BIGINT;
        DECLARE v_deliverable_id BIGINT;
        DECLARE v_epic_id BIGINT;
        DECLARE curMeasure CURSOR FOR SELECT start_date, due_date, completed_at, completion_type, value, target, id FROM `measure`;
        DECLARE curDeliverableNoEpic CURSOR FOR SELECT id FROM `deliverable` WHERE `epic_id` IS NULL;
        DECLARE curDeliverableWithEpic CURSOR FOR SELECT id, epic_id FROM `deliverable` WHERE `epic_id` IS NOT NULL;
        DECLARE CONTINUE HANDLER FOR NOT FOUND SET finished = 1;

    OPEN curMeasure;
        setCompletion: LOOP
            FETCH curMeasure INTO v_start_date, v_due_date, v_completed_at, v_completion_type, v_value, v_target, v_measure_id;
            IF finished = 1 THEN LEAVE setCompletion; END IF;
            SET v_id = (SELECT next_val FROM hibernate_sequence LIMIT 1);
            INSERT INTO `completion` (id, start_date, due_date, completed_at, completion_type, value, target, measure_id)
            VALUES (v_id, v_start_date, v_due_date, v_completed_at, v_completion_type, v_value, v_target, v_measure_id);
            UPDATE `hibernate_sequence` SET next_val = next_val + 1;
        END LOOP setCompletion;
    CLOSE curMeasure;

    SET finished = 0;

    OPEN curDeliverableNoEpic;
        setCompletion: LOOP
            FETCH curDeliverableNoEpic INTO v_deliverable_id;
            IF finished = 1 THEN LEAVE setCompletion; END IF;
            SET v_id = (SELECT next_val FROM hibernate_sequence LIMIT 1);
            INSERT INTO `completion` (id, completion_type, value, target, deliverable_id)
            VALUES (v_id, 'NUMBER', 0, (SELECT COUNT(id) FROM deliverable WHERE parent_id = v_deliverable_id), v_deliverable_id);
            UPDATE `hibernate_sequence` SET next_val = next_val + 1;
        END LOOP setCompletion;
    CLOSE curDeliverableNoEpic;

    SET finished = 0;

    OPEN curDeliverableWithEpic;
        setCompletion: LOOP
            FETCH curDeliverableWithEpic INTO v_deliverable_id, v_epic_id;
            IF finished = 1 THEN LEAVE setCompletion; END IF;
            SET v_id = (SELECT next_val FROM hibernate_sequence LIMIT 1);
            INSERT INTO `completion` (id, completion_type, value, target, deliverable_id, epic_id)
            VALUES (v_id, 'NUMBER', (SELECT `completed_weight` FROM `epic` WHERE `id` = v_epic_id), (SELECT `total_weight` FROM `epic` WHERE `id` = v_epic_id), v_deliverable_id, v_epic_id);
            UPDATE `hibernate_sequence` SET next_val = next_val + 1;
        END LOOP setCompletion;
    CLOSE curDeliverableWithEpic;

END//
DELIMITER ;

CALL COMPLETION_CONVERSION();

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
