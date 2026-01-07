package com.example.hospital.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.hospital.entity.DoctorDisease;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface DoctorDiseaseMapper extends BaseMapper<DoctorDisease> {
    
    /**
     * 根据医生ID查询关联的病种ID列表
     */
    @Select("SELECT disease_id FROM sys_doctor_disease WHERE doctor_id = #{doctorId}")
    List<Integer> selectDiseaseIdsByDoctorId(@Param("doctorId") Integer doctorId);
    
    /**
     * 根据医生ID查询关联的病种ID列表（别名）
     */
    @Select("SELECT disease_id FROM sys_doctor_disease WHERE doctor_id = #{doctorId}")
    List<Integer> findDiseaseIdsByDoctorId(@Param("doctorId") Integer doctorId);
    
    /**
     * 根据病种ID查询关联的医生ID列表
     */
    @Select("SELECT doctor_id FROM sys_doctor_disease WHERE disease_id = #{diseaseId}")
    List<Integer> findDoctorIdsByDiseaseId(@Param("diseaseId") Integer diseaseId);
    
    /**
     * 根据医生ID删除所有关联关系
     */
    @Delete("DELETE FROM sys_doctor_disease WHERE doctor_id = #{doctorId}")
    int deleteByDoctorId(@Param("doctorId") Integer doctorId);
}

