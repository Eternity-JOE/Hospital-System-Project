-- =====================================================
-- 初始化挂号数据
-- 说明：动态生成未来三天的挂号数据
-- 使用 CURDATE() 确保每次执行都是相对于当前日期
-- 患者类型分布：10%军人(2)、15%老人(4)、15%小孩(8)、60%普通(0)
-- 挂号类型分布：80%普通(1)、20%急诊(2)
-- 优先级计算：基础100 + 复诊50 + 军人30 + 老人20 + 儿童15 + 急诊1000
-- =====================================================

-- 清空现有挂号数据
DELETE FROM `sys_registration`;

-- 重置自增ID
ALTER TABLE `sys_registration` AUTO_INCREMENT = 1;

-- 今天的日期变量
SET @today = CURDATE();
SET @tomorrow = DATE_ADD(CURDATE(), INTERVAL 1 DAY);
SET @day_after = DATE_ADD(CURDATE(), INTERVAL 2 DAY);

-- ========== 王医生(ID=4) 明天上午固定5个患者 ==========
INSERT INTO `sys_registration` (`patient_id`, `department_id`, `doctor_id`, `appointment_date`, `time_slot`, `registration_type`, `is_return`, `patient_type`, `status`, `priority_score`) VALUES
(1, 2, 4, @tomorrow, 'AM', 1, 0, 0, 1, 100),
(5, 2, 4, @tomorrow, 'AM', 1, 0, 2, 1, 130),
(9, 2, 4, @tomorrow, 'AM', 1, 0, 0, 1, 100),
(13, 2, 4, @tomorrow, 'AM', 1, 0, 4, 1, 120),
(17, 2, 4, @tomorrow, 'AM', 1, 0, 0, 1, 100);

-- 王医生其他时段
INSERT INTO `sys_registration` (`patient_id`, `department_id`, `doctor_id`, `appointment_date`, `time_slot`, `registration_type`, `is_return`, `patient_type`, `status`, `priority_score`) VALUES
(2, 2, 4, @today, 'AM', 1, 0, 0, 1, 100),
(6, 2, 4, @today, 'PM', 1, 0, 2, 1, 130),
(10, 2, 4, @tomorrow, 'PM', 1, 0, 0, 1, 100),
(14, 2, 4, @day_after, 'AM', 2, 0, 4, 1, 1120),
(18, 2, 4, @day_after, 'PM', 1, 0, 0, 1, 100);

-- ========== 内科医生 (ID=1,2,3) ==========
INSERT INTO `sys_registration` (`patient_id`, `department_id`, `doctor_id`, `appointment_date`, `time_slot`, `registration_type`, `is_return`, `patient_type`, `status`, `priority_score`) VALUES
(3, 1, 1, @today, 'AM', 1, 0, 0, 1, 100),
(7, 1, 1, @today, 'AM', 1, 0, 2, 1, 130),
(11, 1, 1, @today, 'PM', 1, 0, 0, 1, 100),
(15, 1, 1, @tomorrow, 'AM', 1, 0, 4, 1, 120),
(19, 1, 1, @tomorrow, 'AM', 2, 0, 0, 1, 1100),
(23, 1, 1, @tomorrow, 'PM', 1, 0, 8, 1, 115),
(27, 1, 1, @day_after, 'AM', 1, 0, 0, 1, 100),
(4, 1, 1, @day_after, 'PM', 1, 0, 0, 1, 100),
(8, 1, 2, @today, 'AM', 1, 0, 0, 1, 100),
(12, 1, 2, @today, 'PM', 1, 0, 2, 1, 130),
(16, 1, 2, @today, 'PM', 1, 0, 0, 1, 100),
(20, 1, 2, @tomorrow, 'AM', 1, 0, 4, 1, 120),
(24, 1, 2, @tomorrow, 'PM', 2, 0, 0, 1, 1100),
(28, 1, 2, @day_after, 'AM', 1, 0, 8, 1, 115),
(1, 1, 2, @day_after, 'PM', 1, 0, 0, 1, 100),
(5, 1, 3, @today, 'AM', 1, 0, 0, 1, 100),
(9, 1, 3, @today, 'PM', 1, 0, 2, 1, 130),
(13, 1, 3, @tomorrow, 'AM', 1, 0, 0, 1, 100),
(17, 1, 3, @tomorrow, 'AM', 1, 0, 4, 1, 120),
(21, 1, 3, @tomorrow, 'PM', 2, 0, 0, 1, 1100),
(25, 1, 3, @day_after, 'AM', 1, 0, 8, 1, 115);

