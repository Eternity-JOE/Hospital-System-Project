package com.example.hospital.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

/**
 * 患者信誉记录（用于记录爽约次数）
 */
@Data
@TableName("sys_patient_record")
public class PatientRecord {
    @TableId(type = IdType.AUTO)
    private Integer id;
    
    /** 患者ID */
    private Integer patientId;
    
    /** 爽约次数 */
    private Integer noShowCount;
    
    /** 总挂号次数 */
    private Integer totalCount;
    
    /** 完成就诊次数 */
    private Integer completedCount;
}
