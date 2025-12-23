-- =====================================================
-- 患者管理模块数据库表
-- 作者：白轩宇（Member B）
-- 日期：2025-12-23
-- 说明：患者基本信息表
-- =====================================================

-- 患者表
CREATE TABLE IF NOT EXISTS `sys_patient` (
    `id` INT AUTO_INCREMENT PRIMARY KEY COMMENT '患者ID',
    `name` VARCHAR(50) NOT NULL COMMENT '患者姓名',
    `gender` VARCHAR(10) DEFAULT NULL COMMENT '性别',
    `age` INT DEFAULT NULL COMMENT '年龄',
    `phone` VARCHAR(20) DEFAULT NULL COMMENT '联系电话',
    `address` VARCHAR(255) DEFAULT NULL COMMENT '家庭住址',
    `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='患者表';

-- =====================================================
-- 初始化测试数据
-- =====================================================

INSERT INTO `sys_patient` (`name`, `gender`, `age`, `phone`, `address`) VALUES
('张三', '男', 35, '13800138001', '青岛市市南区'),
('李四', '男', 28, '13800138002', '青岛市市北区'),
('王五', '女', 42, '13800138003', '青岛市崂山区'),
('赵六', '女', 25, '13800138004', '青岛市黄岛区');