-- ========== 外科医生 (ID=5,6) ==========
INSERT INTO `sys_registration` (`patient_id`, `department_id`, `doctor_id`, `appointment_date`, `time_slot`, `registration_type`, `is_return`, `patient_type`, `status`, `priority_score`) VALUES
(2, 2, 5, @today, 'AM', 1, 0, 0, 1, 100),
(6, 2, 5, @today, 'AM', 1, 0, 2, 1, 130),
(10, 2, 5, @today, 'PM', 1, 0, 0, 1, 100),
(14, 2, 5, @tomorrow, 'AM', 1, 0, 4, 1, 120),
(18, 2, 5, @tomorrow, 'PM', 2, 0, 0, 1, 1100),
(22, 2, 5, @day_after, 'AM', 1, 0, 8, 1, 115),
(26, 2, 5, @day_after, 'PM', 1, 0, 0, 1, 100),
(3, 2, 6, @today, 'AM', 1, 0, 0, 1, 100),
(7, 2, 6, @today, 'PM', 1, 0, 2, 1, 130),
(11, 2, 6, @tomorrow, 'AM', 1, 0, 0, 1, 100),
(15, 2, 6, @tomorrow, 'PM', 1, 0, 4, 1, 120),
(19, 2, 6, @day_after, 'AM', 2, 0, 0, 1, 1100),
(23, 2, 6, @day_after, 'PM', 1, 0, 8, 1, 115);

-- ========== 儿科医生 (ID=7,8,9) ==========
INSERT INTO `sys_registration` (`patient_id`, `department_id`, `doctor_id`, `appointment_date`, `time_slot`, `registration_type`, `is_return`, `patient_type`, `status`, `priority_score`) VALUES
(9, 3, 7, @today, 'AM', 1, 0, 0, 1, 100),
(10, 3, 7, @today, 'AM', 1, 0, 2, 1, 130),
(4, 3, 7, @today, 'PM', 1, 0, 8, 1, 115),
(8, 3, 7, @tomorrow, 'AM', 1, 0, 0, 1, 100),
(12, 3, 7, @tomorrow, 'PM', 2, 0, 4, 1, 1120),
(16, 3, 7, @day_after, 'AM', 1, 0, 0, 1, 100),
(20, 3, 7, @day_after, 'PM', 1, 0, 8, 1, 115),
(9, 3, 8, @today, 'AM', 1, 0, 0, 1, 100),
(10, 3, 8, @today, 'PM', 1, 0, 2, 1, 130),
(5, 3, 8, @tomorrow, 'AM', 1, 0, 0, 1, 100),
(13, 3, 8, @tomorrow, 'PM', 1, 0, 4, 1, 120),
(17, 3, 8, @day_after, 'AM', 2, 0, 8, 1, 1115),
(21, 3, 8, @day_after, 'PM', 1, 0, 0, 1, 100),
(9, 3, 9, @today, 'AM', 1, 0, 0, 1, 100),
(10, 3, 9, @today, 'PM', 1, 0, 2, 1, 130),
(6, 3, 9, @tomorrow, 'AM', 1, 0, 0, 1, 100),
(14, 3, 9, @tomorrow, 'PM', 1, 0, 4, 1, 120),
(18, 3, 9, @day_after, 'AM', 2, 0, 8, 1, 1115);

