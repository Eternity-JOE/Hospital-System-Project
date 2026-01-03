-- =====================================================
-- 医生表
-- 作者：白轩宇（Member B）
-- 日期：2025-12-23
-- 说明：医生基本信息表，用于挂号时选择医生
-- =====================================================

-- 医生表
CREATE TABLE IF NOT EXISTS `sys_doctor` (
    `id` INT AUTO_INCREMENT PRIMARY KEY COMMENT '医生ID',
    `name` VARCHAR(50) NOT NULL COMMENT '医生姓名',
    `gender` TINYINT DEFAULT 1 COMMENT '性别：1-男，2-女',
    `title` VARCHAR(50) DEFAULT NULL COMMENT '职称（主任医师/副主任医师/主治医师等）',
    `department_id` INT NOT NULL COMMENT '所属科室ID',
    `phone` VARCHAR(20) DEFAULT NULL COMMENT '联系电话',
    `specialty` VARCHAR(255) DEFAULT NULL COMMENT '擅长领域',
    `status` TINYINT DEFAULT 1 COMMENT '状态：1-在职，0-离职',
    `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    INDEX `idx_department` (`department_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='医生表';

-- =====================================================
-- 初始化测试数据
-- =====================================================

INSERT INTO `sys_doctor` (`name`, `gender`, `title`, `department_id`, `phone`, `specialty`, `status`) VALUES
('张医生', 1, '主任医师', 1, '13900000001', '心血管疾病、高血压', 1),
('李医生', 2, '副主任医师', 1, '13900000002', '呼吸系统疾病', 1),
('王医生', 1, '主治医师', 2, '13900000003', '骨科手术、关节置换', 1),
('赵医生', 2, '主任医师', 2, '13900000004', '普外科手术', 1),
('陈医生', 1, '副主任医师', 3, '13900000005', '儿童常见病', 1),
('刘医生', 2, '主治医师', 4, '13900000006', '妇科疾病、产检', 1),
('周医生', 1, '主任医师', 5, '13900000007', '急诊急救', 1),
('吴医生', 2, '主治医师', 6, '13900000008', '眼科手术、白内障', 1);
