ALTER TABLE `product`
    ADD `core_domain` VARCHAR(100),
    RENAME COLUMN `description` to `acronym`;
