package com.example.hospital.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.example.hospital.common.Result;
import com.example.hospital.entity.Disease;
import com.example.hospital.entity.Doctor;
import com.example.hospital.entity.DoctorDisease;
import com.example.hospital.mapper.DiseaseMapper;
import com.example.hospital.mapper.DoctorDiseaseMapper;
import com.example.hospital.mapper.DoctorMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/doctor")
public class DoctorController {

    @Autowired
    private DoctorMapper doctorMapper;
    
    @Autowired
    private DoctorDiseaseMapper doctorDiseaseMapper;
    
    @Autowired
    private DiseaseMapper diseaseMapper;

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

    // 新增医生（包含病种关联）
    @PostMapping
    @Transactional
    public Result<?> save(@RequestBody Doctor doctor) {
        // 1. 保存医生基本信息
        doctorMapper.insert(doctor);
        
        // 2. 保存病种关联关系
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
}
