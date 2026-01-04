package com.example.hospital.controller;

import com.example.hospital.common.Result;
import com.example.hospital.entity.Medicine;
import com.example.hospital.mapper.MedicineMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/medicine")
public class MedicineController {

    @Autowired
    private MedicineMapper medicineMapper;

    // 1. 查询所有药品
    @GetMapping("/list")
    public Result<?> list() {
        List<Medicine> list = medicineMapper.selectList(null);
        return Result.success(list);
    }

    // 2. 新增药品
    @PostMapping
    public Result<?> save(@RequestBody Medicine medicine) {
        medicineMapper.insert(medicine);
        return Result.success(null);
    }

    // 3. 修改药品信息
    @PutMapping
    public Result<?> update(@RequestBody Medicine medicine) {
        medicineMapper.updateById(medicine);
        return Result.success(null);
    }

    // 4. 删除药品
    @DeleteMapping("/{id}")
    public Result<?> delete(@PathVariable Integer id) {
        medicineMapper.deleteById(id);
        return Result.success(null);
    }
}