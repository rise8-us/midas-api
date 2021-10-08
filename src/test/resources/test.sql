SET NAMES utf8;
SET time_zone = '+00:00';
SET foreign_key_checks = 0;
SET sql_mode = 'NO_AUTO_VALUE_ON_ZERO';

SET NAMES utf8mb4;

DROP TABLE IF EXISTS `announcement`;
CREATE TABLE `announcement` (
                                `id` bigint NOT NULL,
                                `message` text NOT NULL,
                                `creation_date` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
                                PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;


DROP TABLE IF EXISTS `assertion`;
CREATE TABLE `assertion` (
                             `id` bigint NOT NULL,
                             `created_by_id` bigint NOT NULL,
                             `type` varchar(70) NOT NULL,
                             `text` text NOT NULL,
                             `creation_date` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
                             `parent_id` bigint DEFAULT NULL,
                             `product_id` bigint NOT NULL,
                             `completed_date` datetime DEFAULT NULL,
                             `status` varchar(70) DEFAULT 'NOT_STARTED',
                             `is_archived` bit(1) DEFAULT b'0',
                             `completion_type` varchar(70) DEFAULT 'STRING',
                             `start_date` datetime DEFAULT NULL,
                             `due_date` datetime DEFAULT NULL,
                             `assigned_person_id` bigint DEFAULT NULL,
                             PRIMARY KEY (`id`),
                             KEY `created_by_id` (`created_by_id`),
                             KEY `parent_id` (`parent_id`),
                             KEY `product_id` (`product_id`),
                             KEY `assigned_person_id` (`assigned_person_id`),
                             CONSTRAINT `assertion_ibfk_2` FOREIGN KEY (`created_by_id`) REFERENCES `user` (`id`),
                             CONSTRAINT `assertion_ibfk_3` FOREIGN KEY (`parent_id`) REFERENCES `assertion` (`id`),
                             CONSTRAINT `assertion_ibfk_4` FOREIGN KEY (`product_id`) REFERENCES `product` (`id`),
                             CONSTRAINT `assertion_ibfk_5` FOREIGN KEY (`assigned_person_id`) REFERENCES `user` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

INSERT INTO `assertion` (`id`, `created_by_id`, `type`, `text`, `creation_date`, `parent_id`, `product_id`, `completed_date`, `status`, `is_archived`, `completion_type`, `start_date`, `due_date`, `assigned_person_id`) VALUES
                                                                                                                                                                                                                              (31,	1,	'OBJECTIVE',	'Deploy to IL4 Prod test something someDeploy to IL4 Prod test something someDeploy to IL4 Prod test something somefdaf sfWWWWWWWWWWWWWWW',	'2021-07-09 14:48:09',	NULL,	23,	NULL,	'ON_TRACK',	CONV('0', 2, 10) + 0,	NULL,	NULL,	NULL,	NULL),
                                                                                                                                                                                                                              (32,	1,	'GOAL',	'Obtain CTF from P1',	'2021-07-09 14:48:09',	31,	23,	NULL,	'COMPLETED',	CONV('0', 2, 10) + 0,	NULL,	NULL,	NULL,	NULL),
                                                                                                                                                                                                                              (33,	1,	'STRATEGY',	'Complete all SD Elements',	'2021-07-09 14:48:09',	32,	23,	NULL,	'NOT_STARTED',	CONV('0', 2, 10) + 0,	'STRING',	NULL,	NULL,	NULL),
                                                                                                                                                                                                                              (34,	1,	'MEASURE',	'Finish all UI tasks',	'2021-07-09 14:48:09',	33,	23,	NULL,	'NOT_STARTED',	CONV('0', 2, 10) + 0,	'STRING',	NULL,	NULL,	NULL),
                                                                                                                                                                                                                              (35,	1,	'MEASURE',	'Finish all API tasks',	'2021-07-09 14:48:09',	33,	23,	NULL,	'NOT_STARTED',	CONV('0', 2, 10) + 0,	'STRING',	NULL,	NULL,	NULL),
                                                                                                                                                                                                                              (36,	1,	'STRATEGY',	'Have a passing gitlab pipeline',	'2021-07-09 14:48:09',	32,	23,	NULL,	'NOT_STARTED',	CONV('0', 2, 10) + 0,	'STRING',	NULL,	NULL,	NULL),
                                                                                                                                                                                                                              (37,	1,	'MEASURE',	'code coverage above 80%',	'2021-07-09 14:48:09',	36,	23,	NULL,	'NOT_STARTED',	CONV('0', 2, 10) + 0,	'STRING',	NULL,	NULL,	NULL),
                                                                                                                                                                                                                              (38,	1,	'MEASURE',	'No security finding in code base',	'2021-07-09 14:48:09',	36,	23,	NULL,	'NOT_STARTED',	CONV('0', 2, 10) + 0,	'STRING',	NULL,	NULL,	NULL),
                                                                                                                                                                                                                              (39,	1,	'MEASURE',	'No vulnerabilities in dependencies  ',	'2021-07-09 14:48:09',	36,	23,	NULL,	'NOT_STARTED',	CONV('0', 2, 10) + 0,	'STRING',	NULL,	NULL,	NULL),
                                                                                                                                                                                                                              (40,	1,	'OBJECTIVE',	'Thing that could be blocked',	'2021-07-09 14:49:04',	NULL,	23,	NULL,	'ON_TRACK',	CONV('0', 2, 10) + 0,	NULL,	NULL,	NULL,	NULL),
                                                                                                                                                                                                                              (41,	1,	'GOAL',	'Goal that is at risk',	'2021-07-09 14:49:04',	40,	23,	NULL,	'ON_TRACK',	CONV('0', 2, 10) + 0,	NULL,	NULL,	NULL,	NULL),
                                                                                                                                                                                                                              (42,	1,	'STRATEGY',	'strat',	'2021-07-09 14:49:04',	41,	23,	NULL,	'NOT_STARTED',	CONV('0', 2, 10) + 0,	'STRING',	NULL,	NULL,	NULL),
                                                                                                                                                                                                                              (43,	1,	'MEASURE',	'measure',	'2021-07-09 14:49:04',	42,	23,	NULL,	'NOT_STARTED',	CONV('0', 2, 10) + 0,	'STRING',	NULL,	NULL,	NULL),
                                                                                                                                                                                                                              (120,	1,	'OBJECTIVE',	'Enter new objective here...',	'2021-09-03 11:58:44',	NULL,	23,	NULL,	'ON_TRACK',	CONV('0', 2, 10) + 0,	NULL,	NULL,	NULL,	NULL),
                                                                                                                                                                                                                              (121,	1,	'GOAL',	'Enter new goal here...',	'2021-09-03 11:58:44',	120,	23,	NULL,	'NOT_STARTED',	CONV('0', 2, 10) + 0,	'STRING',	NULL,	NULL,	NULL),
                                                                                                                                                                                                                              (122,	1,	'STRATEGY',	'Enter new strategy here...',	'2021-09-03 11:58:44',	121,	23,	NULL,	'NOT_STARTED',	CONV('0', 2, 10) + 0,	'STRING',	NULL,	NULL,	NULL),
                                                                                                                                                                                                                              (123,	1,	'MEASURE',	'Enter new measure here...',	'2021-09-03 11:58:44',	122,	23,	NULL,	'NOT_STARTED',	CONV('0', 2, 10) + 0,	'STRING',	NULL,	NULL,	NULL),
                                                                                                                                                                                                                              (125,	1,	'OBJECTIVE',	'Enter new objective here...',	'2021-09-03 18:10:14',	NULL,	23,	NULL,	'COMPLETED',	CONV('0', 2, 10) + 0,	NULL,	NULL,	NULL,	NULL),
                                                                                                                                                                                                                              (126,	1,	'GOAL',	'Enter new goal here...',	'2021-09-03 18:10:14',	125,	23,	NULL,	'COMPLETED',	CONV('0', 2, 10) + 0,	'STRING',	NULL,	NULL,	NULL),
                                                                                                                                                                                                                              (127,	1,	'STRATEGY',	'Enter new strategy here...',	'2021-09-03 18:10:14',	126,	23,	NULL,	'COMPLETED',	CONV('0', 2, 10) + 0,	'STRING',	NULL,	NULL,	NULL),
                                                                                                                                                                                                                              (128,	1,	'MEASURE',	'Enter new measure here...',	'2021-09-03 18:10:14',	127,	23,	NULL,	'COMPLETED',	CONV('0', 2, 10) + 0,	'STRING',	NULL,	NULL,	NULL),
                                                                                                                                                                                                                              (130,	1,	'OBJECTIVE',	'blarg',	'2021-09-03 20:35:24',	NULL,	23,	NULL,	'NOT_STARTED',	CONV('0', 2, 10) + 0,	'STRING',	NULL,	NULL,	NULL),
                                                                                                                                                                                                                              (131,	1,	'GOAL',	'Enter new goal here...',	'2021-09-03 20:35:24',	130,	23,	NULL,	'NOT_STARTED',	CONV('0', 2, 10) + 0,	'STRING',	NULL,	NULL,	NULL),
                                                                                                                                                                                                                              (132,	1,	'STRATEGY',	'Enter new strategy here...',	'2021-09-03 20:35:24',	131,	23,	NULL,	'NOT_STARTED',	CONV('0', 2, 10) + 0,	'STRING',	NULL,	NULL,	NULL),
                                                                                                                                                                                                                              (133,	1,	'MEASURE',	'Enter new measure here...',	'2021-09-03 20:35:24',	132,	23,	NULL,	'ON_TRACK',	CONV('0', 2, 10) + 0,	NULL,	NULL,	NULL,	NULL),
                                                                                                                                                                                                                              (136,	1,	'OBJECTIVE',	'Enter new objective here...',	'2021-09-07 14:47:04',	NULL,	23,	NULL,	'NOT_STARTED',	CONV('0', 2, 10) + 0,	'STRING',	NULL,	NULL,	NULL),
                                                                                                                                                                                                                              (137,	1,	'GOAL',	'Enter new goal here...',	'2021-09-07 14:47:04',	136,	23,	NULL,	'NOT_STARTED',	CONV('0', 2, 10) + 0,	'STRING',	NULL,	NULL,	NULL),
                                                                                                                                                                                                                              (138,	1,	'STRATEGY',	'Enter new strategy here...',	'2021-09-07 14:47:04',	137,	23,	NULL,	'NOT_STARTED',	CONV('0', 2, 10) + 0,	'STRING',	NULL,	NULL,	NULL),
                                                                                                                                                                                                                              (139,	1,	'MEASURE',	'Enter new measure here...',	'2021-09-07 14:47:04',	138,	23,	NULL,	'NOT_STARTED',	CONV('0', 2, 10) + 0,	'STRING',	NULL,	NULL,	NULL);

DROP TABLE IF EXISTS `comment`;
CREATE TABLE `comment` (
                           `id` bigint NOT NULL,
                           `assertion_id` bigint NOT NULL,
                           `created_by_id` bigint NOT NULL,
                           `parent_id` bigint DEFAULT NULL,
                           `text` text,
                           `creation_date` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
                           `last_edit` datetime DEFAULT NULL,
                           `edited_by_id` bigint DEFAULT NULL,
                           PRIMARY KEY (`id`),
                           KEY `assertion_id` (`assertion_id`),
                           KEY `parent_id` (`parent_id`),
                           KEY `created_by_id` (`created_by_id`),
                           CONSTRAINT `comment_ibfk_1` FOREIGN KEY (`assertion_id`) REFERENCES `assertion` (`id`),
                           CONSTRAINT `comment_ibfk_2` FOREIGN KEY (`parent_id`) REFERENCES `comment` (`id`),
                           CONSTRAINT `comment_ibfk_3` FOREIGN KEY (`created_by_id`) REFERENCES `user` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

INSERT INTO `comment` (`id`, `assertion_id`, `created_by_id`, `parent_id`, `text`, `creation_date`, `last_edit`, `edited_by_id`) VALUES
                                                                                                                                     (49,	40,	1,	NULL,	'This is at risk###AT_RISK',	'2021-07-09 14:50:15',	NULL,	NULL),
                                                                                                                                     (50,	41,	1,	NULL,	'this is blocked due to a thing that prevents the completion of it###BLOCKED',	'2021-07-09 14:50:47',	'2021-07-09 14:51:15',	NULL),
                                                                                                                                     (70,	31,	1,	NULL,	'###BLOCKED',	'2021-08-23 21:06:54',	NULL,	NULL),
                                                                                                                                     (71,	31,	1,	NULL,	'test###COMPLETED###BLOCKED',	'2021-08-23 21:08:16',	NULL,	NULL),
                                                                                                                                     (72,	32,	1,	NULL,	'temp###COMPLETED',	'2021-08-24 15:15:20',	NULL,	NULL),
                                                                                                                                     (124,	120,	1,	NULL,	'on track###ON_TRACK',	'2021-09-03 18:10:02',	NULL,	NULL),
                                                                                                                                     (129,	125,	1,	NULL,	'comp###COMPLETED',	'2021-09-03 18:10:28',	NULL,	NULL),
                                                                                                                                     (134,	133,	1,	NULL,	'fdsafa###BLOCKED',	'2021-09-03 21:41:39',	NULL,	NULL),
                                                                                                                                     (141,	31,	1,	NULL,	'###ON_TRACK',	'2021-09-09 16:47:46',	NULL,	NULL),
                                                                                                                                     (142,	40,	1,	NULL,	'###ON_TRACK',	'2021-09-09 16:48:00',	NULL,	NULL),
                                                                                                                                     (143,	41,	1,	NULL,	'###ON_TRACK',	'2021-09-09 16:48:11',	NULL,	NULL),
                                                                                                                                     (144,	133,	1,	NULL,	'###ON_TRACK',	'2021-09-09 16:55:20',	NULL,	NULL);

DROP TABLE IF EXISTS `coverage`;
CREATE TABLE `coverage` (
                            `id` bigint NOT NULL,
                            `job_id` int DEFAULT NULL,
                            `project_id` bigint NOT NULL,
                            `test_coverage` float NOT NULL,
                            `coverage_change` float NOT NULL,
                            `maintainability_rating` varchar(100) NOT NULL,
                            `reliability_rating` varchar(100) NOT NULL,
                            `security_rating` varchar(100) NOT NULL,
                            `creation_date` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
                            `sonarqube_url` varchar(255) DEFAULT NULL,
                            `ref` varchar(255) DEFAULT NULL,
                            `triggered_by` varchar(255) DEFAULT NULL,
                            `pipeline_url` varchar(255) DEFAULT NULL,
                            `pipeline_status` varchar(255) DEFAULT NULL,
                            PRIMARY KEY (`id`),
                            KEY `project_id` (`project_id`),
                            CONSTRAINT `coverage_ibfk_1` FOREIGN KEY (`project_id`) REFERENCES `project` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

INSERT INTO `coverage` (`id`, `job_id`, `project_id`, `test_coverage`, `coverage_change`, `maintainability_rating`, `reliability_rating`, `security_rating`, `creation_date`, `sonarqube_url`, `ref`, `triggered_by`, `pipeline_url`, `pipeline_status`) VALUES
                                                                                                                                                                                                                                                             (1500,	NULL,	14,	95,	0,	'A',	'A',	'A',	'2021-08-27 17:54:03',	NULL,	NULL,	NULL,	NULL,	NULL),
                                                                                                                                                                                                                                                             (1501,	NULL,	15,	93,	0,	'A',	'A',	'A',	'2021-08-27 17:54:35',	NULL,	NULL,	NULL,	NULL,	NULL);

DROP TABLE IF EXISTS `feature`;
CREATE TABLE `feature` (
                           `id` bigint NOT NULL,
                           `title` varchar(70) NOT NULL,
                           `position` int DEFAULT NULL,
                           `creation_date` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
                           `description` text,
                           `product_id` bigint NOT NULL,
                           PRIMARY KEY (`id`),
                           KEY `product_id` (`product_id`),
                           CONSTRAINT `feature_ibfk_1` FOREIGN KEY (`product_id`) REFERENCES `product` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

INSERT INTO `feature` (`id`, `title`, `position`, `creation_date`, `description`, `product_id`) VALUES
                                                                                                    (108,	'feature 4',	1,	'2021-09-01 13:24:54',	'',	23),
                                                                                                    (2000,	'feature 1',	3,	'2021-08-31 15:38:40',	NULL,	23),
                                                                                                    (2001,	'feature 2',	0,	'2021-08-31 15:38:55',	NULL,	23),
                                                                                                    (2002,	'feature 3',	2,	'2021-08-31 15:38:55',	NULL,	23);

DROP TABLE IF EXISTS `flyway_schema_history`;
CREATE TABLE `flyway_schema_history` (
                                         `installed_rank` int NOT NULL,
                                         `version` varchar(50) DEFAULT NULL,
                                         `description` varchar(200) NOT NULL,
                                         `type` varchar(20) NOT NULL,
                                         `script` varchar(1000) NOT NULL,
                                         `checksum` int DEFAULT NULL,
                                         `installed_by` varchar(100) NOT NULL,
                                         `installed_on` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
                                         `execution_time` int NOT NULL,
                                         `success` tinyint(1) NOT NULL,
                                         PRIMARY KEY (`installed_rank`),
                                         KEY `flyway_schema_history_s_idx` (`success`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

INSERT INTO `flyway_schema_history` (`installed_rank`, `version`, `description`, `type`, `script`, `checksum`, `installed_by`, `installed_on`, `execution_time`, `success`) VALUES
                                                                                                                                                                                (1,	'0.0',	'Add User Table',	'SQL',	'V0.0__Add_User_Table.sql',	-1120438702,	'localDBUser',	'2021-07-09 14:13:02',	112,	1),
                                                                                                                                                                                (2,	'0.1',	'Add Products And Teams Tables',	'SQL',	'V0.1__Add_Products_And_Teams_Tables.sql',	95033198,	'localDBUser',	'2021-07-09 14:13:02',	109,	1),
                                                                                                                                                                                (3,	'0.2',	'Add Announcements Tables',	'SQL',	'V0.2__Add_Announcements_Tables.sql',	-554353065,	'localDBUser',	'2021-07-09 14:13:02',	64,	1),
                                                                                                                                                                                (4,	'0.3',	'Add Team Description',	'SQL',	'V0.3__Add_Team_Description.sql',	-188357736,	'localDBUser',	'2021-07-09 14:13:03',	60,	1),
                                                                                                                                                                                (5,	'0.4',	'Add Tags Table',	'SQL',	'V0.4__Add_Tags_Table.sql',	-1857591471,	'localDBUser',	'2021-07-09 14:13:03',	72,	1),
                                                                                                                                                                                (6,	'0.5',	'Add ProductJourneyMap Column',	'SQL',	'V0.5__Add_ProductJourneyMap_Column.sql',	-417221710,	'localDBUser',	'2021-07-09 14:13:03',	39,	1),
                                                                                                                                                                                (7,	'0.6',	'Add Unique Constraint To KeycloakUID Column',	'SQL',	'V0.6__Add_Unique_Constraint_To_KeycloakUID_Column.sql',	-1382028474,	'localDBUser',	'2021-07-09 14:13:03',	39,	1),
                                                                                                                                                                                (8,	'0.7',	'Add Portfolio Table',	'SQL',	'V0.7__Add_Portfolio_Table.sql',	-480850969,	'localDBUser',	'2021-07-09 14:13:03',	125,	1),
                                                                                                                                                                                (9,	'0.8',	'Change Product to Project',	'SQL',	'V0.8__Change_Product_to_Project.sql',	1014482897,	'localDBUser',	'2021-07-09 14:13:03',	93,	1),
                                                                                                                                                                                (10,	'0.9',	'Add Application Table',	'SQL',	'V0.9__Add_Application_Table.sql',	17074475,	'localDBUser',	'2021-07-09 14:13:03',	241,	1),
                                                                                                                                                                                (11,	'0.10',	'Remove Not Null GitlabProjectId Project Table',	'SQL',	'V0.10__Remove_Not_Null_GitlabProjectId_Project_Table.sql',	927222385,	'localDBUser',	'2021-07-09 14:13:04',	63,	1),
                                                                                                                                                                                (12,	'0.11',	'Add CreatedBy Column to Tags Table',	'SQL',	'V0.11__Add_CreatedBy_Column_to_Tags_Table.sql',	721605243,	'localDBUser',	'2021-07-09 14:13:04',	88,	1),
                                                                                                                                                                                (13,	'0.12',	'Change Application to Product',	'SQL',	'V0.12__Change_Application_to_Product.sql',	380255933,	'localDBUser',	'2021-07-09 14:13:04',	163,	1),
                                                                                                                                                                                (14,	'0.13',	'Remove Null Constraint From Previous Migration',	'SQL',	'V0.13__Remove_Null_Constraint_From_Previous_Migration.sql',	-602580503,	'localDBUser',	'2021-07-09 14:13:04',	57,	1),
                                                                                                                                                                                (15,	'0.14',	'Add Vision Statement Column to Products',	'SQL',	'V0.14__Add_Vision_Statement_Column_to_Products.sql',	1378142270,	'localDBUser',	'2021-07-09 14:13:04',	43,	1),
                                                                                                                                                                                (16,	'0.15',	'Add Problem Statement Column to Products',	'SQL',	'V0.15__Add_Problem_Statement_Column_to_Products.sql',	-73659216,	'localDBUser',	'2021-07-09 14:13:04',	37,	1),
                                                                                                                                                                                (17,	'0.16',	'Remove Problem Statement Column from Products',	'SQL',	'V0.16__Remove_Problem_Statement_Column_from_Products.sql',	1483834128,	'localDBUser',	'2021-07-09 14:13:05',	471,	1),
                                                                                                                                                                                (18,	'0.17',	'Add Assertion Table',	'SQL',	'V0.17__Add_Assertion_Table.sql',	-762828952,	'localDBUser',	'2021-07-09 14:13:05',	332,	1),
                                                                                                                                                                                (19,	'0.18',	'Change Problem Column to Text',	'SQL',	'V0.18__Change_Problem_Column_to_Text.sql',	1001755119,	'localDBUser',	'2021-07-09 14:13:05',	25,	1),
                                                                                                                                                                                (20,	'0.19',	'Add Parent Child To Assertion',	'SQL',	'V0.19__Add_Parent_Child_To_Assertion.sql',	-1673035177,	'localDBUser',	'2021-07-09 14:13:05',	83,	1),
                                                                                                                                                                                (21,	'0.20',	'Add Text To Ogsm',	'SQL',	'V0.20__Add_Text_To_Ogsm.sql',	-210599072,	'localDBUser',	'2021-07-09 14:13:05',	91,	1),
                                                                                                                                                                                (22,	'0.21',	'Change Ogsm To Objective',	'SQL',	'V0.21__Change_Ogsm_To_Objective.sql',	-1889733555,	'localDBUser',	'2021-07-09 14:13:06',	65,	1),
                                                                                                                                                                                (23,	'0.22',	'Add Product to Assertion',	'SQL',	'V0.22__Add_Product_to_Assertion.sql',	-556920221,	'localDBUser',	'2021-07-09 14:13:06',	400,	1),
                                                                                                                                                                                (24,	'0.23',	'Add Comment LastEdit',	'SQL',	'V0.23__Add_Comment_LastEdit.sql',	1945505840,	'localDBUser',	'2021-07-09 14:13:06',	39,	1),
                                                                                                                                                                                (25,	'0.24',	'Add Coverage',	'SQL',	'V0.24__Add_Coverage.sql',	807094883,	'localDBUser',	'2021-07-09 14:13:06',	39,	1),
                                                                                                                                                                                (26,	'0.25',	'Add TagType',	'SQL',	'V0.25__Add_TagType.sql',	-914216397,	'localDBUser',	'2021-07-09 14:13:06',	36,	1),
                                                                                                                                                                                (27,	'0.26',	'Add Gitlab Conifig',	'SQL',	'V0.26__Add_Gitlab_Conifig.sql',	-2125135516,	'localDBUser',	'2021-07-09 14:13:06',	38,	1),
                                                                                                                                                                                (28,	'0.27',	'Add Gitlab Config To Project',	'SQL',	'V0.27__Add_Gitlab_Config_To_Project.sql',	-1795180952,	'localDBUser',	'2021-07-09 14:13:07',	86,	1),
                                                                                                                                                                                (29,	'0.28',	'Update Product Type',	'SQL',	'V0.28__Update_Product_Type.sql',	1353369593,	'localDBUser',	'2021-07-09 14:13:07',	13,	1),
                                                                                                                                                                                (30,	'0.29',	'Add Gitlab Coverage Details',	'SQL',	'V0.29__Add_Gitlab_Coverage_Details.sql',	1732413629,	'localDBUser',	'2021-07-09 14:13:07',	38,	1),
                                                                                                                                                                                (31,	'0.30',	'AssertionStatus Enum Update',	'SQL',	'V0.30__AssertionStatus_Enum_Update.sql',	142777820,	'localDBUser',	'2021-07-09 14:13:07',	166,	1),
                                                                                                                                                                                (32,	'0.31',	'Drop Problem Table',	'SQL',	'V0.31__Drop_Problem_Table.sql',	-1581815128,	'localDBUser',	'2021-07-09 14:53:13',	57,	1),
                                                                                                                                                                                (33,	'0.32',	'Add Gitlab Group Id To Product',	'SQL',	'V0.32__Add_Gitlab_Group_Id_To_Product.sql',	1409020099,	'localDBUser',	'2021-07-26 18:15:42',	131,	1),
                                                                                                                                                                                (34,	'0.33',	'Add Gitlab Config Id To Product',	'SQL',	'V0.33__Add_Gitlab_Config_Id_To_Product.sql',	-1666327159,	'localDBUser',	'2021-07-26 18:15:42',	80,	1),
                                                                                                                                                                                (35,	'0.34',	'Change Enums From Ordinal To String',	'SQL',	'V0.34__Change_Enums_From_Ordinal_To_String.sql',	-2037814569,	'localDBUser',	'2021-07-26 18:15:42',	29,	1),
                                                                                                                                                                                (36,	'0.35',	'Rename Gitlab config',	'SQL',	'V0.35__Rename_Gitlab_config.sql',	839862735,	'localDBUser',	'2021-08-18 20:46:52',	76,	1),
                                                                                                                                                                                (37,	'0.36',	'Add One Pager Support',	'SQL',	'V0.36__Add_One_Pager_Support.sql',	1404413901,	'localDBUser',	'2021-08-18 20:46:53',	337,	1),
                                                                                                                                                                                (38,	'0.37',	'Update RoadmapStatus Default',	'SQL',	'V0.37__Update_RoadmapStatus_Default.sql',	1033708091,	'localDBUser',	'2021-08-19 18:46:19',	129,	1),
                                                                                                                                                                                (39,	'0.38',	'Allow Null TargetDate',	'SQL',	'V0.38__Allow_Null_TargetDate.sql',	1491752385,	'localDBUser',	'2021-08-19 20:00:30',	100,	1),
                                                                                                                                                                                (40,	'0.39',	'Add Email And Company To User',	'SQL',	'V0.39__Add_Email_And_Company_To_User.sql',	567155703,	'localDBUser',	'2021-08-30 21:12:03',	95,	1),
                                                                                                                                                                                (41,	'1.1.0',	'Add Release Table',	'SQL',	'V1.1.0__Add_Release_Table.sql',	-596286151,	'localDBUser',	'2021-09-15 14:41:54',	243,	1);

DROP TABLE IF EXISTS `hibernate_sequence`;
CREATE TABLE `hibernate_sequence` (
    `next_val` bigint DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

INSERT INTO `hibernate_sequence` (`next_val`) VALUES
                                                  (145),
                                                  (145);

DROP TABLE IF EXISTS `persona`;
CREATE TABLE `persona` (
                           `id` bigint NOT NULL,
                           `title` varchar(70) NOT NULL,
                           `position` int DEFAULT NULL,
                           `creation_date` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
                           `description` text,
                           `is_supported` bit(1) NOT NULL DEFAULT b'0',
                           `product_id` bigint NOT NULL,
                           PRIMARY KEY (`id`),
                           KEY `product_id` (`product_id`),
                           CONSTRAINT `persona_ibfk_1` FOREIGN KEY (`product_id`) REFERENCES `product` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

INSERT INTO `persona` (`id`, `title`, `position`, `creation_date`, `description`, `is_supported`, `product_id`) VALUES
                                                                                                                    (51,	'Product Managers',	0,	'2021-08-18 21:27:39',	'',	CONV('1', 2, 10) + 0,	23),
                                                                                                                    (61,	'Portfolio Managers',	1,	'2021-08-20 13:13:26',	'',	CONV('1', 2, 10) + 0,	23),
                                                                                                                    (62,	'ABMS Apps Stakeholders',	2,	'2021-08-20 13:13:58',	'',	CONV('0', 2, 10) + 0,	23),
                                                                                                                    (63,	'App Developers',	3,	'2021-08-20 13:14:12',	'',	CONV('0', 2, 10) + 0,	23),
                                                                                                                    (64,	'Warfighters',	4,	'2021-08-20 13:14:29',	'',	CONV('0', 2, 10) + 0,	23),
                                                                                                                    (135,	'new',	5,	'2021-09-07 14:46:45',	'',	CONV('0', 2, 10) + 0,	23);

DROP TABLE IF EXISTS `product`;
CREATE TABLE `product` (
                           `id` bigint NOT NULL,
                           `name` varchar(70) NOT NULL,
                           `creation_date` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
                           `description` text,
                           `is_archived` bit(1) NOT NULL DEFAULT b'0',
                           `product_manager_id` bigint DEFAULT NULL,
                           `type` varchar(70) DEFAULT NULL,
                           `parent_id` bigint DEFAULT NULL,
                           `gitlab_group_id` int DEFAULT NULL,
                           `source_control_id` bigint DEFAULT NULL,
                           `vision` text,
                           `mission` text,
                           `problem_statement` text,
                           PRIMARY KEY (`id`),
                           KEY `product_manager_id` (`product_manager_id`),
                           KEY `parent_id` (`parent_id`),
                           KEY `gitlab_config_id` (`source_control_id`),
                           CONSTRAINT `product_ibfk_1` FOREIGN KEY (`product_manager_id`) REFERENCES `user` (`id`),
                           CONSTRAINT `product_ibfk_3` FOREIGN KEY (`parent_id`) REFERENCES `product` (`id`),
                           CONSTRAINT `product_ibfk_4` FOREIGN KEY (`source_control_id`) REFERENCES `source_control` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

INSERT INTO `product` (`id`, `name`, `creation_date`, `description`, `is_archived`, `product_manager_id`, `type`, `parent_id`, `gitlab_group_id`, `source_control_id`, `vision`, `mission`, `problem_statement`) VALUES
                                                                                                                                                                                                                     (23,	'Midas',	'2021-07-09 14:34:29',	'Measures, Integration, Deployment Analytics System',	CONV('0', 2, 10) + 0,	1000,	'PRODUCT',	30,	NULL,	NULL,	'Provide a modern toolbox for measuring technical activities against warfighter outcomes',	'Automate manual project tracking to increase informed decision-making.',	'ABMS Apps leadership does not have an effective framework to align product team development to strategic goals in a way that is agile and meets the demands of continuous delivery. Additionally, leadership needs to provide transparency to its stakeholders and maintain accountability across a complex and distributed set of micro-service providers.'),
                                                                                                                                                                                                                     (24,	'Spyro',	'2021-07-09 14:38:49',	'Spyro is getting the Consensus Fusion Engine onto Platform One with BAE Systems and Rise8. The Fusion Engine is a necessary component on P1 to fulfill the PMO CRC organic software replacement suite and for ABMS.',	CONV('0', 2, 10) + 0,	1,	'PRODUCT',	30,	NULL,	NULL,	NULL,	NULL,	NULL),
                                                                                                                                                                                                                     (25,	'Red Dragon',	'2021-07-09 14:39:50',	'Airspace Deconfliction Tool',	CONV('0', 2, 10) + 0,	1,	'PRODUCT',	30,	NULL,	NULL,	NULL,	NULL,	NULL),
                                                                                                                                                                                                                     (26,	'Hyve',	'2021-07-09 14:40:20',	'A tool focused on cross-C2 collaboration, that enables effective monitoring and dissemination of ACCURATE mission data. We think a deliberate and integrated communications capability could reduce the pressure on system-to-system capabilities, and introduce increased accuracy and speed for Command and Control today.',	CONV('0', 2, 10) + 0,	1,	'PRODUCT',	30,	NULL,	NULL,	NULL,	NULL,	NULL),
                                                                                                                                                                                                                     (27,	'ARAKNID',	'2021-07-09 14:40:58',	'Automatically compute kill-webs consisting of multiple Courses of Action (COAs) sourced from real time awareness in every domain of what assets are available to achieve a desired effect. Evaluate feasibility of each COA, score options with multiple metrics, and present a ranked set of choices to the operator.',	CONV('0', 2, 10) + 0,	1,	'PRODUCT',	30,	NULL,	NULL,	NULL,	NULL,	NULL),
                                                                                                                                                                                                                     (28,	'Bloodhound',	'2021-07-09 14:41:31',	'ISR Execution service that serves Imagery Analysts by using machine-to-machine communication to automate the flow of info from aircraft sensors so that they can worry less about hand-jamming data into Excel sheets and sifting through mediums like chat to forward them to other members of their kill chain.',	CONV('0', 2, 10) + 0,	1,	'PRODUCT',	30,	NULL,	NULL,	NULL,	NULL,	NULL),
                                                                                                                                                                                                                     (29,	'OmniONE',	'2021-07-09 14:41:46',	'',	CONV('0', 2, 10) + 0,	1,	'PRODUCT',	30,	NULL,	NULL,	NULL,	NULL,	NULL),
                                                                                                                                                                                                                     (30,	'TAC C2',	'2021-07-09 14:42:51',	'Doing things and accomplishing stuff',	CONV('0', 2, 10) + 0,	1000,	'PORTFOLIO',	NULL,	NULL,	NULL,	NULL,	NULL,	NULL),
                                                                                                                                                                                                                     (76,	'test',	'2021-08-25 15:23:23',	'',	CONV('0', 2, 10) + 0,	1,	'PRODUCT',	NULL,	NULL,	NULL,	NULL,	NULL,	NULL),
                                                                                                                                                                                                                     (140,	'test435',	'2021-09-08 21:39:06',	'',	CONV('0', 2, 10) + 0,	1,	'PORTFOLIO',	NULL,	NULL,	NULL,	NULL,	NULL,	NULL);

DROP TABLE IF EXISTS `product_tag`;
CREATE TABLE `product_tag` (
                               `tag_id` bigint NOT NULL,
                               `product_id` bigint DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

INSERT INTO `product_tag` (`tag_id`, `product_id`) VALUES
                                                       (12,	24),
                                                       (3,	24),
                                                       (11,	27),
                                                       (4,	27),
                                                       (12,	28),
                                                       (11,	25),
                                                       (3,	25),
                                                       (4,	26),
                                                       (13,	26),
                                                       (11,	23),
                                                       (4,	23),
                                                       (12,	24),
                                                       (3,	24),
                                                       (11,	27),
                                                       (4,	27),
                                                       (12,	28),
                                                       (11,	25),
                                                       (3,	25),
                                                       (4,	26),
                                                       (13,	26),
                                                       (11,	23),
                                                       (4,	23);

DROP TABLE IF EXISTS `product_team`;
CREATE TABLE `product_team` (
                                `product_id` bigint NOT NULL,
                                `team_id` bigint NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

INSERT INTO `product_team` (`product_id`, `team_id`) VALUES
                                                         (24,	78),
                                                         (23,	75),
                                                         (24,	78),
                                                         (23,	75);

DROP TABLE IF EXISTS `project`;
CREATE TABLE `project` (
                           `id` bigint NOT NULL,
                           `name` varchar(70) NOT NULL,
                           `team_id` bigint DEFAULT NULL,
                           `description` text,
                           `creation_date` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
                           `is_archived` bit(1) NOT NULL DEFAULT b'0',
                           `gitlab_project_id` int DEFAULT NULL,
                           `project_journey_map` bigint NOT NULL DEFAULT '0',
                           `product_id` bigint DEFAULT NULL,
                           `source_control_id` bigint DEFAULT NULL,
                           PRIMARY KEY (`id`),
                           KEY `team_id` (`team_id`),
                           KEY `application_id` (`product_id`),
                           KEY `gitlab_config_id` (`source_control_id`),
                           CONSTRAINT `project_ibfk_1` FOREIGN KEY (`team_id`) REFERENCES `team` (`id`),
                           CONSTRAINT `project_ibfk_3` FOREIGN KEY (`product_id`) REFERENCES `product` (`id`),
                           CONSTRAINT `project_ibfk_4` FOREIGN KEY (`source_control_id`) REFERENCES `source_control` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

INSERT INTO `project` (`id`, `name`, `team_id`, `description`, `creation_date`, `is_archived`, `gitlab_project_id`, `project_journey_map`, `product_id`, `source_control_id`) VALUES
                                                                                                                                                                                  (14,	'Midas UI',	NULL,	'',	'2021-07-09 14:30:07',	CONV('0', 2, 10) + 0,	NULL,	7,	23,	NULL),
                                                                                                                                                                                  (15,	'Midas API',	NULL,	'',	'2021-07-09 14:30:35',	CONV('0', 2, 10) + 0,	NULL,	3,	23,	NULL),
                                                                                                                                                                                  (16,	'Hyve UI',	NULL,	'',	'2021-07-09 14:31:09',	CONV('0', 2, 10) + 0,	NULL,	3,	26,	NULL),
                                                                                                                                                                                  (17,	'Hyve API',	NULL,	'',	'2021-07-09 14:31:29',	CONV('0', 2, 10) + 0,	NULL,	3,	26,	NULL),
                                                                                                                                                                                  (18,	'OmniaONE',	NULL,	'',	'2021-07-09 14:32:01',	CONV('0', 2, 10) + 0,	NULL,	3,	29,	NULL),
                                                                                                                                                                                  (20,	'Bloodhound',	NULL,	'',	'2021-07-09 14:32:49',	CONV('0', 2, 10) + 0,	NULL,	3,	28,	NULL),
                                                                                                                                                                                  (21,	'Spyro',	NULL,	'',	'2021-07-09 14:33:07',	CONV('0', 2, 10) + 0,	NULL,	3,	24,	NULL),
                                                                                                                                                                                  (22,	'ARAKNID',	NULL,	'',	'2021-07-09 14:33:24',	CONV('0', 2, 10) + 0,	NULL,	7,	27,	NULL);

DROP TABLE IF EXISTS `project_tag`;
CREATE TABLE `project_tag` (
                               `tag_id` bigint NOT NULL,
                               `project_id` bigint NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

INSERT INTO `project_tag` (`tag_id`, `project_id`) VALUES
                                                       (10,	14),
                                                       (7,	14),
                                                       (9,	15),
                                                       (6,	15),
                                                       (8,	16),
                                                       (10,	16),
                                                       (5,	17),
                                                       (8,	18),
                                                       (10,	18),
                                                       (19,	20),
                                                       (10,	14),
                                                       (7,	14),
                                                       (9,	15),
                                                       (6,	15),
                                                       (8,	16),
                                                       (10,	16),
                                                       (5,	17),
                                                       (8,	18),
                                                       (10,	18),
                                                       (19,	20);

DROP TABLE IF EXISTS `releases`;
CREATE TABLE `releases` (
                            `id` bigint NOT NULL,
                            `title` text NOT NULL,
                            `creation_date` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
                            `target_date` datetime DEFAULT NULL,
                            `status` varchar(70) DEFAULT 'NOT_STARTED',
                            `is_complete` bit(1) NOT NULL DEFAULT b'0'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;


DROP TABLE IF EXISTS `roadmap`;
CREATE TABLE `roadmap` (
                           `id` bigint NOT NULL,
                           `title` varchar(70) NOT NULL,
                           `position` int DEFAULT NULL,
                           `creation_date` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
                           `target_date` datetime DEFAULT NULL,
                           `description` text,
                           `status` varchar(70) NOT NULL DEFAULT 'FUTURE',
                           `product_id` bigint NOT NULL,
                           PRIMARY KEY (`id`),
                           KEY `product_id` (`product_id`),
                           CONSTRAINT `roadmap_ibfk_1` FOREIGN KEY (`product_id`) REFERENCES `product` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

INSERT INTO `roadmap` (`id`, `title`, `position`, `creation_date`, `target_date`, `description`, `status`, `product_id`) VALUES
                                                                                                                             (52,	'Kickoff!',	0,	'2021-08-19 17:44:20',	'2021-02-01 00:00:00',	'Team consisting of 2 engineers, 1 designer, and 1 PM onboarded on P1',	'COMPLETE',	23),
                                                                                                                             (53,	'1st MVP Feedback Session',	1,	'2021-08-19 18:40:13',	'2021-05-01 00:00:00',	'MVP of the product consisted of an Objective, Goals, Strategy, and Measures (OGSM) Tracker and the product Certificate to Field Tracker.',	'COMPLETE',	23),
                                                                                                                             (54,	'MVP Release deployed to Production (IL4)',	2,	'2021-08-19 18:40:37',	'2021-08-01 00:00:00',	'MIDAS gained a CtF from P1 in Aug 2021 and released its MVP built from feedback of 6 beta testers and 10 user feedback sessions.',	'IN_PROGRESS',	23),
                                                                                                                             (55,	'Portfolio Dashboard',	3,	'2021-08-19 19:44:39',	'2021-09-01 00:00:00',	'A dashboard that describes the ABMS Apps Portfolio, high-level strategy, and current status of applications.',	'IN_PROGRESS',	23),
                                                                                                                             (56,	'Portfolio -> Product OGSM Cascade',	4,	'2021-08-19 19:53:44',	'2021-09-01 00:00:00',	'Visualize Portfolio-to-Product connected goals as strategies',	'IN_PROGRESS',	23),
                                                                                                                             (57,	'Product One-Pagers',	5,	'2021-08-19 20:13:43',	'2021-09-01 00:00:00',	'A high level view of the Product\'s vision, mission, & problem statements.',	'IN_PROGRESS',	23),
                                                                                                                             (58,	'DevSecOps Value Stream Metrics',	6,	'2021-08-19 20:15:42',	'2022-09-01 00:00:00',	'',	'FUTURE',	23),
                                                                                                                             (59,	'Capability Needs Statement Performance Measures',	7,	'2021-08-19 20:52:51',	'2022-05-01 00:00:00',	'',	'FUTURE',	23),
                                                                                                                             (60,	'Add a muffin button',	8,	'2021-08-19 20:53:01',	'2021-06-01 00:00:00',	'I want a button that I can click and a warm, moist, muffin poofs into existence.',	'FUTURE',	23);

DROP TABLE IF EXISTS `source_control`;
CREATE TABLE `source_control` (
                                  `id` bigint NOT NULL,
                                  `name` varchar(255) NOT NULL,
                                  `description` text,
                                  `base_url` varchar(255) DEFAULT NULL,
                                  `token` varchar(255) DEFAULT NULL,
                                  `creation_date` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
                                  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;


DROP TABLE IF EXISTS `tag`;
CREATE TABLE `tag` (
                       `id` bigint NOT NULL,
                       `label` tinytext NOT NULL,
                       `description` text,
                       `color` tinytext DEFAULT (_utf8mb4'#969696'),
                       `creation_date` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
                       `created_by_id` bigint DEFAULT NULL,
                       `tag_type` varchar(70) NOT NULL DEFAULT 'ALL',
                       PRIMARY KEY (`id`),
                       KEY `created_by_id` (`created_by_id`),
                       CONSTRAINT `tag_ibfk_1` FOREIGN KEY (`created_by_id`) REFERENCES `user` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

INSERT INTO `tag` (`id`, `label`, `description`, `color`, `creation_date`, `created_by_id`, `tag_type`) VALUES
                                                                                                            (2,	'Ownership::N2X',	'Funded by N2X',	'#ffc107',	'2021-07-09 14:21:50',	1,	'PRODUCT'),
                                                                                                            (3,	'Ownership::CRC',	'Funded by CRC',	'#5b9f0f',	'2021-07-09 14:22:22',	1,	'PRODUCT'),
                                                                                                            (4,	'Ownership::ABMS',	'ABMS owned and managed',	'#03a9f4',	'2021-07-09 14:23:07',	1,	'PRODUCT'),
                                                                                                            (5,	'Language::Go',	'',	'#00bcd4',	'2021-07-09 14:23:38',	1,	'PROJECT'),
                                                                                                            (6,	'Language::Java',	'',	'#f44336',	'2021-07-09 14:24:31',	1,	'PROJECT'),
                                                                                                            (7,	'Language::Javascript',	'',	'#ffeb3b',	'2021-07-09 14:24:57',	1,	'PROJECT'),
                                                                                                            (8,	'Language::Typescript',	'',	'#0789f2',	'2021-07-09 14:25:49',	1,	'PROJECT'),
                                                                                                            (9,	'Framework::Spring Boot',	'',	'#4caf50',	'2021-07-09 14:26:12',	1,	'PROJECT'),
                                                                                                            (10,	'Framework::React',	'',	'#2196f3',	'2021-07-09 14:27:01',	1,	'PROJECT'),
                                                                                                            (11,	'Horizon::1',	'To be completed in one to three months',	'#adef62',	'2021-07-09 14:28:00',	1,	'PRODUCT'),
                                                                                                            (12,	'Horizon::2',	'To be completed in four to six months',	'#3dc2ff',	'2021-07-09 14:28:27',	1,	'PRODUCT'),
                                                                                                            (13,	'Horizon::3',	'To be completed in seven to twelve months',	'#b409d3',	'2021-07-09 14:28:56',	1,	'PRODUCT'),
                                                                                                            (19,	'Language::C++',	'',	'#c2c2c2',	'2021-07-09 14:32:38',	1,	'PROJECT'),
                                                                                                            (69,	'test',	'',	'#009688',	'2021-08-20 20:43:05',	1,	'ALL'),
                                                                                                            (73,	'new tag to take up space',	'',	'#c2c2c2',	'2021-08-24 19:14:42',	1,	'PRODUCT'),
                                                                                                            (74,	'in development',	'',	'#c2c2c2',	'2021-08-24 19:16:31',	1,	'PRODUCT');

DROP TABLE IF EXISTS `team`;
CREATE TABLE `team` (
                        `id` bigint NOT NULL,
                        `name` varchar(70) NOT NULL,
                        `creation_date` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
                        `is_archived` bit(1) NOT NULL DEFAULT b'0',
                        `gitlab_group_id` bigint DEFAULT NULL,
                        `description` text,
                        `product_manager_id` bigint DEFAULT NULL,
                        `designer_id` bigint DEFAULT NULL,
                        `tech_lead_id` bigint DEFAULT NULL,
                        PRIMARY KEY (`id`),
                        KEY `product_manager_id` (`product_manager_id`),
                        KEY `designer_id` (`designer_id`),
                        KEY `tech_lead_id` (`tech_lead_id`),
                        CONSTRAINT `team_ibfk_1` FOREIGN KEY (`product_manager_id`) REFERENCES `user` (`id`),
                        CONSTRAINT `team_ibfk_2` FOREIGN KEY (`designer_id`) REFERENCES `user` (`id`),
                        CONSTRAINT `team_ibfk_3` FOREIGN KEY (`tech_lead_id`) REFERENCES `user` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

INSERT INTO `team` (`id`, `name`, `creation_date`, `is_archived`, `gitlab_group_id`, `description`, `product_manager_id`, `designer_id`, `tech_lead_id`) VALUES
                                                                                                                                                             (75,	'midas',	'2021-08-25 15:11:52',	CONV('0', 2, 10) + 0,	NULL,	'',	1000,	1,	1002),
                                                                                                                                                             (77,	'Spyro',	'2021-08-25 20:45:02',	CONV('0', 2, 10) + 0,	NULL,	'',	1000,	1001,	1002),
                                                                                                                                                             (78,	'test',	'2021-08-25 20:48:21',	CONV('0', 2, 10) + 0,	NULL,	'',	1001,	NULL,	NULL),
                                                                                                                                                             (79,	't3',	'2021-08-25 20:48:40',	CONV('0', 2, 10) + 0,	NULL,	'',	NULL,	NULL,	NULL);

DROP TABLE IF EXISTS `user`;
CREATE TABLE `user` (
                        `id` bigint NOT NULL,
                        `dod_id` bigint DEFAULT NULL,
                        `keycloak_uid` varchar(100) NOT NULL,
                        `username` varchar(100) NOT NULL,
                        `email` varchar(100) DEFAULT NULL,
                        `display_name` varchar(100) DEFAULT NULL,
                        `roles` bigint NOT NULL DEFAULT '0',
                        `creation_date` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
                        `is_disabled` bit(1) NOT NULL DEFAULT b'0',
                        `last_login` datetime DEFAULT NULL,
                        `phone` varchar(100) DEFAULT NULL,
                        `company` varchar(100) DEFAULT NULL,
                        PRIMARY KEY (`id`),
                        UNIQUE KEY `keycloak_uid` (`keycloak_uid`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

INSERT INTO `user` (`id`, `dod_id`, `keycloak_uid`, `username`, `email`, `display_name`, `roles`, `creation_date`, `is_disabled`, `last_login`, `phone`, `company`) VALUES
                                                                                                                                                                        (1,	9999999999,	'keycloak-sub-123',	'localuser',	'email',	'display',	0,	'2021-07-09 14:13:02',	CONV('0', 2, 10) + 0,	'2021-09-13 14:11:43',	'phone',	'company'),
                                                                                                                                                                        (1000,	9999999998,	'keycloak-sub-1234',	'Matt Nelson',	NULL,	'Matt Nelson',	1,	'2021-07-09 14:13:02',	CONV('0', 2, 10) + 0,	'2021-08-25 12:39:50',	NULL,	NULL),
                                                                                                                                                                        (1001,	9999999997,	'keycloak-sub-12345',	'jsmith1',	NULL,	'Jose Ricaurte',	1,	'2021-07-09 14:13:02',	CONV('0', 2, 10) + 0,	'2021-08-25 12:39:50',	'(555) 867-5309',	'Rise8'),
                                                                                                                                                                        (1002,	9999999996,	'keycloak-sub-123456',	'jsmith2',	NULL,	'Jeff Wills',	1,	'2021-07-09 14:13:02',	CONV('0', 2, 10) + 0,	'2021-08-25 12:39:50',	NULL,	NULL),
                                                                                                                                                                        (1003,	9999999995,	'keycloak-sub-1234567',	'jsmith3',	NULL,	'David Lamberson',	1,	'2021-07-09 14:13:02',	CONV('0', 2, 10) + 0,	'2021-08-25 12:39:50',	NULL,	NULL);

DROP TABLE IF EXISTS `user_team`;
CREATE TABLE `user_team` (
                             `user_id` bigint NOT NULL,
                             `team_id` bigint NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

INSERT INTO `user_team` (`user_id`, `team_id`) VALUES
                                                   (1003,	77),
                                                   (1000,	75),
                                                   (1,	75),
                                                   (1002,	75),
                                                   (1003,	75),
                                                   (1003,	77),
                                                   (1000,	75),
                                                   (1,	75),
                                                   (1002,	75),
                                                   (1003,	75);

-- 2021-09-23 20:02:36