-- =====================================================
-- 医生请假表
-- 说明：存储医生请假申请记录
-- =====================================================

CREATE TABLE IF NOT EXISTS `sys_doctor_leave` (
    `id` INT AUTO_INCREMENT PRIMARY KEY COMMENT '请假ID',
    `doctor_id` INT NOT NULL COMMENT '医生ID',
    `leave_date` DATE NOT NULL COMMENT '请假日期',
    `time_slot` VARCHAR(10) NOT NULL COMMENT '时段：AM-上午，PM-下午',
    `reason` VARCHAR(255) DEFAULT NULL COMMENT '请假原因',
    `status` TINYINT DEFAULT 0 COMMENT '状态：0-待审批，1-已批准，2-已拒绝',
    `apply_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '申请时间',
    `approve_time` DATETIME DEFAULT NULL COMMENT '审批时间',
    `approver_id` INT DEFAULT NULL COMMENT '审批人ID',
    INDEX `idx_doctor` (`doctor_id`),
    INDEX `idx_date` (`leave_date`),
    INDEX `idx_status` (`status`),
    UNIQUE KEY `uk_doctor_date_slot` (`doctor_id`, `leave_date`, `time_slot`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='医生请假表';
