package com.example.hospital.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.util.List;

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
    
    /** 排班时间，格式：周一上午,周二下午,周三全天 */
    private String schedule;
    
    private Integer status;
    
    /** 科室名称（非数据库字段） */
    @TableField(exist = false)
    private String departmentName;
    
    /** 关联的病种ID列表（非数据库字段，用于前端提交） */
    @TableField(exist = false)
    private List<Integer> diseaseIds;
    
    /** 关联的病种列表（非数据库字段，用于前端展示） */
    @TableField(exist = false)
    private List<Disease> diseases;
}