-- ========== 妇产科医生 (ID=10,11,12) ==========
INSERT INTO `sys_registration` (`patient_id`, `department_id`, `doctor_id`, `appointment_date`, `time_slot`, `registration_type`, `is_return`, `patient_type`, `status`, `priority_score`) VALUES
(3, 4, 10, @today, 'AM', 1, 0, 0, 1, 100),
(4, 4, 10, @today, 'PM', 1, 0, 2, 1, 130),
(16, 4, 10, @tomorrow, 'AM', 1, 0, 0, 1, 100),
(20, 4, 10, @tomorrow, 'PM', 1, 0, 4, 1, 120),
(24, 4, 10, @day_after, 'AM', 2, 0, 0, 1, 1100),
(28, 4, 10, @day_after, 'PM', 1, 0, 8, 1, 115),
(3, 4, 11, @today, 'AM', 1, 0, 0, 1, 100),
(4, 4, 11, @today, 'PM', 1, 0, 2, 1, 130),
(12, 4, 11, @tomorrow, 'AM', 1, 0, 0, 1, 100),
(16, 4, 11, @tomorrow, 'PM', 1, 0, 4, 1, 120),
(20, 4, 11, @day_after, 'AM', 2, 0, 8, 1, 1115),
(3, 4, 12, @today, 'AM', 1, 0, 0, 1, 100),
(4, 4, 12, @today, 'PM', 1, 0, 2, 1, 130),
(18, 4, 12, @tomorrow, 'AM', 1, 0, 0, 1, 100),
(22, 4, 12, @tomorrow, 'PM', 1, 0, 4, 1, 120),
(26, 4, 12, @day_after, 'AM', 1, 0, 8, 1, 115),
(30, 4, 12, @day_after, 'PM', 2, 0, 0, 1, 1100);

-- ========== 急诊科医生 (ID=13,14,15) ==========
INSERT INTO `sys_registration` (`patient_id`, `department_id`, `doctor_id`, `appointment_date`, `time_slot`, `registration_type`, `is_return`, `patient_type`, `status`, `priority_score`) VALUES
(1, 5, 13, @today, 'AM', 1, 0, 0, 1, 100),
(5, 5, 13, @today, 'PM', 1, 0, 2, 1, 130),
(9, 5, 13, @tomorrow, 'AM', 1, 0, 0, 1, 100),
(13, 5, 13, @tomorrow, 'PM', 2, 0, 4, 1, 1120),
(17, 5, 13, @day_after, 'AM', 1, 0, 8, 1, 115),
(21, 5, 13, @day_after, 'PM', 1, 0, 0, 1, 100),
(2, 5, 14, @today, 'AM', 1, 0, 0, 1, 100),
(6, 5, 14, @today, 'PM', 1, 0, 2, 1, 130),
(10, 5, 14, @tomorrow, 'AM', 1, 0, 0, 1, 100),
(14, 5, 14, @tomorrow, 'PM', 1, 0, 4, 1, 120),
(18, 5, 14, @day_after, 'AM', 2, 0, 8, 1, 1115),
(3, 5, 15, @today, 'AM', 1, 0, 0, 1, 100),
(7, 5, 15, @today, 'PM', 1, 0, 2, 1, 130),
(11, 5, 15, @tomorrow, 'AM', 1, 0, 0, 1, 100),
(15, 5, 15, @tomorrow, 'PM', 1, 0, 4, 1, 120),
(19, 5, 15, @day_after, 'AM', 1, 0, 8, 1, 115),
(23, 5, 15, @day_after, 'PM', 2, 0, 0, 1, 1100);

-- ========== 眼科医生 (ID=16,17,18) ==========
INSERT INTO `sys_registration` (`patient_id`, `department_id`, `doctor_id`, `appointment_date`, `time_slot`, `registration_type`, `is_return`, `patient_type`, `status`, `priority_score`) VALUES
(4, 6, 16, @today, 'AM', 1, 0, 0, 1, 100),
(8, 6, 16, @today, 'PM', 1, 0, 2, 1, 130),
(12, 6, 16, @tomorrow, 'AM', 1, 0, 0, 1, 100),
(16, 6, 16, @tomorrow, 'PM', 2, 0, 4, 1, 1120),
(20, 6, 16, @day_after, 'AM', 1, 0, 8, 1, 115),
(24, 6, 16, @day_after, 'PM', 1, 0, 0, 1, 100),
(5, 6, 17, @today, 'AM', 1, 0, 0, 1, 100),
(9, 6, 17, @today, 'PM', 1, 0, 2, 1, 130),
(13, 6, 17, @tomorrow, 'AM', 1, 0, 0, 1, 100),
(17, 6, 17, @tomorrow, 'PM', 1, 0, 4, 1, 120),
(21, 6, 17, @day_after, 'AM', 2, 0, 8, 1, 1115),
(6, 6, 18, @today, 'AM', 1, 0, 0, 1, 100),
(10, 6, 18, @today, 'PM', 1, 0, 2, 1, 130),
(14, 6, 18, @tomorrow, 'AM', 1, 0, 0, 1, 100),
(18, 6, 18, @tomorrow, 'PM', 1, 0, 4, 1, 120),
(22, 6, 18, @day_after, 'AM', 1, 0, 8, 1, 115),
(26, 6, 18, @day_after, 'PM', 2, 0, 0, 1, 1100);

