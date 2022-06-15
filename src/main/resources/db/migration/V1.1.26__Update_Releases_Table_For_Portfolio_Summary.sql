DROP TABLE `releases`;

CREATE TABLE `releases` (
    `id` BIGINT NOT NULL,
    `name` VARCHAR(120) NOT NULL,
    `description` TEXT,
    `tag_name` VARCHAR(120),
    `creation_date` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    `released_at` DATETIME,
    `project_id` BIGINT NOT NULL,
    `uid` VARCHAR(255) ,
    FOREIGN KEY (`project_id`) REFERENCES project(`id`),
    PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

DROP TABLE `release_deliverable`;

ALTER TABLE `project`
    ADD COLUMN `release_sync_status` varchar(70) DEFAULT 'SYNCED',
    ADD COLUMN `issue_sync_status` varchar(70) DEFAULT 'SYNCED';