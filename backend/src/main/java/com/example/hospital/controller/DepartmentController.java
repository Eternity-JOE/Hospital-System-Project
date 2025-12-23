package com.example.hospital.controller;

import com.example.hospital.common.Result;
import com.example.hospital.entity.Department;
import com.example.hospital.mapper.DepartmentMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/department")
public class DepartmentController {

    @Autowired
    private DepartmentMapper departmentMapper;

    /**
     * 查询所有科室
     */
    @GetMapping("/list")
    public Result<?> list() {
        List<Department> list = departmentMapper.selectList(null);
        return Result.success(list);
    }

    /**
     * 新增科室
     */
    @PostMapping
    public Result<?> save(@RequestBody Department department) {
        if (department.getStatus() == null) {
            department.setStatus(1);
        }
        departmentMapper.insert(department);
        return Result.success(null);
    }

    /**
     * 修改科室
     */
    @PutMapping
    public Result<?> update(@RequestBody Department department) {
        departmentMapper.updateById(department);
        return Result.success(null);
    }

    /**
     * 删除科室
     */
    @DeleteMapping("/{id}")
    public Result<?> delete(@PathVariable Integer id) {
        departmentMapper.deleteById(id);
        return Result.success(null);
    }
}
