-- =====================================================
-- 病种管理模块数据库表
-- 作者：Member C
-- 日期：2025-01-XX
-- 说明：病种表和医生-病种关联表
-- =====================================================

-- 1. 病种表
CREATE TABLE IF NOT EXISTS `sys_disease` (
    `id` INT AUTO_INCREMENT PRIMARY KEY COMMENT '病种ID',
    `name` VARCHAR(100) NOT NULL COMMENT '病种名称',
    `code` VARCHAR(50) DEFAULT NULL COMMENT '病种编码',
    `description` VARCHAR(255) DEFAULT NULL COMMENT '病种描述',
    `category` VARCHAR(50) DEFAULT NULL COMMENT '病种分类',
    `department_id` INT DEFAULT NULL COMMENT '所属科室ID',
    `status` TINYINT DEFAULT 1 COMMENT '状态：1-启用，0-停用',
    `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    UNIQUE KEY `uk_name` (`name`),
    INDEX `idx_department` (`department_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='病种表';

-- 2. 医生-病种关联表（多对多关系）
CREATE TABLE IF NOT EXISTS `sys_doctor_disease` (
    `id` INT AUTO_INCREMENT PRIMARY KEY COMMENT 'ID',
    `doctor_id` INT NOT NULL COMMENT '医生ID',
    `disease_id` INT NOT NULL COMMENT '病种ID',
    `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    UNIQUE KEY `uk_doctor_disease` (`doctor_id`, `disease_id`),
    INDEX `idx_doctor` (`doctor_id`),
    INDEX `idx_disease` (`disease_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='医生-病种关联表';

-- =====================================================
-- 初始化测试数据
-- 科室ID对应：1-内科, 2-外科, 3-儿科, 4-妇产科, 5-急诊科, 6-眼科, 7-口腔科, 8-皮肤科
-- =====================================================

-- 内科病种（科室ID=1）
INSERT INTO `sys_disease` (`name`, `code`, `description`, `category`, `department_id`, `status`) VALUES
('高血压', 'NEI001', '原发性高血压、继发性高血压', '心血管', 1, 1),
('糖尿病', 'NEI002', '1型糖尿病、2型糖尿病', '内分泌', 1, 1),
('冠心病', 'NEI003', '冠状动脉粥样硬化性心脏病', '心血管', 1, 1),
('肺炎', 'NEI004', '细菌性肺炎、病毒性肺炎', '呼吸', 1, 1),
('胃炎', 'NEI005', '急性胃炎、慢性胃炎', '消化', 1, 1),
('支气管炎', 'NEI006', '急性支气管炎、慢性支气管炎', '呼吸', 1, 1);

-- 外科病种（科室ID=2）
INSERT INTO `sys_disease` (`name`, `code`, `description`, `category`, `department_id`, `status`) VALUES
('阑尾炎', 'WAI001', '急性阑尾炎、慢性阑尾炎', '普外', 2, 1),
('骨折', 'WAI002', '各种类型骨折', '骨科', 2, 1),
('疝气', 'WAI003', '腹股沟疝、脐疝等', '普外', 2, 1),
('胆囊炎', 'WAI004', '急性胆囊炎、慢性胆囊炎', '肝胆', 2, 1),
('关节炎', 'WAI005', '类风湿性关节炎、骨关节炎', '骨科', 2, 1);

-- 儿科病种（科室ID=3）
INSERT INTO `sys_disease` (`name`, `code`, `description`, `category`, `department_id`, `status`) VALUES
('小儿感冒', 'ER001', '儿童普通感冒、流行性感冒', '呼吸', 3, 1),
('小儿腹泻', 'ER002', '婴幼儿腹泻病', '消化', 3, 1),
('小儿肺炎', 'ER003', '儿童支气管肺炎', '呼吸', 3, 1),
('手足口病', 'ER004', '肠道病毒感染', '传染', 3, 1),
('小儿发热', 'ER005', '儿童发热待查', '综合', 3, 1);

-- 妇产科病种（科室ID=4）
INSERT INTO `sys_disease` (`name`, `code`, `description`, `category`, `department_id`, `status`) VALUES
('月经不调', 'FU001', '月经周期紊乱', '妇科', 4, 1),
('妇科炎症', 'FU002', '阴道炎、宫颈炎等', '妇科', 4, 1),
('子宫肌瘤', 'FU003', '子宫平滑肌瘤', '妇科', 4, 1),
('产前检查', 'FU004', '孕期常规检查', '产科', 4, 1),
('不孕症', 'FU005', '女性不孕症', '生殖', 4, 1);

-- 急诊科病种（科室ID=5）
INSERT INTO `sys_disease` (`name`, `code`, `description`, `category`, `department_id`, `status`) VALUES
('急性腹痛', 'JI001', '急性腹部疼痛待查', '急诊', 5, 1),
('外伤', 'JI002', '各类外伤处理', '急诊', 5, 1),
('中毒', 'JI003', '食物中毒、药物中毒等', '急诊', 5, 1),
('心梗', 'JI004', '急性心肌梗死', '急诊', 5, 1),
('脑卒中', 'JI005', '急性脑血管意外', '急诊', 5, 1);

-- 眼科病种（科室ID=6）
INSERT INTO `sys_disease` (`name`, `code`, `description`, `category`, `department_id`, `status`) VALUES
('近视', 'YAN001', '屈光不正-近视', '屈光', 6, 1),
('白内障', 'YAN002', '晶状体混浊', '眼病', 6, 1),
('青光眼', 'YAN003', '眼压升高性眼病', '眼病', 6, 1),
('结膜炎', 'YAN004', '急性结膜炎、慢性结膜炎', '眼表', 6, 1),
('干眼症', 'YAN005', '泪液分泌不足', '眼表', 6, 1);

-- 口腔科病种（科室ID=7）
INSERT INTO `sys_disease` (`name`, `code`, `description`, `category`, `department_id`, `status`) VALUES
('龋齿', 'KOU001', '蛀牙、虫牙', '牙体', 7, 1),
('牙周炎', 'KOU002', '牙龈炎、牙周病', '牙周', 7, 1),
('智齿', 'KOU003', '智齿冠周炎、阻生智齿', '口外', 7, 1),
('口腔溃疡', 'KOU004', '复发性口腔溃疡', '黏膜', 7, 1),
('牙齿矫正', 'KOU005', '牙列不齐矫治', '正畸', 7, 1);

-- 皮肤科病种（科室ID=8）
INSERT INTO `sys_disease` (`name`, `code`, `description`, `category`, `department_id`, `status`) VALUES
('湿疹', 'PI001', '急性湿疹、慢性湿疹', '皮炎', 8, 1),
('荨麻疹', 'PI002', '急性荨麻疹、慢性荨麻疹', '过敏', 8, 1),
('痤疮', 'PI003', '青春痘、粉刺', '皮脂', 8, 1),
('皮肤过敏', 'PI004', '接触性皮炎、药物过敏', '过敏', 8, 1),
('带状疱疹', 'PI005', '水痘-带状疱疹病毒感染', '病毒', 8, 1);

-- =====================================================
-- 初始化医生-病种关联数据
-- 要求：每个科室的所有病种都被至少两个医生关联
-- 医生ID对应：
-- 内科(1-3): 1-张建国, 2-李秀英, 3-王明华
-- 外科(4-6): 4-王医生, 5-赵志强, 6-孙丽娟
-- 儿科(7-9): 7-陈小明, 8-刘芳芳, 9-杨晓燕
-- 妇产科(10-12): 10-周美玲, 11-吴静怡, 12-郑雅琴
-- 急诊科(13-15): 13-黄伟强, 14-林志远, 15-徐海涛
-- 眼科(16-18): 16-马光明, 17-胡晓红, 18-高志华
-- 口腔科(19-21): 19-罗建华, 20-谢丽萍, 21-唐志刚
-- 皮肤科(22-24): 22-曹美华, 23-邓晓峰, 24-冯雪梅
-- =====================================================

-- 内科医生关联病种 (病种ID: 1-高血压, 2-糖尿病, 3-冠心病, 4-肺炎, 5-胃炎, 6-支气管炎)
INSERT INTO `sys_doctor_disease` (`doctor_id`, `disease_id`) VALUES
(1, 1), (1, 3), (1, 2),    -- 张建国: 高血压、冠心病、糖尿病
(2, 4), (2, 6), (2, 1),    -- 李秀英: 肺炎、支气管炎、高血压
(3, 2), (3, 5), (3, 3);    -- 王明华: 糖尿病、胃炎、冠心病

-- 外科医生关联病种 (病种ID: 7-阑尾炎, 8-骨折, 9-疝气, 10-胆囊炎, 11-关节炎)
INSERT INTO `sys_doctor_disease` (`doctor_id`, `disease_id`) VALUES
(4, 8), (4, 11),           -- 王医生: 骨折、关节炎
(5, 7), (5, 9), (5, 10),   -- 赵志强: 阑尾炎、疝气、胆囊炎
(6, 10), (6, 7), (6, 9);   -- 孙丽娟: 胆囊炎、阑尾炎、疝气

-- 儿科医生关联病种 (病种ID: 12-小儿感冒, 13-小儿腹泻, 14-小儿肺炎, 15-手足口病, 16-小儿发热)
INSERT INTO `sys_doctor_disease` (`doctor_id`, `disease_id`) VALUES
(7, 14), (7, 12), (7, 16), -- 陈小明: 小儿肺炎、小儿感冒、小儿发热
(8, 13), (8, 15), (8, 12), -- 刘芳芳: 小儿腹泻、手足口病、小儿感冒
(9, 12), (9, 16), (9, 14); -- 杨晓燕: 小儿感冒、小儿发热、小儿肺炎

-- 妇产科医生关联病种 (病种ID: 17-月经不调, 18-妇科炎症, 19-子宫肌瘤, 20-产前检查, 21-不孕症)
INSERT INTO `sys_doctor_disease` (`doctor_id`, `disease_id`) VALUES
(10, 20), (10, 19), (10, 21), -- 周美玲: 产前检查、子宫肌瘤、不孕症
(11, 19), (11, 17), (11, 21), -- 吴静怡: 子宫肌瘤、月经不调、不孕症
(12, 18), (12, 17), (12, 20); -- 郑雅琴: 妇科炎症、月经不调、产前检查

-- 急诊科医生关联病种 (病种ID: 22-急性腹痛, 23-外伤, 24-中毒, 25-心梗, 26-脑卒中)
INSERT INTO `sys_doctor_disease` (`doctor_id`, `disease_id`) VALUES
(13, 25), (13, 26), (13, 22), -- 黄伟强: 心梗、脑卒中、急性腹痛
(14, 23), (14, 24), (14, 22), -- 林志远: 外伤、中毒、急性腹痛
(15, 22), (15, 23), (15, 25); -- 徐海涛: 急性腹痛、外伤、心梗

-- 眼科医生关联病种 (病种ID: 27-近视, 28-白内障, 29-青光眼, 30-结膜炎, 31-干眼症)
INSERT INTO `sys_doctor_disease` (`doctor_id`, `disease_id`) VALUES
(16, 28), (16, 29), (16, 27), -- 马光明: 白内障、青光眼、近视
(17, 29), (17, 30), (17, 31), -- 胡晓红: 青光眼、结膜炎、干眼症
(18, 27), (18, 30), (18, 28); -- 高志华: 近视、结膜炎、白内障

-- 口腔科医生关联病种 (病种ID: 32-龋齿, 33-牙周炎, 34-智齿, 35-口腔溃疡, 36-牙齿矫正)
INSERT INTO `sys_doctor_disease` (`doctor_id`, `disease_id`) VALUES
(19, 34), (19, 32), (19, 36), -- 罗建华: 智齿、龋齿、牙齿矫正
(20, 33), (20, 35), (20, 32), -- 谢丽萍: 牙周炎、口腔溃疡、龋齿
(21, 32), (21, 36), (21, 33); -- 唐志刚: 龋齿、牙齿矫正、牙周炎

-- 皮肤科医生关联病种 (病种ID: 37-湿疹, 38-荨麻疹, 39-痤疮, 40-皮肤过敏, 41-带状疱疹)
INSERT INTO `sys_doctor_disease` (`doctor_id`, `disease_id`) VALUES
(22, 41), (22, 37), (22, 38), -- 曹美华: 带状疱疹、湿疹、荨麻疹
(23, 38), (23, 40), (23, 37), -- 邓晓峰: 荨麻疹、皮肤过敏、湿疹
(24, 39), (24, 40), (24, 41); -- 冯雪梅: 痤疮、皮肤过敏、带状疱疹
