-- =====================================================
-- 数据库初始化整合脚本
-- 在下面依次添加测试用的数据表即可
-- 说明：一次性执行所有建表和初始化数据的SQL
-- =====================================================

-- 设置编码
SET NAMES utf8mb4;

-- 选择数据库
USE hospital_db;

-- =====================================================
-- 删除已有表（按外键依赖顺序，先删子表再删主表）
-- =====================================================
DROP TABLE IF EXISTS `sys_user`;
DROP TABLE IF EXISTS `sys_doctor_leave`;
DROP TABLE IF EXISTS `bill`;
DROP TABLE IF EXISTS `medicine`;
DROP TABLE IF EXISTS `sys_doctor_disease`;
DROP TABLE IF EXISTS `sys_disease`;
DROP TABLE IF EXISTS `sys_registration`;
DROP TABLE IF EXISTS `sys_patient_record`;
DROP TABLE IF EXISTS `sys_doctor`;
DROP TABLE IF EXISTS `sys_patient`;
DROP TABLE IF EXISTS `sys_department`;

-- =====================================================
-- 请将下面的路径替换成你本地的完整路径
-- =====================================================

-- 1. 创建科室表、挂号表、患者信誉表 + 科室测试数据
source E:/github/Hospital-System-Project/sql/registration_tables.sql

-- 2. 创建病人表 + 病人测试数据
source E:/github/Hospital-System-Project/sql/patient_table.sql

-- 3. 创建医生表 + 医生测试数据
source E:/github/Hospital-System-Project/sql/doctor_table.sql

-- 4. 创建病种表和医生-病种关联表 + 病种测试数据
source E:/github/Hospital-System-Project/sql/disease_table.sql

-- 5. 为医生表添加排班字段
source E:/github/Hospital-System-Project/sql/update_doctor_schedule.sql
  
-- 6. 创建药品表、收费表 + 测试数据
source E:/github/Hospital-System-Project/sql/medicine_billing_table.sql

-- 7. 创建用户登录表 + 测试账户数据
source E:/github/Hospital-System-Project/sql/user_table.sql

-- 8. 创建医生请假表
source E:/github/Hospital-System-Project/sql/leave_table.sql

-- 9. 初始化挂号数据（动态生成未来三天的数据）
source E:/github/Hospital-System-Project/sql/init_registration_data.sql
