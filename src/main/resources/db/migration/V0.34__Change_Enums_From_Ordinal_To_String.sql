UPDATE `assertion`
    SET `type` = "OBJECTIVE" WHERE `type` = "0";

UPDATE `assertion`
    SET `type` = "GOAL" WHERE `type` = "1";

UPDATE `assertion`
    SET `type` = "STRATEGY" WHERE `type` = "2";

UPDATE `assertion`
    SET `type` = "MEASURE" WHERE `type` = "3";