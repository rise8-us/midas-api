DROP DATABASE IF EXISTS midas_db;
DROP DATABASE IF EXISTS midas_db_test;

DROP USER IF EXISTS localDBUser;
DROP USER IF EXISTS local_test;

CREATE DATABASE midas_db;
CREATE DATABASE midas_db_test;

CREATE USER 'localDBUser'@'%' IDENTIFIED BY 'localDBPassword';
CREATE USER 'local_test'@'%' IDENTIFIED BY 'local_test';
CREATE USER IF NOT EXISTS 'root'@'%';

GRANT ALL ON midas_db.* TO 'localDBUser'@'%' WITH GRANT OPTION;
GRANT ALL ON midas_db_test.* TO 'local_test'@'%' WITH GRANT OPTION;
GRANT ALL ON *.* TO 'root'@'%' WITH GRANT OPTION;

SET GLOBAL time_zone = "+00:00";

FLUSH PRIVILEGES;