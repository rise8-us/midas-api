ALTER TABLE `issue`
    ADD `labels` VARCHAR(255);

ALTER TABLE `gantt_target` CHANGE `title` `title` VARCHAR(255) NOT NULL;
ALTER TABLE `gantt_milestone` CHANGE `title` `title` VARCHAR(255) NOT NULL;
ALTER TABLE `gantt_event` CHANGE `title` `title` VARCHAR(255) NOT NULL;