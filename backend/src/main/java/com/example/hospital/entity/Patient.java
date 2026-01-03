package com.example.hospital.entity;
import com.baomidou.mybatisplus.annotation.IdType; // 如果报红，检查pom是否引入mybatis-plus
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

@Data
@TableName("sys_patient")
public class Patient {
    @TableId(type = IdType.AUTO)
    private Integer id;
    private String name;
    private String gender;
    private Integer age;
    private String phone;
    private String address;
}