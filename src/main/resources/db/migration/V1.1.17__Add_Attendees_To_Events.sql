CREATE TABLE `gantt_event_user_attendee` (
    `event_id` BIGINT NOT NULL,
    `user_id` BIGINT NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

ALTER TABLE `gantt_event_user` RENAME TO `gantt_event_user_organizer`;