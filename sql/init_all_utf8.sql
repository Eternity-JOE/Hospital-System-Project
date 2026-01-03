-- =====================================================
-- 数据库初始化脚本（兼容旧版本 MySQL，使用 utf8）
-- 说明：将所有 utf8mb4 改为 utf8，兼容 MySQL 5.0+
-- =====================================================

-- 注意：如果数据库已存在，可以跳过 CREATE DATABASE 语句
-- CREATE DATABASE IF NOT EXISTS hospital_db DEFAULT CHARACTER SET utf8 COLLATE utf8_general_ci;

-- 选择数据库
USE hospital_db;

-- =====================================================
-- 1. 创建科室表、挂号表、患者信誉表
-- =====================================================

-- 1. 科室表
CREATE TABLE IF NOT EXISTS `sys_department` (
    `id` INT AUTO_INCREMENT PRIMARY KEY COMMENT '科室ID',
    `name` VARCHAR(50) NOT NULL COMMENT '科室名称',
    `code` VARCHAR(20) NOT NULL COMMENT '科室编码',
    `description` VARCHAR(255) DEFAULT NULL COMMENT '科室描述',
    `location` VARCHAR(100) DEFAULT NULL COMMENT '科室位置',
    `status` TINYINT DEFAULT 1 COMMENT '状态：1-启用，0-停用'
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='科室表';

-- 2. 挂号记录表
CREATE TABLE IF NOT EXISTS `sys_registration` (
    `id` INT AUTO_INCREMENT PRIMARY KEY COMMENT '挂号ID',
    `patient_id` INT NOT NULL COMMENT '患者ID',
    `department_id` INT NOT NULL COMMENT '科室ID',
    `doctor_id` INT DEFAULT NULL COMMENT '医生ID（可选）',
    `appointment_date` DATE NOT NULL COMMENT '预约日期',
    `time_slot` VARCHAR(10) NOT NULL COMMENT '时段：AM-上午，PM-下午',
    `registration_type` TINYINT DEFAULT 1 COMMENT '挂号类型：1-普通，2-急诊',
    `is_return` TINYINT DEFAULT 0 COMMENT '是否复诊：0-初诊，1-复诊',
    `patient_type` TINYINT DEFAULT 0 COMMENT '患者类型：0-普通，1-军人，2-老人，3-儿童',
    `status` TINYINT DEFAULT 1 COMMENT '状态：0-已取消，1-待就诊，2-已完成，3-爽约',
    `priority_score` INT DEFAULT 100 COMMENT '优先级分数',
    `queue_number` INT DEFAULT NULL COMMENT '排队序号',
    `create_time` TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    INDEX `idx_patient` (`patient_id`),
    INDEX `idx_department_date` (`department_id`, `appointment_date`),
    INDEX `idx_status` (`status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='挂号记录表';

-- 3. 患者信誉记录表
CREATE TABLE IF NOT EXISTS `sys_patient_record` (
    `id` INT AUTO_INCREMENT PRIMARY KEY COMMENT 'ID',
    `patient_id` INT NOT NULL UNIQUE COMMENT '患者ID',
    `no_show_count` INT DEFAULT 0 COMMENT '爽约次数',
    `total_count` INT DEFAULT 0 COMMENT '总挂号次数',
    `completed_count` INT DEFAULT 0 COMMENT '完成就诊次数'
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='患者信誉记录表';

-- =====================================================
-- 2. 创建病人表
-- =====================================================

CREATE TABLE IF NOT EXISTS `sys_patient` (
    `id` INT AUTO_INCREMENT PRIMARY KEY COMMENT '患者ID',
    `name` VARCHAR(50) NOT NULL COMMENT '患者姓名',
    `gender` VARCHAR(10) DEFAULT NULL COMMENT '性别',
    `age` INT DEFAULT NULL COMMENT '年龄',
    `phone` VARCHAR(20) DEFAULT NULL COMMENT '联系电话',
    `address` VARCHAR(255) DEFAULT NULL COMMENT '家庭住址',
    `create_time` TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间'
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='患者表';

-- =====================================================
-- 3. 创建医生表
-- =====================================================

CREATE TABLE IF NOT EXISTS `sys_doctor` (
    `id` INT AUTO_INCREMENT PRIMARY KEY COMMENT '医生ID',
    `name` VARCHAR(50) NOT NULL COMMENT '医生姓名',
    `gender` TINYINT DEFAULT 1 COMMENT '性别：1-男，2-女',
    `title` VARCHAR(50) DEFAULT NULL COMMENT '职称（主任医师/副主任医师/主治医师等）',
    `department_id` INT NOT NULL COMMENT '所属科室ID',
    `phone` VARCHAR(20) DEFAULT NULL COMMENT '联系电话',
    `specialty` VARCHAR(255) DEFAULT NULL COMMENT '擅长领域',
    `status` TINYINT DEFAULT 1 COMMENT '状态：1-在职，0-离职',
    `create_time` TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    INDEX `idx_department` (`department_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='医生表';

-- =====================================================
-- 4. 创建病种表和医生-病种关联表
-- =====================================================

-- 病种表
CREATE TABLE IF NOT EXISTS `sys_disease` (
    `id` INT AUTO_INCREMENT PRIMARY KEY COMMENT '病种ID',
    `name` VARCHAR(100) NOT NULL COMMENT '病种名称',
    `code` VARCHAR(50) DEFAULT NULL COMMENT '病种编码',
    `description` VARCHAR(255) DEFAULT NULL COMMENT '病种描述',
    `category` VARCHAR(50) DEFAULT NULL COMMENT '病种分类',
    `status` TINYINT DEFAULT 1 COMMENT '状态：1-启用，0-停用',
    `create_time` TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    UNIQUE KEY `uk_name` (`name`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='病种表';

-- 医生-病种关联表
CREATE TABLE IF NOT EXISTS `sys_doctor_disease` (
    `id` INT AUTO_INCREMENT PRIMARY KEY COMMENT 'ID',
    `doctor_id` INT NOT NULL COMMENT '医生ID',
    `disease_id` INT NOT NULL COMMENT '病种ID',
    `create_time` TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    UNIQUE KEY `uk_doctor_disease` (`doctor_id`, `disease_id`),
    INDEX `idx_doctor` (`doctor_id`),
    INDEX `idx_disease` (`disease_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='医生-病种关联表';

-- =====================================================
-- 5. 为医生表添加排班字段
-- =====================================================

ALTER TABLE `sys_doctor` 
ADD COLUMN `schedule` VARCHAR(255) DEFAULT NULL COMMENT '排班时间，格式：周一上午,周二下午,周三全天' AFTER `specialty`;

-- =====================================================
-- 初始化测试数据
-- =====================================================

-- 插入科室数据
INSERT INTO `sys_department` (`name`, `code`, `description`, `location`, `status`) VALUES
('内科', 'NEI', '内科疾病诊治', '门诊楼1层', 1),
('外科', 'WAI', '外科手术及诊治', '门诊楼2层', 1),
('儿科', 'ER', '儿童疾病诊治', '门诊楼1层', 1),
('妇产科', 'FU', '妇科及产科', '门诊楼3层', 1),
('急诊科', 'JI', '急诊急救', '急诊楼1层', 1),
('眼科', 'YAN', '眼部疾病诊治', '门诊楼2层', 1),
('口腔科', 'KOU', '口腔疾病诊治', '门诊楼2层', 1),
('皮肤科', 'PI', '皮肤疾病诊治', '门诊楼3层', 1);

-- 插入病人数据
INSERT INTO `sys_patient` (`name`, `gender`, `age`, `phone`, `address`) VALUES
('张三', '男', 35, '13800138001', '青岛市市南区'),
('李四', '男', 28, '13800138002', '青岛市市北区'),
('王五', '女', 42, '13800138003', '青岛市崂山区'),
('赵六', '女', 25, '13800138004', '青岛市黄岛区');

-- 插入医生数据
INSERT INTO `sys_doctor` (`name`, `gender`, `title`, `department_id`, `phone`, `specialty`, `status`) VALUES
('张医生', 1, '主任医师', 1, '13900000001', '心血管疾病、高血压', 1),
('李医生', 2, '副主任医师', 1, '13900000002', '呼吸系统疾病', 1),
('王医生', 1, '主治医师', 2, '13900000003', '骨科手术、关节置换', 1),
('赵医生', 2, '主任医师', 2, '13900000004', '普外科手术', 1),
('陈医生', 1, '副主任医师', 3, '13900000005', '儿童常见病', 1),
('刘医生', 2, '主治医师', 4, '13900000006', '妇科疾病、产检', 1),
('周医生', 1, '主任医师', 5, '13900000007', '急诊急救', 1),
('吴医生', 2, '主治医师', 6, '13900000008', '眼科手术、白内障', 1);

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

