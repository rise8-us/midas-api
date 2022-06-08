UPDATE `portfolio`
    SET sprint_start_date = NOW()
    WHERE sprint_start_date IS NULL;

UPDATE `portfolio`
    SET sprint_duration_in_days = 7
    WHERE sprint_duration_in_days IS NULL;
