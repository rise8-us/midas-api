ALTER TABLE `users`
    MODIFY COLUMN `keycloak_uid` VARCHAR(100) NOT NULL UNIQUE;