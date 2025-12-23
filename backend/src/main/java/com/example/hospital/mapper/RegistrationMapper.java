package com.example.hospital.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.hospital.entity.Registration;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.time.LocalDate;
import java.util.List;

@Mapper
public interface RegistrationMapper extends BaseMapper<Registration> {
    
    /**
     * 查询挂号列表（带患者、科室、医生信息）
     */
    @Select("SELECT r.*, p.name as patient_name, p.age as patient_age, " +
            "d.name as department_name " +
            "FROM sys_registration r " +
            "LEFT JOIN sys_patient p ON r.patient_id = p.id " +
            "LEFT JOIN sys_department d ON r.department_id = d.id " +
            "ORDER BY r.appointment_date DESC, r.priority_score DESC")
    List<Registration> selectRegistrationList();
    
    /**
     * 查询某天某科室某时段的挂号数量
     */
    @Select("SELECT COUNT(*) FROM sys_registration " +
            "WHERE department_id = #{departmentId} " +
            "AND appointment_date = #{date} " +
            "AND time_slot = #{timeSlot} " +
            "AND status != 0")
    int countByDepartmentAndDateAndSlot(@Param("departmentId") Integer departmentId,
                                        @Param("date") LocalDate date,
                                        @Param("timeSlot") String timeSlot);
    
    /**
     * 查询患者是否在同一医生处有历史就诊记录（用于判断复诊）
     */
    @Select("SELECT COUNT(*) FROM sys_registration " +
            "WHERE patient_id = #{patientId} " +
            "AND doctor_id = #{doctorId} " +
            "AND status = 2")
    int countCompletedByPatientAndDoctor(@Param("patientId") Integer patientId,
                                         @Param("doctorId") Integer doctorId);
    
    /**
     * 查询某天某科室的待就诊队列（按优先级排序）
     */
    @Select("SELECT r.*, p.name as patient_name, p.age as patient_age " +
            "FROM sys_registration r " +
            "LEFT JOIN sys_patient p ON r.patient_id = p.id " +
            "WHERE r.department_id = #{departmentId} " +
            "AND r.appointment_date = #{date} " +
            "AND r.time_slot = #{timeSlot} " +
            "AND r.status = 1 " +
            "ORDER BY r.priority_score DESC, r.create_time ASC")
    List<Registration> selectQueueByDepartmentAndDate(@Param("departmentId") Integer departmentId,
                                                      @Param("date") LocalDate date,
                                                      @Param("timeSlot") String timeSlot);
}
