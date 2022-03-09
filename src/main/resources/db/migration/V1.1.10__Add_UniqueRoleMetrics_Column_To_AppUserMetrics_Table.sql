DROP PROCEDURE IF EXISTS COMPLETION_CONVERSION;

ALTER TABLE `app_user_metrics`
    ADD COLUMN `unique_role_metrics` JSON;

UPDATE `app_user_metrics`
    SET `unique_role_metrics` = JSON_OBJECT();

ALTER TABLE `app_user_metrics`
    RENAME TO `metrics_app_user`;