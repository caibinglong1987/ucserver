DROP database IF EXISTS ucdb;
CREATE DATABASE IF NOT EXISTS ucdb DEFAULT CHARACTER SET utf8 COLLATE utf8_general_ci;
GRANT ALL PRIVILEGES ON *.* TO 'ucdba'@'%' IDENTIFIED BY 'ucdba123';