-- ========== 口腔科医生 (ID=19,20,21) ==========
INSERT INTO `sys_registration` (`patient_id`, `department_id`, `doctor_id`, `appointment_date`, `time_slot`, `registration_type`, `is_return`, `patient_type`, `status`, `priority_score`) VALUES
(7, 7, 19, @today, 'AM', 1, 0, 0, 1, 100),
(11, 7, 19, @today, 'PM', 1, 0, 2, 1, 130),
(15, 7, 19, @tomorrow, 'AM', 1, 0, 0, 1, 100),
(19, 7, 19, @tomorrow, 'PM', 2, 0, 4, 1, 1120),
(23, 7, 19, @day_after, 'AM', 1, 0, 8, 1, 115),
(27, 7, 19, @day_after, 'PM', 1, 0, 0, 1, 100),
(8, 7, 20, @today, 'AM', 1, 0, 0, 1, 100),
(12, 7, 20, @today, 'PM', 1, 0, 2, 1, 130),
(16, 7, 20, @tomorrow, 'AM', 1, 0, 0, 1, 100),
(20, 7, 20, @tomorrow, 'PM', 1, 0, 4, 1, 120),
(24, 7, 20, @day_after, 'AM', 2, 0, 8, 1, 1115),
(9, 7, 21, @today, 'AM', 1, 0, 0, 1, 100),
(13, 7, 21, @today, 'PM', 1, 0, 2, 1, 130),
(17, 7, 21, @tomorrow, 'AM', 1, 0, 0, 1, 100),
(21, 7, 21, @tomorrow, 'PM', 1, 0, 4, 1, 120),
(25, 7, 21, @day_after, 'AM', 1, 0, 8, 1, 115),
(29, 7, 21, @day_after, 'PM', 2, 0, 0, 1, 1100);

-- ========== 皮肤科医生 (ID=22,23,24) ==========
INSERT INTO `sys_registration` (`patient_id`, `department_id`, `doctor_id`, `appointment_date`, `time_slot`, `registration_type`, `is_return`, `patient_type`, `status`, `priority_score`) VALUES
(10, 8, 22, @today, 'AM', 1, 0, 0, 1, 100),
(14, 8, 22, @today, 'PM', 1, 0, 2, 1, 130),
(18, 8, 22, @tomorrow, 'AM', 1, 0, 0, 1, 100),
(22, 8, 22, @tomorrow, 'PM', 1, 0, 4, 1, 120),
(26, 8, 22, @day_after, 'AM', 2, 0, 8, 1, 1115),
(30, 8, 22, @day_after, 'PM', 1, 0, 0, 1, 100),
(11, 8, 23, @today, 'AM', 1, 0, 0, 1, 100),
(15, 8, 23, @today, 'PM', 1, 0, 2, 1, 130),
(19, 8, 23, @tomorrow, 'AM', 1, 0, 0, 1, 100),
(23, 8, 23, @tomorrow, 'PM', 2, 0, 4, 1, 1120),
(27, 8, 23, @day_after, 'AM', 1, 0, 8, 1, 115),
(12, 8, 24, @today, 'AM', 1, 0, 0, 1, 100),
(16, 8, 24, @today, 'PM', 1, 0, 2, 1, 130),
(20, 8, 24, @tomorrow, 'AM', 1, 0, 0, 1, 100),
(24, 8, 24, @tomorrow, 'PM', 1, 0, 4, 1, 120),
(28, 8, 24, @day_after, 'AM', 1, 0, 8, 1, 115),
(2, 8, 24, @day_after, 'PM', 2, 0, 0, 1, 1100);
