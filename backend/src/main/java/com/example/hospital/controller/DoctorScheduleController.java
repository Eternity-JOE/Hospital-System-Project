package com.example.hospital.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.example.hospital.common.Result;
import com.example.hospital.entity.DoctorLeave;
import com.example.hospital.entity.Patient;
import com.example.hospital.entity.Registration;
import com.example.hospital.mapper.DoctorLeaveMapper;
import com.example.hospital.mapper.PatientMapper;
import com.example.hospital.mapper.RegistrationMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;

@RestController
@RequestMapping("/api/doctor/schedule")
public class DoctorScheduleController {

    @Autowired
    private RegistrationMapper registrationMapper;
    
    @Autowired
    private PatientMapper patientMapper;
    
    @Autowired
    private DoctorLeaveMapper doctorLeaveMapper;

    // 获取医生某周的预约安排
    @GetMapping("/week")
    public Result<Map<String, Object>> getWeekSchedule(
            @RequestParam Integer doctorId,
            @RequestParam String startDate,
            @RequestParam String endDate) {
        
        // 获取预约记录
        QueryWrapper<Registration> wrapper = new QueryWrapper<>();
        wrapper.eq("doctor_id", doctorId)
               .ge("appointment_date", startDate)
               .le("appointment_date", endDate)
               .in("status", 1, 2) // 待就诊和已完成
               .orderByAsc("appointment_date", "time_slot");
        
        List<Registration> registrations = registrationMapper.selectList(wrapper);
        
        List<Map<String, Object>> appointments = new ArrayList<>();
        for (Registration reg : registrations) {
            Map<String, Object> item = new HashMap<>();
            item.put("id", reg.getId());
            item.put("appointmentDate", reg.getAppointmentDate());
            item.put("timeSlot", reg.getTimeSlot());
            item.put("status", reg.getStatus());
            item.put("patientId", reg.getPatientId());
            
            // 获取病人信息
            Patient patient = patientMapper.selectById(reg.getPatientId());
            if (patient != null) {
                item.put("patientName", patient.getName());
                item.put("patientGender", patient.getGender());
                item.put("patientAge", patient.getAge());
                item.put("patientPhone", patient.getPhone());
            }
            
            appointments.add(item);
        }
        
        // 获取已批准的请假记录
        List<DoctorLeave> leaves = doctorLeaveMapper.findApprovedLeavesByDateRange(doctorId, startDate, endDate);
        List<Map<String, Object>> leaveList = new ArrayList<>();
        for (DoctorLeave leave : leaves) {
            Map<String, Object> item = new HashMap<>();
            item.put("leaveDate", leave.getLeaveDate().toString());
            item.put("timeSlot", leave.getTimeSlot());
            leaveList.add(item);
        }
        
        Map<String, Object> result = new HashMap<>();
        result.put("appointments", appointments);
        result.put("leaves", leaveList);
        
        return Result.success(result);
    }
}
