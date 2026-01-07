package com.example.hospital.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.hospital.common.Result;
import com.example.hospital.entity.Patient;
import com.example.hospital.entity.User;
import com.example.hospital.mapper.PatientMapper;
import com.example.hospital.mapper.UserMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/patient")
public class PatientController {

    @Autowired
    private PatientMapper patientMapper;

    @Autowired
    private UserMapper userMapper;

    // 1. 查询所有
    @GetMapping("/list")
    public Result<?> list() {
        List<Patient> list = patientMapper.selectList(null);
        return Result.success(list);
    }

    // 2. 新增（同时创建登录账号）
    @PostMapping
    public Result<?> save(@RequestBody Map<String, Object> data) {
        String username = (String) data.get("username");
        String password = (String) data.get("password");
        
        // 检查用户名是否已存在
        if (username != null && !username.isEmpty()) {
            User existUser = userMapper.findByUsername(username);
            if (existUser != null) {
                return Result.error("该账号已被使用");
            }
        }
        
        // 创建病人信息
        Patient patient = new Patient();
        patient.setName((String) data.get("name"));
        patient.setGender((String) data.get("gender"));
        if (data.get("age") != null) {
            patient.setAge(Integer.valueOf(data.get("age").toString()));
        }
        patient.setPhone((String) data.get("phone"));
        patient.setAddress((String) data.get("address"));
        patientMapper.insert(patient);
        
        // 创建登录账号
        if (username != null && !username.isEmpty() && password != null && !password.isEmpty()) {
            userMapper.insertUser(username, password, "patient", patient.getId());
        }
        
        return Result.success(null);
    }

    // 3. 修改
    @PutMapping
    public Result<?> update(@RequestBody Patient patient) {
        patientMapper.updateById(patient);
        return Result.success(null);
    }

    // 4. 删除
    @DeleteMapping("/{id}")
    public Result<?> delete(@PathVariable Integer id) {
        patientMapper.deleteById(id);
        return Result.success(null);
    }
}

// 病人端个人信息接口
@RestController
@RequestMapping("/api/patient")
class PatientProfileController {

    @Autowired
    private PatientMapper patientMapper;

    @Autowired
    private UserMapper userMapper;

    // 获取当前病人的个人信息
    @GetMapping("/profile")
    public Result<?> getProfile(@RequestParam Integer userId) {
        User user = userMapper.findById(userId);
        if (user == null || user.getRefId() == null) {
            return Result.success(null);
        }
        Patient patient = patientMapper.selectById(user.getRefId());
        return Result.success(patient);
    }

    // 保存病人个人信息
    @PostMapping("/profile")
    public Result<?> saveProfile(@RequestBody Map<String, Object> data) {
        Integer userId = Integer.valueOf(data.get("userId").toString());
        
        User user = userMapper.findById(userId);
        if (user == null) {
            return Result.error("用户不存在");
        }

        Patient patient = new Patient();
        patient.setName((String) data.get("name"));
        patient.setGender((String) data.get("gender"));
        patient.setAge(Integer.valueOf(data.get("age").toString()));
        patient.setPhone((String) data.get("phone"));
        patient.setAddress((String) data.get("address"));

        if (user.getRefId() != null) {
            // 已有记录，更新
            patient.setId(user.getRefId());
            patientMapper.updateById(patient);
        } else {
            // 新建记录
            patientMapper.insert(patient);
            // 更新 user 表的 ref_id
            userMapper.updateRefId(userId, patient.getId());
        }

        return Result.success(null);
    }
}