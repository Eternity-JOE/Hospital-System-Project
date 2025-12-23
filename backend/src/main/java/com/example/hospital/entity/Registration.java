package com.example.hospital.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 挂号记录实体类
 */
@Data
@TableName("sys_registration")
public class Registration {
    @TableId(type = IdType.AUTO)
    private Integer id;
    
    /** 患者ID */
    private Integer patientId;
    
    /** 科室ID */
    private Integer departmentId;
    
    /** 医生ID */
    private Integer doctorId;
    
    /** 预约日期 */
    private LocalDate appointmentDate;
    
    /** 时段：AM-上午，PM-下午 */
    private String timeSlot;
    
    /** 挂号类型：1-普通，2-急诊 */
    private Integer registrationType;
    
    /** 是否复诊：0-初诊，1-复诊 */
    private Integer isReturn;
    
    /** 患者类型：0-普通，1-军人，2-老人(>=65岁)，3-儿童(<=14岁) */
    private Integer patientType;
    
    /** 状态：0-已取消，1-待就诊，2-已完成，3-爽约 */
    private Integer status;
    
    /** 计算后的优先级分数（越高越优先） */
    private Integer priorityScore;
    
    /** 排队序号 */
    private Integer queueNumber;
    
    /** 创建时间（挂号时间） */
    private LocalDateTime createTime;
    
    /** 更新时间 */
    private LocalDateTime updateTime;
    
    // ========== 非数据库字段，用于前端展示 ==========
    @TableField(exist = false)
    private String patientName;
    
    @TableField(exist = false)
    private String departmentName;
    
    @TableField(exist = false)
    private String doctorName;
    
    @TableField(exist = false)
    private Integer patientAge;
}
