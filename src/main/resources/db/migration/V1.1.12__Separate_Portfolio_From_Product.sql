CREATE TABLE `portfolio` (
     `id` BIGINT NOT NULL,
     `creation_date` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
     `name` VARCHAR(120) NOT NULL,
     `description` TEXT,
     `is_archived` BIT(1) NOT NULL DEFAULT 0,
     `gitlab_group_id` INT,
     `source_control_id` BIGINT,
     `vision` TEXT,
     `mission` TEXT,
     `problem_statement` TEXT,
     PRIMARY KEY (`id`),
     FOREIGN KEY (`source_control_id`) REFERENCES source_control(`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE `personnel` (
     `id` BIGINT NOT NULL,
     `creation_date` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
     `owner_id` BIGINT,
     `product_id` BIGINT,
     `type` TEXT,
     PRIMARY KEY (`id`),
     FOREIGN KEY (`owner_id`) REFERENCES user(`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

ALTER TABLE `product_team` ADD `personnel_id` BIGINT NOT NULL;
ALTER TABLE `product_team` RENAME `personnel_team`;

CREATE TABLE `personnel_user_admin` (
    `personnel_id` BIGINT NOT NULL,
    `user_id` BIGINT NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE `portfolio_personnel` (
    `portfolio_id` BIGINT NOT NULL,
    `personnel_id` BIGINT NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE `product_personnel` (
    `product_id` BIGINT NOT NULL,
    `personnel_id` BIGINT NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE `product_portfolio` (
     `product_id` BIGINT NOT NULL,
     `portfolio_id` BIGINT NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

DELIMITER //
CREATE PROCEDURE PERSONNEL_CREATION()
    BEGIN
        DECLARE finished INTEGER DEFAULT 0;
        DECLARE v_id BIGINT;
        DECLARE v_product_id BIGINT;
        DECLARE v_creation_date DATE;
        DECLARE v_owner_id BIGINT;
        DECLARE v_type TEXT;

        DECLARE products CURSOR FOR SELECT id, owner_id, creation_date, type FROM `product`;
        DECLARE CONTINUE HANDLER FOR NOT FOUND SET finished = 1;

    OPEN products;
        setPersonnel: LOOP
            FETCH products INTO v_product_id, v_owner_id, v_creation_date, v_type;
            IF finished = 1 THEN LEAVE setPersonnel; END IF;
            SET v_id = (SELECT next_val FROM hibernate_sequence LIMIT 1);
            INSERT INTO `personnel` (id, creation_date, owner_id, product_id, type)
            VALUES (v_id, v_creation_date, v_owner_id, v_product_id, v_type);
            UPDATE `personnel_team` SET `personnel_id` = v_id WHERE `product_id` = v_product_id;
            UPDATE `hibernate_sequence` SET next_val = next_val + 1;
        END LOOP setPersonnel;
    CLOSE products;
END//
DELIMITER ;

CALL PERSONNEL_CREATION();
DROP PROCEDURE PERSONNEL_CREATION;

INSERT INTO `portfolio` (id, creation_date, name, description, is_archived, gitlab_group_id, source_control_id, vision, mission, problem_statement)
    SELECT id, creation_date, name, description, is_archived, gitlab_group_id, source_control_id, vision, mission, problem_statement
    FROM `product` WHERE `type` = 'PORTFOLIO';

INSERT INTO `portfolio_personnel` (portfolio_id, personnel_id)
    SELECT product_id, id
    FROM `personnel`
    WHERE `type` = 'PORTFOLIO';

INSERT INTO `product_personnel` (product_id, personnel_id)
    SELECT product_id, id
    FROM `personnel`
    WHERE `type` = 'PRODUCT';

INSERT INTO `product_portfolio` (product_id, portfolio_id)
    SELECT id, parent_id
    FROM `product`
    WHERE `parent_id` IS NOT NULL;

ALTER TABLE `personnel`
    DROP COLUMN `type`,
    DROP COLUMN `product_id`;

ALTER TABLE `personnel_team`
    DROP COLUMN `product_id`;

ALTER TABLE `product`
    DROP CONSTRAINT `product_ibfk_1`,
    DROP CONSTRAINT `product_ibfk_3`,
    DROP COLUMN `owner_id`,
    DROP COLUMN `parent_id`;

DELETE FROM `product`
    WHERE `type` = 'PORTFOLIO';

ALTER TABLE `product`
    DROP COLUMN `type`;
