-- ----------------------------
-- 1. 药品表
-- ----------------------------
DROP TABLE IF EXISTS `medicine`;
CREATE TABLE `medicine` (
  `id` int NOT NULL AUTO_INCREMENT,
  `name` varchar(100) NOT NULL,
  `type` varchar(50) DEFAULT NULL,
  `price` decimal(10,2) NOT NULL DEFAULT '0.00',
  `stock` int NOT NULL DEFAULT '0',
  `unit` varchar(20) DEFAULT '盒',
  `status` tinyint DEFAULT '1',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

INSERT INTO `medicine` VALUES (1, '阿莫西林胶囊', '西药', 15.50, 100, '盒', 1);
INSERT INTO `medicine` VALUES (2, '板蓝根颗粒', '中成药', 12.00, 200, '袋', 1);

-- ----------------------------
-- 2. 收费账单表
-- ----------------------------
DROP TABLE IF EXISTS `bill`;
CREATE TABLE `bill` (
  `id` int NOT NULL AUTO_INCREMENT,
  `patient_id` int NOT NULL,
  `total_amount` decimal(10,2) NOT NULL,
  `status` tinyint DEFAULT '0',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP,
  `pay_time` datetime DEFAULT NULL,
  `doctor_id`int NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

INSERT INTO `bill` ('doctor_id',`patient_id`, `total_amount`, `status`) VALUES (1,1, 50.00, 0);
