-- =====================================================
-- 医生排班数据初始化
-- 日期：2026-01-11
-- 说明：初始化医生排班数据
-- 格式：1,2,3,4,5,6,7 表示周一到周日
-- 每个医生休息2天，尽量不连续
-- 同科室医生错开休息，保证每天每个病种都有人
-- =====================================================

-- 内科 (department_id = 1) - 医生ID: 1,2,3
-- 张建国(1): 休周二、周五
-- 李秀英(2): 休周四、周日
-- 王明华(3): 休周一、周六
UPDATE `sys_doctor` SET `schedule` = '1,3,4,6,7' WHERE `id` = 1;
UPDATE `sys_doctor` SET `schedule` = '1,2,3,5,6' WHERE `id` = 2;
UPDATE `sys_doctor` SET `schedule` = '2,3,4,5,7' WHERE `id` = 3;

-- 外科 (department_id = 2) - 医生ID: 4,5,6
-- 王医生(4): 特殊处理，由后端动态计算（保证今明后三天不休息）
-- 赵志强(5): 休周一、周四
-- 孙丽娟(6): 休周三、周日
UPDATE `sys_doctor` SET `schedule` = 'DYNAMIC' WHERE `id` = 4;
UPDATE `sys_doctor` SET `schedule` = '2,3,5,6,7' WHERE `id` = 5;
UPDATE `sys_doctor` SET `schedule` = '1,2,4,5,6' WHERE `id` = 6;

-- 儿科 (department_id = 3) - 医生ID: 7,8,9
-- 陈小明(7): 休周二、周五
-- 刘芳芳(8): 休周四、周日
-- 杨晓燕(9): 休周一、周六
UPDATE `sys_doctor` SET `schedule` = '1,3,4,6,7' WHERE `id` = 7;
UPDATE `sys_doctor` SET `schedule` = '1,2,3,5,6' WHERE `id` = 8;
UPDATE `sys_doctor` SET `schedule` = '2,3,4,5,7' WHERE `id` = 9;

-- 妇产科 (department_id = 4) - 医生ID: 10,11,12
-- 周美玲(10): 休周二、周五
-- 吴静怡(11): 休周四、周日
-- 郑雅琴(12): 休周一、周六
UPDATE `sys_doctor` SET `schedule` = '1,3,4,6,7' WHERE `id` = 10;
UPDATE `sys_doctor` SET `schedule` = '1,2,3,5,6' WHERE `id` = 11;
UPDATE `sys_doctor` SET `schedule` = '2,3,4,5,7' WHERE `id` = 12;

-- 急诊科 (department_id = 5) - 医生ID: 13,14,15
-- 黄伟强(13): 休周二、周五
-- 林志远(14): 休周四、周日
-- 徐海涛(15): 休周一、周六
UPDATE `sys_doctor` SET `schedule` = '1,3,4,6,7' WHERE `id` = 13;
UPDATE `sys_doctor` SET `schedule` = '1,2,3,5,6' WHERE `id` = 14;
UPDATE `sys_doctor` SET `schedule` = '2,3,4,5,7' WHERE `id` = 15;

-- 眼科 (department_id = 6) - 医生ID: 16,17,18
-- 马光明(16): 休周二、周五
-- 胡晓红(17): 休周四、周日
-- 高志华(18): 休周一、周六
UPDATE `sys_doctor` SET `schedule` = '1,3,4,6,7' WHERE `id` = 16;
UPDATE `sys_doctor` SET `schedule` = '1,2,3,5,6' WHERE `id` = 17;
UPDATE `sys_doctor` SET `schedule` = '2,3,4,5,7' WHERE `id` = 18;

-- 口腔科 (department_id = 7) - 医生ID: 19,20,21
-- 罗建华(19): 休周二、周五
-- 谢丽萍(20): 休周四、周日
-- 唐志刚(21): 休周一、周六
UPDATE `sys_doctor` SET `schedule` = '1,3,4,6,7' WHERE `id` = 19;
UPDATE `sys_doctor` SET `schedule` = '1,2,3,5,6' WHERE `id` = 20;
UPDATE `sys_doctor` SET `schedule` = '2,3,4,5,7' WHERE `id` = 21;

-- 皮肤科 (department_id = 8) - 医生ID: 22,23,24
-- 曹美华(22): 休周二、周五
-- 邓晓峰(23): 休周四、周日
-- 冯雪梅(24): 休周一、周六
UPDATE `sys_doctor` SET `schedule` = '1,3,4,6,7' WHERE `id` = 22;
UPDATE `sys_doctor` SET `schedule` = '1,2,3,5,6' WHERE `id` = 23;
UPDATE `sys_doctor` SET `schedule` = '2,3,4,5,7' WHERE `id` = 24;

