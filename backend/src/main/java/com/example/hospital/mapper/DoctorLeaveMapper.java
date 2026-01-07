package com.example.hospital.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.hospital.entity.DoctorLeave;
import org.apache.ibatis.annotations.*;
import java.util.List;

@Mapper
public interface DoctorLeaveMapper extends BaseMapper<DoctorLeave> {
    
    // 查询医生某月的请假次数（已批准的）
    @Select("SELECT COUNT(*) FROM sys_doctor_leave WHERE doctor_id = #{doctorId} AND status = 1 AND DATE_FORMAT(leave_date, '%Y-%m') = #{yearMonth}")
    int countApprovedLeaveInMonth(@Param("doctorId") Integer doctorId, @Param("yearMonth") String yearMonth);
    
    // 查询所有待审批的请假申请（带医生姓名）
    @Select("SELECT l.*, d.name as doctor_name FROM sys_doctor_leave l LEFT JOIN sys_doctor d ON l.doctor_id = d.id WHERE l.status = 0 ORDER BY l.apply_time DESC")
    List<DoctorLeave> findPendingLeaves();
    
    // 查询医生的请假记录
    @Select("SELECT * FROM sys_doctor_leave WHERE doctor_id = #{doctorId} ORDER BY leave_date DESC")
    List<DoctorLeave> findByDoctorId(@Param("doctorId") Integer doctorId);
    
    // 查询医生某段时间内已批准的请假
    @Select("SELECT * FROM sys_doctor_leave WHERE doctor_id = #{doctorId} AND leave_date >= #{startDate} AND leave_date <= #{endDate} AND status = 1")
    List<DoctorLeave> findApprovedLeavesByDateRange(@Param("doctorId") Integer doctorId, @Param("startDate") String startDate, @Param("endDate") String endDate);
    
    // 检查某医生某天某时段是否已请假（已批准）
    @Select("SELECT COUNT(*) FROM sys_doctor_leave WHERE doctor_id = #{doctorId} AND leave_date = #{leaveDate} AND time_slot = #{timeSlot} AND status = 1")
    int checkLeaveExists(@Param("doctorId") Integer doctorId, @Param("leaveDate") String leaveDate, @Param("timeSlot") String timeSlot);
}
