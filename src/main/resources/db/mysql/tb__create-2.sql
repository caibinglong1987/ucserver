DROP TABLE IF EXISTS `user_profile`;
CREATE TABLE `user_profile` (
  `id` bigint(20) auto_increment NOT NULL,
  `userid` bigint(20) DEFAULT NULL,
  `money` decimal(10,2) DEFAULT NULL,
  `money_type` int DEFAULT NULL,
  `sex` int DEFAULT 0,
  `birthday` datetime DEFAULT NULL,
  `pay_points` int DEFAULT 0,
  `rank_points` int DEFAULT 0,
  `rank_id` int DEFAULT 0,
  `address_id` bigint(20) DEFAULT NULL,
  `visit_count` bigint(20) DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `U_USERPROFILE_USERID` (`userid`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

DROP TABLE IF EXISTS `user_credits`;
CREATE TABLE `user_credits` (
  `id` bigint(20) auto_increment NOT NULL,
  `name` varchar(32) NOT NULL,
  `logo` varchar(256) NOT NULL,
  `min_points` bigint(20) DEFAULT NULL,
  `max_points` bigint(20) DEFAULT NULL,
  `discount` int DEFAULT 0,
  PRIMARY KEY (`id`),
  KEY `I_USER_CREDITS_NAME` (`name`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

DROP TABLE IF EXISTS `user_address`;
CREATE TABLE `user_address` (
  `id` bigint(20) auto_increment NOT NULL,
  `userid` bigint(20) NOT NULL,
  `consignee` varchar(32) DEFAULT NULL,
  `email` varchar(128) DEFAULT NULL,
  `country` int DEFAULT 1,
  `province` int DEFAULT 0,
  `city` int DEFAULT 0,
  `district` int DEFAULT 0,
  `address` varchar(255) DEFAULT NULL,
  `zipcode` varchar(16) DEFAULT NULL,
  `mobile` varchar(32) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `I_ADDRESS_USER_ID` (`userid`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

DROP TABLE IF EXISTS `user_thirdparty`;
CREATE TABLE `user_thirdparty` (
  `id` bigint(20) auto_increment NOT NULL,
  `userid` bigint(20) NOT NULL,
  `openid` varchar(32) NOT NULL,
  `companyid` bigint(20) NOT NULL,
  `rank_points` int DEFAULT NULL,
  `credit_limit` int DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `I_USER_THIRDPARTY_USER_ID` (`userid`), 
  UNIQUE KEY `I_USER_THIRDPARTY_OPENID` (`openid`,`companyid`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

DROP TABLE IF EXISTS `employee_profile`;
CREATE TABLE `employee_profile` (
  `id` bigint(20) auto_increment NOT NULL,
  `code` varchar(32) NOT NULL,
  `userid` bigint(20) DEFAULT NULL,
  `dept` varchar(64) DEFAULT NULL,  
  `position` varchar(64) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `I_EMPLOYEE_USER_ID` (`userid`)  
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

DROP TABLE IF EXISTS `role`;
CREATE TABLE `role` (
  `roleid` bigint(20) auto_increment NOT NULL,
  `pid` bigint(20) DEFAULT 0,
  `name` varchar(32) DEFAULT NULL,
  `type` varchar(32) DEFAULT NULL,  
  `description` varchar(256) DEFAULT NULL,
  `leaf` bit(1) DEFAULT 0,
  `sort` int DEFAULT 0,
  `createtime` datetime DEFAULT NULL,
  `modifytime` datetime DEFAULT NULL,
  PRIMARY KEY (`roleid`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

DROP TABLE IF EXISTS `module`;
CREATE TABLE `module` (
  `id` bigint(20) auto_increment NOT NULL,
  `pid` bigint(20) DEFAULT 0,
  `code` varchar(32) DEFAULT NULL,
  `type` varchar(32) DEFAULT NULL,  
  `url` varchar(256) DEFAULT NULL,
  `image` varchar(256) DEFAULT NULL,
  `description` varchar(256) DEFAULT NULL,
  `expanded` bit(1) DEFAULT 0,
  `sort` int DEFAULT 0,
  `createtime` datetime DEFAULT NULL,
  `modifytime` datetime DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

DROP TABLE IF EXISTS `admin_role`;
CREATE TABLE `admin_role` (
  `id` bigint(20) auto_increment NOT NULL,
  `userid` bigint(20) NOT NULL,
  `roleid` bigint(20) NOT NULL,
  PRIMARY KEY (`id`),
  KEY `I_ADMINROLE_USER_ID` (`userid`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

DROP TABLE IF EXISTS `role_module`;
CREATE TABLE `role_module` (
  `id` bigint(20) auto_increment NOT NULL,
  `roleid` bigint(20) NOT NULL,
  `moduleid` bigint(20) NOT NULL,
  PRIMARY KEY (`id`),
  KEY `I_ROLEMODULE_ROLE_ID` (`roleid`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

DROP TABLE IF EXISTS `touchchans`;
CREATE TABLE `touchchans` (
  `id` bigint(20) auto_increment NOT NULL,
  `subid` int NOT NULL,
  `devid` bigint(20) NOT NULL,
  `userid` bigint(20) NOT NULL,
  `phone` varchar(32) DEFAULT NULL,
  `domain` varchar(32) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `I_TOUCHCHANS_USER_ID` (`userid`),
  KEY `I_TOUCHCHANS_DEV_ID` (`devid`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

DROP TABLE IF EXISTS `datacard`;
CREATE TABLE `datacard` (
  `id` bigint(20) NOT NULL,
  `iccid` varchar(20) NOT NULL,
  `imsi` bigint(20) DEFAULT NULL,
  `userid` bigint(20) NOT NULL,
  `ki` varchar(32) DEFAULT NULL,
  `createtime` datetime DEFAULT NULL,
  `distributor` bigint(20) DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `I_DATACARD_ICCID` (`iccid`),
  KEY `I_DATACARD_IMSI` (`imsi`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

DROP TABLE IF EXISTS `server`;
CREATE TABLE `server` (
  `id` bigint(20) auto_increment NOT NULL,
  `hostname` varchar(32) NOT NULL,
  `hostip` varchar(32) NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `I_SERVER_HOSTNAME` (`hostname`),
  UNIQUE KEY `I_SERVER_HOSTIP` (`hostip`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

DROP TABLE IF EXISTS `service`;
CREATE TABLE `service` (
  `id` bigint(20) auto_increment NOT NULL,
  `servicename` varchar(32) NOT NULL,
  `servicecode` varchar(4) NOT NULL,
  `protocol` varchar(32) NOT NULL,
  `serviceport` int DEFAULT 0,
  PRIMARY KEY (`id`),
  UNIQUE KEY `I_SERVER_SERVICENAME` (`servicename`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

DROP TABLE IF EXISTS `servicenode`;
CREATE TABLE `servicenode` (
  `id` bigint(20) auto_increment NOT NULL,
  `serviceid` bigint(20) NOT NULL,
  `serverid` bigint(20) NOT NULL,
  PRIMARY KEY (`id`),
  KEY `I_SERVICENODE_SERVICE_ID` (`serviceid`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;


DROP TABLE IF EXISTS `product`;
CREATE TABLE `product` (
  `id` bigint(20) auto_increment NOT NULL,
  `storeid` bigint(20) NOT NULL,
  `brandid` bigint(20) NOT NULL,
  `categoryid` bigint(20) NOT NULL,
  `name` varchar(64) NOT NULL,
  `subname` varchar(255) NOT NULL,
  `image` varchar(255) NOT NULL,
  `description` varchar(512) NOT NULL,
  `unit_price` decimal(10,2) NOT NULL,
  `quantity_per_unit` int DEFAULT 1,
  `validity` int DEFAULT -1,
  `stock_number` int DEFAULT 1,
  `sell_count` bigint(20) DEFAULT 0,
  `groupid` bigint(20) DEFAULT 0,
  `is_real` bit(1) DEFAULT 0,
  `is_delete` bit(1) DEFAULT 0,
  `is_package` bit(1) DEFAULT 0,
  `createtime` datetime NOT NULL,
  `modifytime` datetime DEFAULT NULL,
  `deletetime` datetime DEFAULT NULL,
  `sale_state` int DEFAULT 0,
  `typeid` bigint(20) NOT NULL,  
  PRIMARY KEY (`id`),
  KEY `I_PRODUCT_STORE_ID` (`storeid`),
  KEY `I_PRODUCT_BRAND_ID` (`brandid`),
  KEY `I_PRODUCT_CATEGORY_ID` (`categoryid`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

DROP TABLE IF EXISTS `prd_type`;
CREATE TABLE `prd_type` (
  `id` bigint(20) auto_increment NOT NULL,
  `name` varchar(60) NOT NULL DEFAULT '',
  `enabled` bit(1) NOT NULL DEFAULT 1,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

DROP TABLE IF EXISTS `prd_attr`;
CREATE TABLE `prd_attr` (
  `id` bigint(20) auto_increment NOT NULL,
  `productid` bigint(20) NOT NULL,
  `attrid` bigint(20) NOT NULL,
  `value` text NOT NULL,
  `price` varchar(60) NOT NULL DEFAULT '',
  PRIMARY KEY (`id`),
  KEY `I_PRDATTR_PRD_ID` (`productid`),
  KEY `I_PRDATTR_ATTR_ID` (`attrid`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

DROP TABLE IF EXISTS `prd_category`;
CREATE TABLE `prd_category` (
  `id` bigint(20) auto_increment NOT NULL,
  `pid` bigint(20) NOT NULL,
  `path` varchar(255) NOT NULL,
  `name` varchar(64) NOT NULL,
  `subname` varchar(255) NOT NULL,
  `logo` varchar(255) NOT NULL,
  `description` varchar(512) NOT NULL,
  `sort` int NOT NULL,
  `createtime` datetime NOT NULL,
  `modifytime` datetime DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

DROP TABLE IF EXISTS `prd_brand`;
CREATE TABLE `prd_brand` (
  `id` bigint(20) auto_increment NOT NULL,
  `name` varchar(64) NOT NULL,
  `subname` varchar(255) NOT NULL,
  `logo` varchar(255) NOT NULL,
  `description` varchar(512) NOT NULL,
  `sort` int NOT NULL,
  `createtime` datetime NOT NULL,
  `modifytime` datetime DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

DROP TABLE IF EXISTS `prd_package`;
CREATE TABLE `prd_package` (
  `id` bigint(20) auto_increment NOT NULL,
  `packageid` bigint(20) NOT NULL,
  `productid` bigint(20) NOT NULL,
  PRIMARY KEY (`id`),
  KEY `I_PACKAGE_PACKAGE_ID` (`packageid`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

DROP TABLE IF EXISTS `store`;
CREATE TABLE `store` (
  `id` bigint(20) auto_increment NOT NULL,
  `userid` bigint(20) NOT NULL,
  `name` varchar(64) NOT NULL,
  `logo` varchar(255) NOT NULL,
  `intro` varchar(255) NOT NULL,
  `location` varchar(255) NOT NULL,
  `source` varchar(255) DEFAULT NULL,
  `description` varchar(512) NOT NULL,
  `invoce_content` varchar(255) NOT NULL,
  `createtime` datetime NOT NULL,
  `modifytime` datetime DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `I_STORE_USER_ID` (`userid`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

DROP TABLE IF EXISTS `cart`;
CREATE TABLE `cart` (
  `id` bigint(20) auto_increment NOT NULL,
  `userid` bigint(20) NOT NULL,
  `sessionid` varchar(64) NOT NULL,
  `productid` bigint(20) NOT NULL,
  `unit_price` decimal(10,2) NOT NULL,
  `quantity` int DEFAULT 1,
  `discount` decimal(10,2) DEFAULT 0.0,
  `attrs` text,
  PRIMARY KEY (`id`),
  KEY `I_CART_USER_ID` (`userid`),
  KEY `I_CART_SESSION_ID` (`sessionid`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

DROP TABLE IF EXISTS `prd_discount`;
CREATE TABLE `prd_discount` (
  `id` bigint(20) auto_increment NOT NULL,
  `productid` bigint(20) NOT NULL,
  `quantity` int NOT NULL,
  `discount` int DEFAULT 100,
  `effect_datetime` datetime NOT NULL,
  `failure_datetime` datetime DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `I_DISCOUNT_PRODUCT_ID` (`productid`),
  KEY `I_DISCOUNT_EFFECT_DATETIME` (`effect_datetime`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

DROP TABLE IF EXISTS `prd_unitprice`;
CREATE TABLE `prd_unitprice` (
  `id` bigint(20) auto_increment NOT NULL,
  `productid` bigint(20) NOT NULL,
  `price` decimal(10,2) NOT NULL,
  `effect_datetime` datetime NOT NULL,
  `failure_datetime` datetime DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `I_UNITPRICE_PRODUCT_ID` (`productid`),
  KEY `I_UNITPRICE_EFFECT_DATETIME` (`effect_datetime`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

DROP TABLE IF EXISTS `prd_order`;
CREATE TABLE `prd_order` (
  `id` bigint(20) auto_increment NOT NULL,
  `userid` bigint(20) NOT NULL,
  `ship_address` bigint(20) DEFAULT NULL,
  `shipping_status` int DEFAULT 0,
  `pay_status` int DEFAULT 0,
  `order_status` int DEFAULT 0,
  `shipping_id` int DEFAULT 0,
  `payid` int DEFAULT 0,
  `payvoucher` varchar(64) DEFAULT NULL,
  `shipping_fee` decimal(10,2),
  `invoice_no` varchar(64) DEFAULT NULL,
  `price` decimal(10,2) NOT NULL,
  `discount` decimal(10,2) DEFAULT NULL,
  `voucher_amount` decimal(10,2) DEFAULT NULL,
  `payable_amount` decimal(10,2) NOT NULL,
  `tax_fee` decimal(10,2),
  `receipt` bit(1) DEFAULT 0,
  `inv_payee` varchar(120) DEFAULT NULL,
  `inv_content` varchar(255) DEFAULT NULL,
  `obtainvoucher` bit(1) DEFAULT 0,
  `outletid` bigint(20) DEFAULT NULL,
  `obtaintime` datetime DEFAULT NULL,
  `createtime` datetime NOT NULL,
  `confirmtime` datetime DEFAULT NULL,
  `paytime` datetime DEFAULT NULL,
  `shippingime` datetime DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `I_ORDER_USER_ID` (`userid`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

DROP TABLE IF EXISTS `orderdetail`;
CREATE TABLE `orderdetail` (
  `id` bigint(20) auto_increment NOT NULL,
  `orderid` bigint(20) NOT NULL,
  `productid` bigint(20) NOT NULL,
  `unit_price` decimal(10,2) NOT NULL,
  `quantity_per_unit` int DEFAULT 1,
  `quantity` int DEFAULT 1,
  `discount` decimal(10,2) DEFAULT 0.0,
  `status` int DEFAULT 0,
  `attrs` text,
  `userid` bigint(20) NOT NULL,
  `effect_datetime` datetime DEFAULT NULL,
  `failure_datetime` datetime DEFAULT NULL,
  `areaname` varchar(32) DEFAULT NULL,
  `phone` varchar(32) DEFAULT NULL,  
  `call_duration` int DEFAULT NULL, 
  `simids` text DEFAULT NULL,  
  `source` varchar(255) DEFAULT NULL,    
  PRIMARY KEY (`id`),
  KEY `I_ORDERDETAIL_ORDER_ID` (`orderid`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

DROP TABLE IF EXISTS `refund`;
CREATE TABLE `refund` (
  `id` bigint(20) auto_increment NOT NULL,
  `userid` bigint(20) NOT NULL,
  `orderid` bigint(20) NOT NULL,
  `money` decimal(10,2) NOT NULL,
  `refund_account` varchar(64) NOT NULL,
  `refund_type` int DEFAULT 1,
  `bank_name` varchar(32) DEFAULT NULL,
  `bank_branch` varchar(32) DEFAULT NULL,
  `username` varchar(32) DEFAULT NULL,
  `createtime` datetime NOT NULL,
  `status` int DEFAULT 0,
  PRIMARY KEY (`id`),
  KEY `I_REFUND_USER_ID` (`userid`),
  KEY `I_REFUND_ORDER_ID` (`orderid`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

DROP TABLE IF EXISTS `outlet`;
CREATE TABLE `outlet` (
  `id` bigint(20) auto_increment NOT NULL,
  `name` varchar(255) DEFAULT NULL,
  `province` int DEFAULT 0,
  `city` int DEFAULT 0,
  `district` int DEFAULT 0,
  `address` varchar(255) DEFAULT NULL,
  `mobile` varchar(32) DEFAULT NULL,
  `type` int DEFAULT 0,
  `companyid` bigint(20) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

DROP TABLE IF EXISTS `outlet_prd`;
CREATE TABLE `outlet_prd` (
  `id` bigint(20) auto_increment NOT NULL,
  `outletid` bigint(20) NOT NULL,
  `productid` bigint(20) NOT NULL,
  `stock_number` int NOT NULL,
  `sell_count` bigint(20) NOT NULL,
  PRIMARY KEY (`id`),
  KEY `I_OUTLETPRD_OUTLET_ID` (`outletid`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

DROP TABLE IF EXISTS `evoucher`;
CREATE TABLE `evoucher` (
  `id` bigint(20) auto_increment NOT NULL,
  `name` varchar(255) NOT NULL,
  `money` decimal(10,2) NOT NULL,
  `min_amount` decimal(10,2) DEFAULT NULL,
  `max_amount` decimal(10,2) DEFAULT NULL,
  `effect_datetime` datetime DEFAULT NULL,
  `failure_datetime` datetime DEFAULT NULL,
  `background` varchar(255) NOT NULL,
  `repeatable` bit(1) NOT NULL DEFAULT 0,
  `description` varchar(255) DEFAULT NULL,
  `type` int NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

DROP TABLE IF EXISTS `user_evoucher`;
CREATE TABLE `user_evoucher` (
  `id` bigint(20) auto_increment NOT NULL,
  `evoucherid` bigint(20) NOT NULL,
  `userid` bigint(20) NOT NULL,
  `evoucher_sn` bigint(20) NOT NULL,
  `used_datetime` datetime DEFAULT NULL,
  `orderid` bigint(20) DEFAULT NULL,
  `image` varchar(255) NOT NULL,
  `background` varchar(255) DEFAULT NULL,
  `description` varchar(255) DEFAULT NULL,
  `effect_datetime` datetime DEFAULT NULL,
  `failure_datetime` datetime DEFAULT NULL,
  `max_amount` decimal(10,2) DEFAULT NULL,
  `min_amount` decimal(10,2) DEFAULT NULL,
  `money` decimal(10,2) DEFAULT NULL,
  `name` varchar(255) DEFAULT NULL,
  `repeatable` bit(1) NOT NULL DEFAULT 0,
  PRIMARY KEY (`id`),
  KEY `I_USEREVOUCHER_EVOUCHER_ID` (`evoucherid`),
  KEY `I_USEREVOUCHER_USER_ID` (`userid`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

DROP TABLE IF EXISTS `prd_evoucher`;
CREATE TABLE `prd_evoucher` (
  `id` bigint(20) auto_increment NOT NULL,
  `evoucherid` bigint(20) NOT NULL,
  `productid` bigint(20) NOT NULL,
  PRIMARY KEY (`id`),
  KEY `I_PRDEVOUCHER_EVOUCHER_ID` (`evoucherid`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

DROP TABLE IF EXISTS `evoucher_verify`;
CREATE TABLE `evoucher_verify` (
  `id` bigint(20) auto_increment NOT NULL,
  `evoucherid` bigint(20) NOT NULL,
  `evoucher_sn` bigint(20) NOT NULL,
  `evoucher_name` varchar(255) DEFAULT NULL,
  `action_userid` bigint(20) NOT NULL,  
  `userid` bigint(20) NOT NULL,
  `orderid` bigint(20) DEFAULT NULL,
  `status` int NOT NULL,
  `createtime` datetime NOT NULL,
  PRIMARY KEY (`id`),
  KEY `I_EVOUCHERVERIFY_EVOUCHER_ID` (`evoucherid`),
  KEY `I_EVOUCHERVERIFY_ACTIONUSER_ID` (`action_userid`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

DROP TABLE IF EXISTS `datacard_traffic`;
CREATE TABLE `datacard_traffic` (
  `id` bigint(20) auto_increment NOT NULL,
  `datacardid` bigint(20) DEFAULT NULL,
  `orderid` bigint(20) NOT NULL,
  `orderdetailid` bigint(20) NOT NULL,  
  `iccid` varchar(20) DEFAULT NULL,
  `effect_datetime` datetime NOT NULL,
  `failure_datetime` datetime NOT NULL,
  `areaname` varchar(32) NOT NULL,
  `userid` bigint(20) NOT NULL,
  `imsi` varchar(15) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `I_DATATRAFFIC_ICCID` (`iccid`),
  KEY `I_DATATRAFFIC_USERID` (`userid`),
  KEY `I_DATATRAFFIC_EFFECT_DATETIME` (`effect_datetime`),
  KEY `I_DATATRAFFIC_FAILURE_DATETIME` (`failure_datetime`),
  KEY `I_DATATRAFFIC_IMSI` (`imsi`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

DROP TABLE IF EXISTS `touch_voice`;
CREATE TABLE `touch_voice` (
  `id` bigint(20) auto_increment NOT NULL,
  `touchdevid` bigint(20) NOT NULL,
  `orderid` bigint(20) DEFAULT NULL,
  `orderdetailid` bigint(20) DEFAULT NULL, 
  `touchchansid` bigint(20) DEFAULT NULL,
  `phone` varchar(20) NOT NULL,
  `effect_datetime` datetime NOT NULL,
  `failure_datetime` datetime NOT NULL,
  `userid` bigint(20) NOT NULL, 
  `sgroup` int NOT NULL,  
  PRIMARY KEY (`id`),
  KEY `I_TOUCHVOICE_ORDERDETAILID` (`orderdetailid`),
  KEY `I_TOUCHVOICE_PHONE` (`phone`),
  KEY `I_TOUCHVOICE_USERID` (`userid`),
  KEY `I_TOUCHVOICE_EFFECT_DATETIME` (`effect_datetime`),
  KEY `I_TOUCHVOICE_FAILURE_DATETIME` (`failure_datetime`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

DROP TABLE IF EXISTS `user_trafficvoice`;
CREATE TABLE `user_trafficvoice` (
  `id` bigint(20) auto_increment NOT NULL,
  `userid` bigint(20) NOT NULL,
  `orderid` bigint(20) DEFAULT NULL, 
  `orderdetailid` bigint(20) DEFAULT NULL, 
  `productid` bigint(20) DEFAULT NULL,
  `effect_datetime` datetime DEFAULT NULL,
  `failure_datetime` datetime DEFAULT NULL,
  `areaname` varchar(32) DEFAULT NULL,
  `call_duration` int DEFAULT NULL, 
  `simid` varchar(20) DEFAULT NULL,  
  `source` varchar(255) DEFAULT NULL,  
  PRIMARY KEY (`id`),
  KEY `I_U_TRAFFICVOICE_USERID` (`userid`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

DROP TABLE IF EXISTS `cdr`;
CREATE TABLE `cdr` (
  `id` bigint(20) NOT NULL,
  `caller` varchar(32) NOT NULL,
  `callee` varchar(32) NOT NULL,
  `callid` varchar(64) NOT NULL,
  `createtime` datetime NOT NULL,
  `setuptime` int NOT NULL,
  `starttime` datetime NOT NULL,
  `endtime` datetime NOT NULL,
  `duration` int NOT NULL,
  `direction` bit(1) NOT NULL,
  `userid` bigint(20) DEFAULT NULL,
  `sip_code` varchar(3) NOT NULL,
  `sip_reason` varchar(32) DEFAULT NULL,
  `relayphone` varchar(32) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `I_CDR_USERID` (`userid`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
DROP TABLE IF EXISTS `msgpublish`;
CREATE TABLE `msgpublish` (
  `id` bigint(20) NOT NULL,
  `caller` varchar(32) NOT NULL,
  `callee` varchar(32) NOT NULL,
  `type` int DEFAULT 1,
  `message` text NOT NULL,
  `createtime` datetime NOT NULL,
  `publishtime` datetime DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `I_MSGPUBLISH_CALLER` (`caller`),
  KEY `I_MSGPUBLISH_CALLEE` (`callee`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
DROP TABLE IF EXISTS `mdr`;
CREATE TABLE `mdr` (
  `id` bigint(20) NOT NULL,
  `caller` varchar(32) NOT NULL,
  `callee` varchar(32) NOT NULL,
  `callid` varchar(64) NOT NULL,
  `time` datetime NOT NULL,
  `message` text NOT NULL,
  `direction` bit(1) NOT NULL,
  `userid` bigint(20) DEFAULT NULL,
  `sip_code` varchar(3) NOT NULL,
  `sip_reason` varchar(32) DEFAULT NULL,
  `relayphone` varchar(32) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `I_MDR_USERID` (`userid`),
  KEY `I_MDR_CALLER` (`caller`),
  KEY `I_MDR_CALLEE` (`callee`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
DROP TABLE IF EXISTS `areagroup`;
CREATE TABLE `areagroup` (
  `id` bigint(20) auto_increment NOT NULL,
  `groupname` varchar(32) NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

DROP TABLE IF EXISTS `areacode`;
CREATE TABLE `areacode` (
  `id` bigint(20) auto_increment NOT NULL,
  `areacode` varchar(4) NOT NULL,
  `areaname` varchar(32) NOT NULL,
  `nationalcode` varchar(4) NOT NULL,
  `moneycode` varchar(16) NOT NULL,
  `groupid` bigint(20) NOT NULL,
  `timediff` int NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

DROP TABLE IF EXISTS `city`;
CREATE TABLE `city` (
  `id` bigint(20) auto_increment NOT NULL,
  `pid` bigint(20) NOT NULL,
  `name` varchar(32) NOT NULL,
  `type` int DEFAULT 0,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

DROP TABLE IF EXISTS `shipping`;
CREATE TABLE `shipping` (
  `id` bigint(20) auto_increment NOT NULL,
  `code` varchar(20) NOT NULL,
  `name` varchar(120) NOT NULL,
  `description` varchar(255) NOT NULL,
  `insure` varchar(10) NOT NULL,
  `support_cod` bit(1) DEFAULT 0,
  `enabled` bit(0) DEFAULT 0,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

DROP TABLE IF EXISTS `shipping_area`;
CREATE TABLE `shipping_area` (
  `id` bigint(20) auto_increment NOT NULL,
  `shippingid` int NOT NULL,
  `name` varchar(120) NOT NULL,
  `fee` decimal(10,2) NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

DROP TABLE IF EXISTS `payment`;
CREATE TABLE `payment` (
  `id` bigint(20) auto_increment NOT NULL,
  `code` varchar(20) NOT NULL,
  `name` varchar(120) NOT NULL,
  `description` varchar(255) NOT NULL,
  `fee` varchar(10) NOT NULL,
  `sort` int DEFAULT 0,
  `enabled` bit(0) DEFAULT 0,
  `terminaltype` int DEFAULT 1,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

DROP TABLE IF EXISTS `attribute`;
CREATE TABLE `attribute` (
  `id` bigint(20) auto_increment NOT NULL,
  `typeid` bigint(20) NOT NULL DEFAULT '0',
  `name` varchar(60) NOT NULL DEFAULT '',
  `varname` varchar(60) NOT NULL DEFAULT '',
  `input_type` bit(1) NOT NULL DEFAULT 0,
  `type` varchar(10) NOT NULL DEFAULT '',
  `values` text NOT NULL,
  PRIMARY KEY (`id`),
  KEY `ATTR_TYPE_ID` (`typeid`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

DROP TABLE IF EXISTS `company`;
CREATE TABLE `company` (
  `id` bigint(20) auto_increment NOT NULL,
  `code` varchar(20) NOT NULL,
  `name` varchar(120) NOT NULL,
  `description` varchar(255) NOT NULL,
  `comptype` int NOT NULL,
  `enabled` bit(0) DEFAULT 0,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

DROP TABLE IF EXISTS `datacard_area`;
CREATE TABLE `datacard_area` (
  `id` bigint(20) auto_increment NOT NULL,
  `datacard_type` int DEFAULT 1,
  `areaname` varchar(32) NOT NULL,
  `nationalcode` varchar(4) NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;