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
-- 请将下面的路径替换成你本地的完整路径
-- =====================================================

-- 1. 创建科室表、挂号表、患者信誉表 + 科室测试数据
source E:/java web final project/Hospital-System-Project-master/Hospital-System-Project-master/sql/registration_tables.sql

-- 2. 创建病人表 + 病人测试数据
source E:/java web final project/Hospital-System-Project-master/Hospital-System-Project-master/sql/patient_table.sql

-- 3. 创建医生表 + 医生测试数据
source E:/java web final project/Hospital-System-Project-master/Hospital-System-Project-master/sql/doctor_table.sql

-- 4. 创建病种表和医生-病种关联表 + 病种测试数据
source E:/java web final project/Hospital-System-Project-master/Hospital-System-Project-master/sql/disease_table.sql

-- 5. 为医生表添加排班字段
source E:/java web final project/Hospital-System-Project-master/Hospital-System-Project-master/sql/update_doctor_schedule.sql
  
-- 6. 创建药品表、收费表 + 测试数据
source E:/java web final project/Hospital-System-Project-master/Hospital-System-Project-master/sql/medicine_billing_table.sql
