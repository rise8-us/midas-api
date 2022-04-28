CREATE TABLE `capability_deliverable` (
    `capability_id` BIGINT NOT NULL,
    `deliverable_id` BIGINT NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE `performance_measure_deliverable` (
    `performance_measure_id` BIGINT NOT NULL,
    `deliverable_id` BIGINT NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE `capability_mission_thread` (
    `capability_id` BIGINT NOT NULL,
    `mission_thread_id` BIGINT NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE `capability_performance_measure` (
    `capability_id` BIGINT NOT NULL,
    `performance_measure_id` BIGINT NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE `gantt_target_deliverables` (
    `target_id` BIGINT NOT NULL,
    `deliverable_id` BIGINT NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

INSERT INTO `capability_deliverable` (capability_id, deliverable_id)
    SELECT c.id, d.id
    FROM `deliverable` d
    LEFT JOIN `capability` c
    ON c.id=d.capability_id
    WHERE d.capability_id IS NOT NULL;

INSERT INTO `performance_measure_deliverable` (performance_measure_id, deliverable_id)
    SELECT c.id, d.id
    FROM `deliverable` d
    LEFT JOIN `performance_measure` c
    ON c.id=d.performance_measure_id
    WHERE d.performance_measure_id IS NOT NULL;

INSERT INTO `capability_performance_measure` (capability_id, performance_measure_id)
    SELECT c.id, p.id
    FROM `performance_measure` p
    LEFT JOIN `capability` c
    ON c.id=p.capability_id
    WHERE p.capability_id IS NOT NULL;

ALTER TABLE `deliverable`
    DROP CONSTRAINT `deliverable_ibfk_3`,
    DROP COLUMN `capability_id`;

ALTER TABLE `deliverable`
    DROP CONSTRAINT `deliverable_ibfk_1`,
    DROP COLUMN `performance_measure_id`;

ALTER TABLE `capability`
    DROP CONSTRAINT `capability_ibfk_1`,
    DROP COLUMN `mission_thread_id`;

ALTER TABLE `performance_measure`
    DROP CONSTRAINT `performance_measure_ibfk_1`,
    DROP COLUMN `capability_id`;



