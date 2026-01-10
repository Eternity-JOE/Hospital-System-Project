package com.example.hospital.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@TableName("bill")
public class Bill {
    @TableId(type = IdType.AUTO)
    private Integer id;          // 账单流水号
    private Integer doctorId;    // 医生ID，和数据库doctor_id字段映射，mybatis-plus自动驼峰转下划线
    private Integer patientId;   // 病人ID
    private BigDecimal totalAmount; // 总金额
    private Integer status;      // 0=待缴费 1=已缴费
    private LocalDateTime createTime; // 创建时间
    private LocalDateTime payTime;    // 缴费时间
}
