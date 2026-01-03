package com.example.hospital.controller;

import com.example.hospital.common.Result;
import com.example.hospital.entity.Disease;
import com.example.hospital.mapper.DiseaseMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/disease")
public class DiseaseController {

    @Autowired
    private DiseaseMapper diseaseMapper;

    // 查询所有病种（用于下拉选择）
    @GetMapping("/list")
    public Result<?> list() {
        List<Disease> list = diseaseMapper.selectList(null);
        return Result.success(list);
    }
}

