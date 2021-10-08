DROP DATABASE IF EXISTS appDB;
DROP DATABASE IF EXISTS appDB_test;

DROP USER IF EXISTS localDBUser;
DROP USER IF EXISTS local_test;

CREATE DATABASE appDB;
CREATE DATABASE appDB_test;

CREATE USER 'localDBUser'@'%' IDENTIFIED BY 'localDBUser';
CREATE USER 'local_test'@'%' IDENTIFIED BY 'local_test';
CREATE USER IF NOT EXISTS 'root'@'%';

GRANT ALL ON appDB.* TO 'localDBUser'@'%' WITH GRANT OPTION;
GRANT ALL ON appDB_test.* TO 'local_test'@'%' WITH GRANT OPTION;
GRANT ALL ON *.* TO 'root'@'%' WITH GRANT OPTION;

SET GLOBAL time_zone = "+00:00";

FLUSH PRIVILEGES;