ALTER TABLE `assertion`
    MODIFY COLUMN `status` BIGINT DEFAULT 0,
    MODIFY COLUMN `objective_id` BIGINT,
    ADD COLUMN `product_id` BIGINT NOT NULL;

UPDATE assertion a, objective o SET a.product_id = o.product_id where a.objective_id = o.id;

ALTER TABLE `product`
    DROP COLUMN `vision_statement`;

DELIMITER //
DROP PROCEDURE IF EXISTS OBJECTIVETOASSERTION//
CREATE PROCEDURE OBJECTIVETOASSERTION()
BEGIN
    DECLARE finished INTEGER DEFAULT 0;
    DECLARE v_id BIGINT;
    DECLARE v_text TEXT;
    DECLARE v_p_id BIGINT;
    DECLARE v_c_id BIGINT;
    DECLARE curObjective CURSOR FOR SELECT id, text, product_id, created_by_id FROM `objective`;
    DECLARE CONTINUE HANDLER FOR NOT FOUND SET finished = 1;

   OPEN curObjective;
       setAssertion: LOOP
       FETCH curObjective INTO v_id, v_text, v_p_id, v_c_id;
               IF finished = 1 THEN LEAVE setAssertion;
               END IF;
       INSERT INTO `assertion` (id, type, text, product_id, created_by_id, status)
          VALUES (v_id, 0, v_text, v_p_id, v_c_id, 0);
       UPDATE `assertion` SET parent_id = v_id where type = 1 and objective_id = v_id;

    END LOOP setAssertion;
    CLOSE curObjective;
END//
DELIMITER ;
CALL OBJECTIVETOASSERTION();
DROP PROCEDURE OBJECTIVETOASSERTION;

ALTER TABLE `assertion`
    DROP FOREIGN KEY `assertion_ibfk_1`,
    DROP COLUMN `objective_id`,
    ADD COLUMN `completed_date` DATETIME,
    ADD FOREIGN KEY (`product_id`) REFERENCES product(`id`);

ALTER TABLE `objective`
    DROP FOREIGN KEY `objective_ibfk_1`,
    DROP FOREIGN KEY `objective_ibfk_2`;
DROP TABLE `objective`;
