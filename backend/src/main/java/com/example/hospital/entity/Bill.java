package com.example.hospital.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;  // <--- 确保这行导入了
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data // <--- 关键！如果没有这个，所有的 set 方法都会报错
@TableName("bill")
public class Bill {
    @TableId(type = IdType.AUTO)
    private Integer id;
    private Integer patientId;
    private BigDecimal totalAmount;
    private Integer status;      // 对应 setStatus
    private LocalDateTime createTime; // 对应 setCreateTime
    private LocalDateTime payTime;    // 对应 setPayTime
}