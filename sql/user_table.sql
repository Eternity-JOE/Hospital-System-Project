-- =====================================================
-- 用户登录表
-- 说明：存储三种角色的登录账户信息
-- =====================================================

-- 用户登录表
CREATE TABLE IF NOT EXISTS `sys_user` (
    `id` INT AUTO_INCREMENT PRIMARY KEY COMMENT '用户ID',
    `username` VARCHAR(50) NOT NULL COMMENT '用户名',
    `password` VARCHAR(100) NOT NULL COMMENT '密码',
    `role` VARCHAR(20) NOT NULL COMMENT '角色：admin-管理员，doctor-医生，patient-病人',
    `ref_id` INT DEFAULT NULL COMMENT '关联ID（医生关联sys_doctor.id，病人关联sys_patient.id）',
    `status` TINYINT DEFAULT 1 COMMENT '状态：1-启用，0-禁用',
    `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    UNIQUE KEY `uk_username` (`username`),
    INDEX `idx_role` (`role`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户登录表';

-- =====================================================
-- 初始化测试数据
-- =====================================================

-- 管理员账户（1个）
INSERT INTO `sys_user` (`username`, `password`, `role`, `ref_id`) VALUES
('管理员', '123456', 'admin', NULL);

-- 医生账户（关联sys_doctor表中的医生）
-- 医生ID: 1-张建国, 2-李秀英, 3-王明华, 4-王医生(外科骨科), 5-赵志强, ...
INSERT INTO `sys_user` (`username`, `password`, `role`, `ref_id`) VALUES
('张建国', '123456', 'doctor', 1),
('李秀英', '123456', 'doctor', 2),
('王明华', '123456', 'doctor', 3),
('王医生', '123456', 'doctor', 4),
('赵志强', '123456', 'doctor', 5),
('孙丽娟', '123456', 'doctor', 6);

-- 病人账户（关联sys_patient表中的病人）
INSERT INTO `sys_user` (`username`, `password`, `role`, `ref_id`) VALUES
('张三', '123456', 'patient', 1),
('李四', '123456', 'patient', 2),
('王五', '123456', 'patient', 3);
