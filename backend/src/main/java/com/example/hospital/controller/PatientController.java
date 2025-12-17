package com.example.hospital.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.hospital.common.Result;
import com.example.hospital.entity.Patient;
import com.example.hospital.mapper.PatientMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/patient")
public class PatientController {

    @Autowired
    private PatientMapper patientMapper;

    // 1. 查询所有
    @GetMapping("/list")
    public Result<?> list() {
        List<Patient> list = patientMapper.selectList(null);
        return Result.success(list);
    }

    // 2. 新增
    @PostMapping
    public Result<?> save(@RequestBody Patient patient) {
        patientMapper.insert(patient);
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