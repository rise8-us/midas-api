ALTER TABLE `portfolio`
    ADD `gantt_note` TEXT,
    ADD `gantt_note_modified_by` BIGINT,
    ADD `gantt_note_modified_at` DATETIME,
    ADD `sprint_start_date` DATE,
    ADD `sprint_duration_in_days` INT;
