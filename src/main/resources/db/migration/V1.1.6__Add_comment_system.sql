ALTER TABLE `user` ADD COLUMN `user_type` VARCHAR(70) NOT NULL DEFAULT 'ACTIVE';

UPDATE `user` SET `user_type` = 'ACTIVE' WHERE `is_disabled` = 0;
UPDATE `user` SET `user_type` = 'DISABLED' WHERE `is_disabled` = 1;

INSERT INTO `user` (id, dod_id, keycloak_uid, user_type, username, display_name, roles)
    VALUES(
           (SELECT `next_val` from hibernate_sequence),
           9999999999,
           'non-keycloak-comment-system',
           'SYSTEM',
           'comment-system',
           'System Generated',
            1
        );

UPDATE `hibernate_sequence` SET `next_val` = (`next_val` + 1);