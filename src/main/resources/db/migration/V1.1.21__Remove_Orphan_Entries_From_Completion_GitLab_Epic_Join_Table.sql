DELETE FROM `completion_gitlab_epic` WHERE NOT EXISTS(SELECT id FROM `epic` e WHERE e.id = epic_id);