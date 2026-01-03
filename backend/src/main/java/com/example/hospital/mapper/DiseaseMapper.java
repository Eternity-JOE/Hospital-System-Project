package com.example.hospital.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.hospital.entity.Disease;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface DiseaseMapper extends BaseMapper<Disease> {
}

