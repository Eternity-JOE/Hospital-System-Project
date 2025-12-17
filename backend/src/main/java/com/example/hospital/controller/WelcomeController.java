package com.example.hospital.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class WelcomeController {

    // 监听根路径 "/"
    @GetMapping("/")
    public String welcome() {
        return "✅ 医院后端服务器正在运行中！请访问 /patient/list 查看数据。";
    }
}