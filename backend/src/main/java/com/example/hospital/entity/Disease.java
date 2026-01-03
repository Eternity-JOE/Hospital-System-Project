package com.example.hospital.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

@Data
@TableName("sys_disease")
public class Disease {
    @TableId(type = IdType.AUTO)
    private Integer id;
    
    private String name;
    
    private String code;
    
    private String description;
    
    private String category;
    
    private Integer status;
}

