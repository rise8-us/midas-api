ALTER TABLE `coverage`
    ADD COLUMN `sonarqube_url` VARCHAR(255),
    ADD COLUMN `ref` VARCHAR(255),
    ADD COLUMN `triggered_by` VARCHAR(255),
    ADD COLUMN `pipeline_url` VARCHAR(255),
    ADD COLUMN `pipeline_status` VARCHAR(255);

