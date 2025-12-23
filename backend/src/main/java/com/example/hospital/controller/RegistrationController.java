package com.example.hospital.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.example.hospital.common.Result;
import com.example.hospital.entity.Patient;
import com.example.hospital.entity.PatientRecord;
import com.example.hospital.entity.Registration;
import com.example.hospital.mapper.PatientMapper;
import com.example.hospital.mapper.PatientRecordMapper;
import com.example.hospital.mapper.RegistrationMapper;
import com.example.hospital.service.PriorityCalculator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/registration")
public class RegistrationController {

    @Autowired
    private RegistrationMapper registrationMapper;
    
    @Autowired
    private PatientMapper patientMapper;
    
    @Autowired
    private PatientRecordMapper patientRecordMapper;
    
    @Autowired
    private PriorityCalculator priorityCalculator;
    
    /** 每个时段最大挂号数 */
    private static final int MAX_SLOT_CAPACITY = 30;

    /**
     * 查询所有挂号记录
     */
    @GetMapping("/list")
    public Result<?> list() {
        List<Registration> list = registrationMapper.selectRegistrationList();
        return Result.success(list);
    }
    
    /**
     * 查询某天某科室某时段的排队队列（按优先级排序）
     */
    @GetMapping("/queue")
    public Result<?> getQueue(@RequestParam Integer departmentId,
                              @RequestParam String date,
                              @RequestParam String timeSlot) {
        LocalDate appointmentDate = LocalDate.parse(date);
        List<Registration> queue = registrationMapper.selectQueueByDepartmentAndDate(
                departmentId, appointmentDate, timeSlot);
        
        // 动态更新等待时间加分
        for (int i = 0; i < queue.size(); i++) {
            Registration r = queue.get(i);
            r.setPriorityScore(priorityCalculator.updateWaitingBonus(r));
            r.setQueueNumber(i + 1);
        }
        
        return Result.success(queue);
    }

    /**
     * 新增挂号
     */
    @PostMapping
    public Result<?> save(@RequestBody Registration registration) {
        // 1. 检查号源是否充足
        int currentCount = registrationMapper.countByDepartmentAndDateAndSlot(
                registration.getDepartmentId(),
                registration.getAppointmentDate(),
                registration.getTimeSlot());
        if (currentCount >= MAX_SLOT_CAPACITY) {
            return Result.error("该时段号源已满，请选择其他时段");
        }
        
        // 2. 获取患者年龄用于优先级计算
        Integer patientAge = null;
        if (registration.getPatientId() != null) {
            Patient patient = patientMapper.selectById(registration.getPatientId());
            if (patient != null) {
                patientAge = patient.getAge();
            }
        }
        
        // 3. 计算优先级分数
        int priorityScore = priorityCalculator.calculatePriority(registration, patientAge);
        registration.setPriorityScore(priorityScore);
        
        // 4. 设置默认值
        registration.setStatus(1); // 待就诊
        registration.setCreateTime(LocalDateTime.now());
        registration.setUpdateTime(LocalDateTime.now());
        if (registration.getIsReturn() == null) {
            registration.setIsReturn(0);
        }
        if (registration.getPatientType() == null) {
            registration.setPatientType(0);
        }
        if (registration.getRegistrationType() == null) {
            registration.setRegistrationType(1);
        }
        
        registrationMapper.insert(registration);
        
        // 5. 更新患者挂号统计
        updatePatientRecord(registration.getPatientId(), "register");
        
        return Result.success(null);
    }

    /**
     * 修改挂号信息
     */
    @PutMapping
    public Result<?> update(@RequestBody Registration registration) {
        registration.setUpdateTime(LocalDateTime.now());
        
        // 重新计算优先级
        Integer patientAge = null;
        if (registration.getPatientId() != null) {
            Patient patient = patientMapper.selectById(registration.getPatientId());
            if (patient != null) {
                patientAge = patient.getAge();
            }
        }
        int priorityScore = priorityCalculator.calculatePriority(registration, patientAge);
        registration.setPriorityScore(priorityScore);
        
        registrationMapper.updateById(registration);
        return Result.success(null);
    }

