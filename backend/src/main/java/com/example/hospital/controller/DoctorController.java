package com.example.hospital.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.example.hospital.common.Result;
import com.example.hospital.entity.Disease;
import com.example.hospital.entity.Doctor;
import com.example.hospital.entity.DoctorDisease;
import com.example.hospital.entity.User;
import com.example.hospital.mapper.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/doctor")
public class DoctorController {

    @Autowired
    private DoctorMapper doctorMapper;
    
    @Autowired
    private DoctorDiseaseMapper doctorDiseaseMapper;
    
    @Autowired
    private DiseaseMapper diseaseMapper;
    
    @Autowired
    private UserMapper userMapper;

    // 查询所有医生（管理页面用，包含所有状态的医生）
    @GetMapping("/list")
    public Result<?> list() {
        List<Doctor> list = doctorMapper.selectList(null);
        // 为每个医生加载关联的病种ID列表
        for (Doctor doctor : list) {
            List<Integer> diseaseIds = doctorDiseaseMapper.selectDiseaseIdsByDoctorId(doctor.getId());
            doctor.setDiseaseIds(diseaseIds);
        }
        return Result.success(list);
    }

    // 根据科室查询医生
    @GetMapping("/listByDepartment")
    public Result<?> listByDepartment(@RequestParam Integer departmentId) {
        QueryWrapper<Doctor> wrapper = new QueryWrapper<>();
        wrapper.eq("department_id", departmentId).eq("status", 1);
        List<Doctor> list = doctorMapper.selectList(wrapper);
        return Result.success(list);
    }

    // 新增医生（包含病种关联和登录账号）
    @PostMapping
    @Transactional
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
        
        // 1. 保存医生基本信息
        Doctor doctor = new Doctor();
        doctor.setName((String) data.get("name"));
        if (data.get("gender") != null) {
            doctor.setGender(Integer.valueOf(data.get("gender").toString()));
        }
        doctor.setTitle((String) data.get("title"));
        if (data.get("departmentId") != null) {
            doctor.setDepartmentId(Integer.valueOf(data.get("departmentId").toString()));
        }
        doctor.setPhone((String) data.get("phone"));
        doctor.setSpecialty((String) data.get("specialty"));
        doctor.setSchedule((String) data.get("schedule"));
        if (data.get("status") != null) {
            doctor.setStatus(Integer.valueOf(data.get("status").toString()));
        }
        doctorMapper.insert(doctor);
        
        // 2. 保存病种关联关系
        @SuppressWarnings("unchecked")
        List<Integer> diseaseIds = (List<Integer>) data.get("diseaseIds");
        if (diseaseIds != null && !diseaseIds.isEmpty()) {
            for (Integer diseaseId : diseaseIds) {
                DoctorDisease doctorDisease = new DoctorDisease();
                doctorDisease.setDoctorId(doctor.getId());
                doctorDisease.setDiseaseId(diseaseId);
                doctorDiseaseMapper.insert(doctorDisease);
            }
        }
        
        // 3. 创建登录账号
        if (username != null && !username.isEmpty() && password != null && !password.isEmpty()) {
            userMapper.insertUser(username, password, "doctor", doctor.getId());
        }
        
        return Result.success(null);
    }

    // 修改医生（包含病种关联）
    @PutMapping
    @Transactional
    public Result<?> update(@RequestBody Doctor doctor) {
        // 1. 更新医生基本信息
        doctorMapper.updateById(doctor);
        
        // 2. 删除旧的病种关联关系
        doctorDiseaseMapper.deleteByDoctorId(doctor.getId());
        
        // 3. 保存新的病种关联关系
        if (doctor.getDiseaseIds() != null && !doctor.getDiseaseIds().isEmpty()) {
            for (Integer diseaseId : doctor.getDiseaseIds()) {
                DoctorDisease doctorDisease = new DoctorDisease();
                doctorDisease.setDoctorId(doctor.getId());
                doctorDisease.setDiseaseId(diseaseId);
                doctorDiseaseMapper.insert(doctorDisease);
            }
        }
        
        return Result.success(null);
    }

    // 删除医生（同时删除病种关联）
    @DeleteMapping("/{id}")
    @Transactional
    public Result<?> delete(@PathVariable Integer id) {
        // 1. 删除病种关联关系
        doctorDiseaseMapper.deleteByDoctorId(id);
        
        // 2. 删除医生
        doctorMapper.deleteById(id);
        
        return Result.success(null);
    }
    
    // 获取医生个人信息（医生端用）
    @GetMapping("/profile")
    public Result<?> getProfile(@RequestParam Integer userId) {
        User user = userMapper.findById(userId);
        if (user == null || user.getRefId() == null) {
            return Result.success(null);
        }
        Doctor doctor = doctorMapper.selectById(user.getRefId());
        if (doctor != null) {
            List<Integer> diseaseIds = doctorDiseaseMapper.selectDiseaseIdsByDoctorId(doctor.getId());
            doctor.setDiseaseIds(diseaseIds);
        }
        return Result.success(doctor);
    }
}
