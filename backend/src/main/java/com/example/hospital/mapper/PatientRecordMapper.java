package com.example.hospital.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.hospital.entity.PatientRecord;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface PatientRecordMapper extends BaseMapper<PatientRecord> {
    
    /**
     * 根据患者ID查询信誉记录
     */
    @Select("SELECT * FROM sys_patient_record WHERE patient_id = #{patientId}")
    PatientRecord selectByPatientId(@Param("patientId") Integer patientId);
}