    /**
     * 取消挂号（退号）
     * 规则：问诊前24小时可正常退号，返还号源
     */
    @PostMapping("/cancel/{id}")
    public Result<?> cancel(@PathVariable Integer id) {
        Registration registration = registrationMapper.selectById(id);
        if (registration == null) {
            return Result.error("挂号记录不存在");
        }
        
        if (registration.getStatus() != 1) {
            return Result.error("只有待就诊状态的挂号可以取消");
        }
        
        // 检查是否在24小时前
        LocalDateTime appointmentDateTime = registration.getAppointmentDate().atStartOfDay();
        if ("PM".equals(registration.getTimeSlot())) {
            appointmentDateTime = appointmentDateTime.plusHours(14); // 下午2点
        } else {
            appointmentDateTime = appointmentDateTime.plusHours(8); // 上午8点
        }
        
        long hoursUntilAppointment = ChronoUnit.HOURS.between(LocalDateTime.now(), appointmentDateTime);
        if (hoursUntilAppointment < 24) {
            return Result.error("距离就诊时间不足24小时，无法取消挂号");
        }
        
        // 取消挂号
        registration.setStatus(0);
        registration.setUpdateTime(LocalDateTime.now());
        registrationMapper.updateById(registration);
        
        return Result.success("退号成功，号源已返还");
    }

    /**
     * 完成就诊
     */
    @PostMapping("/complete/{id}")
    public Result<?> complete(@PathVariable Integer id) {
        Registration registration = registrationMapper.selectById(id);
        if (registration == null) {
            return Result.error("挂号记录不存在");
        }
        
        registration.setStatus(2); // 已完成
        registration.setUpdateTime(LocalDateTime.now());
        registrationMapper.updateById(registration);
        
        // 更新患者完成就诊统计
        updatePatientRecord(registration.getPatientId(), "complete");
        
        return Result.success(null);
    }

    /**
     * 标记爽约
     */
    @PostMapping("/noshow/{id}")
    public Result<?> markNoShow(@PathVariable Integer id) {
        Registration registration = registrationMapper.selectById(id);
        if (registration == null) {
            return Result.error("挂号记录不存在");
        }
        
        registration.setStatus(3); // 爽约
        registration.setUpdateTime(LocalDateTime.now());
        registrationMapper.updateById(registration);
        
        // 更新患者爽约统计
        updatePatientRecord(registration.getPatientId(), "noshow");
        
        return Result.success("已标记为爽约");
    }

    /**
     * 删除挂号记录
     */
    @DeleteMapping("/{id}")
    public Result<?> delete(@PathVariable Integer id) {
        registrationMapper.deleteById(id);
        return Result.success(null);
    }
    
    /**
     * 查询某天的号源情况
     */
    @GetMapping("/slots")
    public Result<?> getSlots(@RequestParam Integer departmentId, @RequestParam String date) {
        LocalDate appointmentDate = LocalDate.parse(date);
        
        int amCount = registrationMapper.countByDepartmentAndDateAndSlot(departmentId, appointmentDate, "AM");
        int pmCount = registrationMapper.countByDepartmentAndDateAndSlot(departmentId, appointmentDate, "PM");
        
        Map<String, Object> slots = new HashMap<>();
        slots.put("AM", Map.of("used", amCount, "total", MAX_SLOT_CAPACITY, "available", MAX_SLOT_CAPACITY - amCount));
        slots.put("PM", Map.of("used", pmCount, "total", MAX_SLOT_CAPACITY, "available", MAX_SLOT_CAPACITY - pmCount));
        
        return Result.success(slots);
    }
    
    /**
     * 更新患者信誉记录
     */
    private void updatePatientRecord(Integer patientId, String action) {
        if (patientId == null) return;
        
        PatientRecord record = patientRecordMapper.selectByPatientId(patientId);
        if (record == null) {
            record = new PatientRecord();
            record.setPatientId(patientId);
            record.setNoShowCount(0);
            record.setTotalCount(0);
            record.setCompletedCount(0);
        }
        
        switch (action) {
            case "register":
                record.setTotalCount(record.getTotalCount() + 1);
                break;
            case "complete":
                record.setCompletedCount(record.getCompletedCount() + 1);
                break;
            case "noshow":
                record.setNoShowCount(record.getNoShowCount() + 1);
                break;
        }
        
        if (record.getId() == null) {
            patientRecordMapper.insert(record);
        } else {
            patientRecordMapper.updateById(record);
        }
    }
}
