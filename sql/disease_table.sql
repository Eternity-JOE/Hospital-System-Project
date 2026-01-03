-- =====================================================
-- 病种管理模块数据库表
-- 作者：Member C
-- 日期：2025-01-XX
-- 说明：病种表和医生-病种关联表
-- =====================================================

-- 1. 病种表
CREATE TABLE IF NOT EXISTS `sys_disease` (
    `id` INT AUTO_INCREMENT PRIMARY KEY COMMENT '病种ID',
    `name` VARCHAR(100) NOT NULL COMMENT '病种名称',
    `code` VARCHAR(50) DEFAULT NULL COMMENT '病种编码',
    `description` VARCHAR(255) DEFAULT NULL COMMENT '病种描述',
    `category` VARCHAR(50) DEFAULT NULL COMMENT '病种分类',
    `status` TINYINT DEFAULT 1 COMMENT '状态：1-启用，0-停用',
    `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    UNIQUE KEY `uk_name` (`name`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='病种表';

-- 2. 医生-病种关联表（多对多关系）
CREATE TABLE IF NOT EXISTS `sys_doctor_disease` (
    `id` INT AUTO_INCREMENT PRIMARY KEY COMMENT 'ID',
    `doctor_id` INT NOT NULL COMMENT '医生ID',
    `disease_id` INT NOT NULL COMMENT '病种ID',
    `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    UNIQUE KEY `uk_doctor_disease` (`doctor_id`, `disease_id`),
    INDEX `idx_doctor` (`doctor_id`),
    INDEX `idx_disease` (`disease_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='医生-病种关联表';

-- =====================================================
-- 初始化测试数据
-- =====================================================

-- 插入病种数据
INSERT INTO `sys_disease` (`name`, `code`, `description`, `category`, `status`) VALUES
('高血压', 'HYP', '原发性高血压、继发性高血压', '心血管', 1),
('糖尿病', 'DM', '1型糖尿病、2型糖尿病', '内分泌', 1),
('冠心病', 'CHD', '冠状动脉粥样硬化性心脏病', '心血管', 1),
('肺炎', 'PN', '细菌性肺炎、病毒性肺炎', '呼吸', 1),
('骨折', 'FX', '各种类型骨折', '骨科', 1),
('阑尾炎', 'APP', '急性阑尾炎、慢性阑尾炎', '外科', 1),
('感冒', 'COLD', '普通感冒、流行性感冒', '呼吸', 1),
('胃炎', 'GAST', '急性胃炎、慢性胃炎', '消化', 1),
('关节炎', 'ARTH', '类风湿性关节炎、骨关节炎', '骨科', 1),
('皮肤病', 'DERM', '湿疹、皮炎等', '皮肤', 1);

