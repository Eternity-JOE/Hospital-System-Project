package com.example.hospital.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

/**
 * 科室实体类
 */
@Data
@TableName("sys_department")
public class Department {
    @TableId(type = IdType.AUTO)
    private Integer id;
    
    /** 科室名称 */
    private String name;
    
    /** 科室编码 */
    private String code;
    
    /** 科室描述 */
    private String description;
    
    /** 科室位置 */
    private String location;
    
    /** 状态：1-启用，0-停用 */
    private Integer status;
}
