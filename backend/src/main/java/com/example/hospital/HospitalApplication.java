package com.example.hospital;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
// ⚠️ 关键点：告诉 MyBatis 你的 Mapper 接口在哪
// 如果你的 mapper 放在 com.example.hospital.mapper 包下，就写这个路径
@MapperScan("com.example.hospital.mapper")
public class HospitalApplication {

    public static void main(String[] args) {
        SpringApplication.run(HospitalApplication.class, args);
        System.out.println("✅ 医院管理系统后端启动成功！访问地址: http://localhost:8080");
    }
}