package com.example.hospital.controller;

import com.example.hospital.common.Result;
import com.example.hospital.entity.User;
import com.example.hospital.entity.Patient;
import com.example.hospital.mapper.UserMapper;
import com.example.hospital.mapper.PatientMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api")
public class LoginController {

    @Autowired
    private UserMapper userMapper;
    
    @Autowired
    private PatientMapper patientMapper;

    @PostMapping("/login")
    public Result<Map<String, Object>> login(@RequestBody Map<String, String> loginForm) {
        String username = loginForm.get("username");
        String password = loginForm.get("password");
        String role = loginForm.get("role");

        if (username == null || password == null || role == null) {
            return Result.error("参数不完整");
        }

        User user = userMapper.login(username, password, role);
        
        if (user == null) {
            return Result.error("用户名、密码或身份不匹配");
        }

        Map<String, Object> data = new HashMap<>();
        data.put("id", user.getId());
        data.put("username", user.getUsername());
        data.put("role", user.getRole());
        data.put("refId", user.getRefId());
        
        // 如果是患者，返回年龄信息
        if ("patient".equals(user.getRole()) && user.getRefId() != null) {
            Patient patient = patientMapper.selectById(user.getRefId());
            if (patient != null && patient.getAge() != null) {
                data.put("age", patient.getAge());
            }
        }
        
        return Result.success(data);
    }

    @PostMapping("/register")
    public Result<String> register(@RequestBody Map<String, String> registerForm) {
        String username = registerForm.get("username");
        String password = registerForm.get("password");

        if (username == null || password == null || username.isEmpty() || password.isEmpty()) {
            return Result.error("账号和密码不能为空");
        }

        // 检查用户名是否已存在
        User existUser = userMapper.findByUsername(username);
        if (existUser != null) {
            return Result.error("该账号已被注册");
        }

        // 注册新病人账号
        int result = userMapper.registerPatient(username, password);
        if (result > 0) {
            return Result.success("注册成功");
        } else {
            return Result.error("注册失败，请稍后重试");
        }
    }
}
