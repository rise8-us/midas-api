ALTER TABLE `measure`
    ADD COLUMN `status` VARCHAR(70) NOT NULL DEFAULT 'NOT_STARTED';

UPDATE `measure`
    SET status='COMPLETED' WHERE completed_at IS NOT NULL;

UPDATE `measure`
    SET value=target WHERE completed_at IS NOT NULL;

UPDATE `measure`
    SET status='ON_TRACK' WHERE start_date IS NOT NULL AND completed_at IS NULL;
