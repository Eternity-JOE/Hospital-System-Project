package com.example.hospital.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import java.math.BigDecimal;

@Data
@TableName("medicine")
public class Medicine {
    @TableId(type = IdType.AUTO)
    private Integer id;
    private String name;
    private String type;
    private BigDecimal price; // 价格建议使用 BigDecimal 保证精度
    private Integer stock;
    private String unit;
    private Integer status; // 1-启用, 0-禁用
}