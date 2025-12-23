package com.example.hospital.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.example.hospital.common.Result;
import com.example.hospital.entity.Doctor;
import com.example.hospital.mapper.DoctorMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/doctor")
public class DoctorController {

    @Autowired
    private DoctorMapper doctorMapper;

    // 查询所有医生
    @GetMapping("/list")
    public Result<?> list() {
        QueryWrapper<Doctor> wrapper = new QueryWrapper<>();
        wrapper.eq("status", 1); // 只查在职医生
        List<Doctor> list = doctorMapper.selectList(wrapper);
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

    // 新增医生
    @PostMapping
    public Result<?> save(@RequestBody Doctor doctor) {
        doctorMapper.insert(doctor);
        return Result.success(null);
    }

    // 修改医生
    @PutMapping
    public Result<?> update(@RequestBody Doctor doctor) {
        doctorMapper.updateById(doctor);
        return Result.success(null);
    }

    // 删除医生
    @DeleteMapping("/{id}")
    public Result<?> delete(@PathVariable Integer id) {
        doctorMapper.deleteById(id);
        return Result.success(null);
    }
}
