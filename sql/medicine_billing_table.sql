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
INSERT INTO `medicine` VALUES (3, '布洛芬缓释胶囊', '西药', 18.00, 150, '盒', 1);
INSERT INTO `medicine` VALUES (4, '感冒灵颗粒', '中成药', 10.50, 180, '袋', 1);
INSERT INTO `medicine` VALUES (5, '头孢克肟分散片', '西药', 25.00, 80, '盒', 1);
INSERT INTO `medicine` VALUES (6, '复方甘草片', '中成药', 8.00, 200, '瓶', 1);
INSERT INTO `medicine` VALUES (7, '氯雷他定片', '西药', 22.00, 120, '盒', 1);
INSERT INTO `medicine` VALUES (8, '维生素C片', '保健品', 6.00, 300, '瓶', 1);

-- ----------------------------
-- 2. 收费账单表
-- ----------------------------
DROP TABLE IF EXISTS `bill`;
CREATE TABLE `bill` (
  `id` int NOT NULL AUTO_INCREMENT,
  `doctor_id` int NOT NULL COMMENT '医生ID',
  `patient_id` int NOT NULL COMMENT '病人ID',
  `registration_id` int DEFAULT NULL COMMENT '关联挂号ID',
  `diagnosis` text DEFAULT NULL COMMENT '诊断信息',
  `medicines` text DEFAULT NULL COMMENT '药品信息JSON',
  `other_cost` decimal(10,2) DEFAULT '0.00' COMMENT '其他费用',
  `total_amount` decimal(10,2) NOT NULL COMMENT '总金额',
  `status` tinyint DEFAULT '0' COMMENT '0=待缴费 1=已缴费',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `pay_time` datetime DEFAULT NULL COMMENT '缴费时间',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;
