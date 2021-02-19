CREATE TABLE `hibernate_sequence` (`next_val` bigint(20)) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;
INSERT INTO `hibernate_sequence` (next_val) VALUES(2);

-- make user table plural after overrides implemented
CREATE TABLE `users` (
  `id` bigint(20) NOT NULL,
  `dod_id` bigint(20),
  `keycloak_uid` VARCHAR(100) NOT NULL,
  `username` VARCHAR(100) NOT NULL,
  `email` VARCHAR(100),
  `display_name` VARCHAR(100),
  `roles` bigint(20) NOT NULL DEFAULT 0,
  `creation_date` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `is_disabled` BIT(1) NOT NULL DEFAULT 0,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

INSERT INTO `users` (id, dod_id, keycloak_uid, username, roles)
  VALUES(1, 9999999999, 'keycloak-sub-123', 'localuser', 1);