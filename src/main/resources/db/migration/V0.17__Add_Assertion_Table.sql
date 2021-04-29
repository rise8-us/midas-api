ALTER TABLE `product`
    DROP FOREIGN KEY `product_ibfk_2`,
    DROP COLUMN portfolio_id,
    ADD COLUMN type VARCHAR(70),
    ADD COLUMN parent_id BIGINT,
    ADD FOREIGN KEY (`parent_id`) REFERENCES product(`id`);

DROP TABLE `portfolio`;
DROP TABLE `portfolio_problem`;

CREATE TABLE `ogsm` (
    `id` BIGINT NOT NULL,
    `product_id` BIGINT NOT NULL,
    `created_by_id` BIGINT NOT NULL,
    `creation_date` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    `completed_date` DATETIME,
    PRIMARY KEY (`id`),
    FOREIGN KEY (`product_id`) REFERENCES product(`id`),
    FOREIGN KEY (`created_by_id`) REFERENCES user(`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE `assertion` (
   `id` BIGINT NOT NULL,
   `ogsm_id` BIGINT NOT NULL,
   `created_by_id` BIGINT NOT NULL,
   `type` VARCHAR(70) NOT NULL,
   `text` TEXT NOT NULL,
   `creation_date` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
   PRIMARY KEY (`id`),
   FOREIGN KEY (`ogsm_id`) REFERENCES ogsm(`id`),
   FOREIGN KEY (`created_by_id`) REFERENCES user(`id`)
 ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

 CREATE TABLE `comment` (
    `id` BIGINT NOT NULL,
    `assertion_id` BIGINT NOT NULL,
    `created_by_id` BIGINT NOT NULL,
    `parent_id` BIGINT,
    `text` TEXT,
    `creation_date` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (`id`),
    FOREIGN KEY (`assertion_id`) REFERENCES assertion(`id`),
    FOREIGN KEY (`parent_id`) REFERENCES comment(`id`),
    FOREIGN KEY (`created_by_id`) REFERENCES user(`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

 CREATE TABLE `assertion_tag` (
     `tag_id` BIGINT NOT NULL,
     `assertion_id` BIGINT NOT NULL
     ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;