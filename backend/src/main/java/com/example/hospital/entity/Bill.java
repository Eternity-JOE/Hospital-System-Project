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
    private Integer doctorId;    // 医生ID
    private Integer patientId;   // 病人ID
    private Integer registrationId; // 关联的挂号ID
    private String diagnosis;    // 诊断信息
    private String medicines;    // 药品信息JSON
    private BigDecimal otherCost; // 其他费用
    private BigDecimal totalAmount; // 总金额
    private Integer status;      // 0=待缴费 1=已缴费
    private LocalDateTime createTime; // 创建时间
    private LocalDateTime payTime;    // 缴费时间
}
