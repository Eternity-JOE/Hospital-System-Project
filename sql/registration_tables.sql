-- =====================================================
-- 挂号管理模块数据库表
-- 作者：Member B
-- 日期：2025-12-23
-- 说明：包含科室表、挂号表、患者信誉记录表
-- =====================================================

-- 1. 科室表
CREATE TABLE IF NOT EXISTS `sys_department` (
    `id` INT AUTO_INCREMENT PRIMARY KEY COMMENT '科室ID',
    `name` VARCHAR(50) NOT NULL COMMENT '科室名称',
    `code` VARCHAR(20) NOT NULL COMMENT '科室编码',
    `description` VARCHAR(255) DEFAULT NULL COMMENT '科室描述',
    `location` VARCHAR(100) DEFAULT NULL COMMENT '科室位置',
    `status` TINYINT DEFAULT 1 COMMENT '状态：1-启用，0-停用'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='科室表';

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
    `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    INDEX `idx_patient` (`patient_id`),
    INDEX `idx_department_date` (`department_id`, `appointment_date`),
    INDEX `idx_status` (`status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='挂号记录表';

-- 3. 患者信誉记录表（用于爽约惩罚机制）
CREATE TABLE IF NOT EXISTS `sys_patient_record` (
    `id` INT AUTO_INCREMENT PRIMARY KEY COMMENT 'ID',
    `patient_id` INT NOT NULL UNIQUE COMMENT '患者ID',
    `no_show_count` INT DEFAULT 0 COMMENT '爽约次数',
    `total_count` INT DEFAULT 0 COMMENT '总挂号次数',
    `completed_count` INT DEFAULT 0 COMMENT '完成就诊次数'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='患者信誉记录表';

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
