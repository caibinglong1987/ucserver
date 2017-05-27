DROP TABLE IF EXISTS `user`;
CREATE TABLE `user` (
  `userid` bigint(20) auto_increment NOT NULL,
  `createtime` bigint(20) DEFAULT NULL,
  `username` varchar(32) DEFAULT NULL,
  `password` varchar(32) DEFAULT NULL,
  `email` varchar(128) DEFAULT NULL,
  `phone` varchar(32) DEFAULT NULL,
  `status` int DEFAULT 0,
  `nickname` varchar(32) DEFAULT NULL,
  `name` varchar(32) DEFAULT NULL,
  `type` int DEFAULT 0,
  `tenantid` int DEFAULT 1,
  PRIMARY KEY (`userid`),
  UNIQUE KEY `U_USER_USERNAME` (`username`,`tenantid`),
  UNIQUE KEY `U_USER_PHONE` (`phone`,`tenantid`),
  UNIQUE KEY `U_USER_EMAIL` (`email`,`tenantid`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

DROP TABLE IF EXISTS `phonelist`;
CREATE TABLE `phonelist` (
  `phone` varchar(32) NOT NULL,
  `userid` bigint(20) NOT NULL,
  `verified` bit(1) DEFAULT 0,
  PRIMARY KEY (`phone`),
  KEY `I_PHONES_USER_ID` (`userid`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

DROP TABLE IF EXISTS `touchdev`;
CREATE TABLE `touchdev` (
  `id` bigint(20) auto_increment NOT NULL,
  `devid` varchar(32) NOT NULL,
  `userid` bigint(20) NOT NULL,
  `phone` varchar(32) DEFAULT NULL,
  `host` varchar(32) DEFAULT NULL,
  `wifi_ssid` varchar(32) DEFAULT NULL,
  `wifi_password` varchar(32) DEFAULT NULL,
  `domain` varchar(32) DEFAULT NULL,
  `devtype` int NOT NULL,
  `sgroup` int DEFAULT 0,
  `verified` bit(1) DEFAULT 0,
  PRIMARY KEY (`id`),
  KEY `I_TOUCHS_USER_ID` (`userid`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

DROP TABLE IF EXISTS `verifycode`;
CREATE TABLE `verifycode` (
  `checkid` bigint(20) auto_increment NOT NULL,
  `code` varchar(6) DEFAULT NULL,
  `userid` bigint(20) DEFAULT NULL,
  `email` varchar(128) DEFAULT NULL,  
  `phone` varchar(32) DEFAULT NULL,
  `createtime` bigint(20) DEFAULT NULL,
  `expired` bigint(20) DEFAULT NULL,
  PRIMARY KEY (`checkid`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

DROP TABLE IF EXISTS `appreleased`;
CREATE TABLE `appreleased` (
  `id` bigint(20) auto_increment NOT NULL,
  `type` int DEFAULT 0,
  `app_name` char(32) DEFAULT NULL,
  `version` int DEFAULT NULL,  
  `version_name` char(16) DEFAULT NULL,
  `pkgname` char(64) DEFAULT NULL,
  `url` varchar(128) DEFAULT NULL,
  `releasetime` bigint(20) DEFAULT NULL,
  `description` varchar(256) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;