package com.example.hospital.service;

import com.example.hospital.entity.PatientRecord;
import com.example.hospital.entity.Registration;
import com.example.hospital.mapper.PatientRecordMapper;
import com.example.hospital.mapper.RegistrationMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalDateTime;

/**
 * 优先级计算器
 * 
 * 借鉴操作系统多级反馈队列(MLFQ)思想，实现智能排队调度
 * 
 * 优先级规则（分数越高越优先）：
 * 1. 急诊：+1000分（最高优先级）
 * 2. 特殊人群：军人+200，老人(>=65岁)+150，儿童(<=14岁)+150
 * 3. 复诊：同一医生复诊+100分
 * 4. 等待时间补偿：每等待1小时+10分（防止饥饿）
 * 5. 爽约惩罚：每次爽约-50分（信用机制）
 */
@Service
public class PriorityCalculator {
    
    // ========== 优先级权重常量 ==========
    /** 急诊基础分 */
    private static final int EMERGENCY_SCORE = 1000;
    /** 军人加分 */
    private static final int MILITARY_SCORE = 200;
    /** 老人加分 */
    private static final int ELDERLY_SCORE = 150;
    /** 儿童加分 */
    private static final int CHILD_SCORE = 150;
    /** 复诊加分 */
    private static final int RETURN_VISIT_SCORE = 100;
    /** 每小时等待加分 */
    private static final int WAIT_HOUR_SCORE = 10;
    /** 每次爽约扣分 */
    private static final int NO_SHOW_PENALTY = 50;
    /** 普通挂号基础分 */
    private static final int BASE_SCORE = 100;
    
    @Autowired
    private PatientRecordMapper patientRecordMapper;
    
    @Autowired
    private RegistrationMapper registrationMapper;
    
    /**
     * 计算挂号的优先级分数
     * 
     * @param registration 挂号信息
     * @param patientAge 患者年龄
     * @return 优先级分数
     */
    public int calculatePriority(Registration registration, Integer patientAge) {
        int score = BASE_SCORE;
        
        // 1. 急诊最高优先级
        if (registration.getRegistrationType() != null && registration.getRegistrationType() == 2) {
            score += EMERGENCY_SCORE;
        }
        
        // 2. 特殊人群加分
        if (registration.getPatientType() != null) {
            switch (registration.getPatientType()) {
                case 1: // 军人
                    score += MILITARY_SCORE;
                    break;
                case 2: // 老人
                    score += ELDERLY_SCORE;
                    break;
                case 3: // 儿童
                    score += CHILD_SCORE;
                    break;
            }
        }
        // 根据年龄自动判断老人/儿童（如果没有手动设置）
        if (patientAge != null && (registration.getPatientType() == null || registration.getPatientType() == 0)) {
            if (patientAge >= 65) {
                score += ELDERLY_SCORE;
            } else if (patientAge <= 14) {
                score += CHILD_SCORE;
            }
        }
        
        // 3. 复诊加分
        if (registration.getIsReturn() != null && registration.getIsReturn() == 1) {
            score += RETURN_VISIT_SCORE;
        } else if (registration.getDoctorId() != null && registration.getPatientId() != null) {
            // 自动检测是否为复诊
            int completedCount = registrationMapper.countCompletedByPatientAndDoctor(
                    registration.getPatientId(), registration.getDoctorId());
            if (completedCount > 0) {
                score += RETURN_VISIT_SCORE;
                registration.setIsReturn(1);
            }
        }
        
        // 4. 爽约惩罚
        if (registration.getPatientId() != null) {
            PatientRecord record = patientRecordMapper.selectByPatientId(registration.getPatientId());
            if (record != null && record.getNoShowCount() != null && record.getNoShowCount() > 0) {
                score -= record.getNoShowCount() * NO_SHOW_PENALTY;
            }
        }
        
        return Math.max(score, 0); // 分数不能为负
    }
    
    /**
     * 动态更新等待时间加分（可定时调用）
     * 
     * @param registration 挂号信息
     * @return 更新后的优先级分数
     */
    public int updateWaitingBonus(Registration registration) {
        if (registration.getCreateTime() == null) {
            return registration.getPriorityScore() != null ? registration.getPriorityScore() : BASE_SCORE;
        }
        
        // 计算等待时长（小时）
        long waitingHours = Duration.between(registration.getCreateTime(), LocalDateTime.now()).toHours();
        int waitingBonus = (int) (waitingHours * WAIT_HOUR_SCORE);
        
        int baseScore = registration.getPriorityScore() != null ? registration.getPriorityScore() : BASE_SCORE;
        return baseScore + waitingBonus;
    }
}
