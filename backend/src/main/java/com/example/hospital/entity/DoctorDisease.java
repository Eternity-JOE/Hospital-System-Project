package com.example.hospital.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

@Data
@TableName("sys_doctor_disease")
public class DoctorDisease {
    @TableId(type = IdType.AUTO)
    private Integer id;
    
    private Integer doctorId;
    
    private Integer diseaseId;
}

