CREATE TABLE `measure` (
   `id` BIGINT NOT NULL,
   `assertion_id` BIGINT,
   `creation_date` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
   `start_date` DATE,
   `due_date` DATE,
   `completed_at` DATETIME,
   `completion_type` VARCHAR(70) NOT NULL DEFAULT 'BINARY',
   `value` FLOAT NOT NULL DEFAULT 0,
   `target` FLOAT NOT NULL DEFAULT 1,
   `text` TEXT,

   PRIMARY KEY (`id`),
   FOREIGN KEY (`assertion_id`) REFERENCES assertion(`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

ALTER TABLE `comment`
    CHANGE COLUMN `assertion_id` `assertion_id` BIGINT;

UPDATE `assertion` SET start_date=CAST(`creation_date` AS DATE) WHERE start_date IS NULL;
UPDATE `assertion` SET completed_date=CURRENT_TIMESTAMP WHERE status='COMPLETED';
UPDATE `assertion` s LEFT JOIN `assertion` g ON s.parent_id=g.id SET s.parent_id=g.parent_id WHERE s.type='STRATEGY';

INSERT INTO `measure` (id, start_date, due_date, completed_at, assertion_id, `text`)
    SELECT id, CAST(start_date AS DATE), due_date, completed_date, parent_id, `text`
    FROM `assertion`
    WHERE type='GOAL' OR type='MEASURE';

CREATE TABLE `assertion_comment` (
    `assertion_id` BIGINT NOT NULL,
    `comment_id` BIGINT NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE `measure_comment` (
    `measure_id` BIGINT NOT NULL,
    `comment_id` BIGINT NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

INSERT INTO `assertion_comment` (assertion_id, comment_id)
    SELECT a.id, c.id FROM `assertion` a RIGHT JOIN `comment` c ON a.id=c.assertion_id
    WHERE a.type='OBJECTIVE' OR a.type='STRATEGY';

INSERT INTO `measure_comment` (measure_id, comment_id)
    SELECT a.id, c.id FROM `assertion` a RIGHT JOIN `comment` c ON a.id=c.assertion_id
    WHERE a.type='GOAL' OR a.type='MEASURE';

ALTER TABLE `comment`
    DROP CONSTRAINT `comment_ibfk_1`,
    DROP COLUMN assertion_id;

UPDATE `assertion` SET parent_id=NULL WHERE type='GOAL' OR type='MEASURE';
DELETE FROM `assertion` WHERE type='GOAL' OR type='MEASURE';

ALTER TABLE `assertion`
    DROP COLUMN `type`,
    DROP COLUMN `completion_type`,
    ADD COLUMN `inherited_from` BIGINT,
    RENAME COLUMN `completed_date` TO `completed_at`,
    MODIFY COLUMN `due_date` DATE,
    MODIFY COLUMN `start_date` DATE;

ALTER TABLE `product`
    ADD COLUMN `roadmap_type` VARCHAR(70) NOT NULL DEFAULT 'MANUAL';

UPDATE `product`
    SET roadmap_type = 'GITLAB';

CREATE TABLE `feedback` (
   `id` BIGINT NOT NULL,
   `creation_date` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
   `created_by_id` BIGINT NOT NULL,
   `edited_by_id` BIGINT,
   `edited_at` DATETIME,
   `rating` VARCHAR(70) NOT NULL DEFAULT 'AVERAGE',
   `notes` TEXT,
   `related_to` TEXT NOT NULL,

   PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

ALTER TABLE `epic` RENAME COLUMN `closed_at` TO `completed_at`;

ALTER TABLE `roadmap`
    ADD COLUMN `start_date` DATE,
    ADD COLUMN `due_date` DATE,
    ADD COLUMN `completed_at` DATETIME,
    ADD COLUMN `is_hidden` BIT(1) DEFAULT 0 NOT NULL;

UPDATE `roadmap` SET `start_date`=CAST(`creation_date` AS DATE);
UPDATE `roadmap` SET `due_date`=CAST(`target_date` AS DATE);
UPDATE `roadmap` SET `completed_at`=`target_date` WHERE `status`='COMPLETE';

ALTER TABLE `roadmap`
    DROP COLUMN `target_date`,
    DROP COLUMN `position`;
