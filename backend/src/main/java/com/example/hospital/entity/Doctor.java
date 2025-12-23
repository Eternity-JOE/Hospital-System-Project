package com.example.hospital.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

@Data
@TableName("sys_doctor")
public class Doctor {
    @TableId(type = IdType.AUTO)
    private Integer id;
    
    private String name;
    
    private Integer gender;
    
    private String title;
    
    private Integer departmentId;
    
    private String phone;
    
    private String specialty;
    
    private Integer status;
    
    /** 科室名称（非数据库字段） */
    @TableField(exist = false)
    private String departmentName;
}
