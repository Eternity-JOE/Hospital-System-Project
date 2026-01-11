package com.example.hospital.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.example.hospital.common.Result;
import com.example.hospital.entity.*;
import com.example.hospital.mapper.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.text.Collator;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.Locale;
import java.util.*;

@RestController
@RequestMapping("/api/patient/registration")
public class PatientRegistrationController {

    @Autowired
    private RegistrationMapper registrationMapper;
    @Autowired
    private DoctorMapper doctorMapper;
    @Autowired
    private DoctorDiseaseMapper doctorDiseaseMapper;
    @Autowired
    private DiseaseMapper diseaseMapper;
    @Autowired
    private DepartmentMapper departmentMapper;
    @Autowired
    private DoctorLeaveMapper doctorLeaveMapper;

    // 职称优先级映射
    private int getTitlePriority(String title) {
        if (title == null) return 0;
        if (title.contains("主任医师")) return 4;
        if (title.contains("副主任医师")) return 3;
        if (title.contains("主治医师")) return 2;
        if (title.contains("住院医师")) return 1;
        return 0;
    }
    
    // 检查医生在指定日期是否有排班
    private boolean isDoctorScheduled(Doctor doctor, String dateStr) {
        if (dateStr == null || doctor.getSchedule() == null) {
            return true; // 没有指定日期或没有排班设置，默认可用
        }
        
        LocalDate date = LocalDate.parse(dateStr);
        int dayOfWeek = date.getDayOfWeek().getValue(); // 1=周一, 7=周日
        
        String schedule = doctor.getSchedule();
        
        // 动态排班：休息日是今天往后第4天和第6天
        if ("DYNAMIC".equals(schedule)) {
            LocalDate today = LocalDate.now();
            int todayDow = today.getDayOfWeek().getValue(); // 1-7
            
            // 计算往后第4天和第6天是星期几
            int rest1 = ((todayDow - 1 + 4) % 7) + 1; // 往后第4天
            int rest2 = ((todayDow - 1 + 6) % 7) + 1; // 往后第6天
            
            // 如果目标日期是休息日，返回false
            return dayOfWeek != rest1 && dayOfWeek != rest2;
        }
        
        // 普通医生：检查schedule字段
        // schedule格式: "1,3,4,6,7" 表示周一、周三、周四、周六、周日上班
        String[] workDays = schedule.split(",");
        for (String day : workDays) {
            if (Integer.parseInt(day.trim()) == dayOfWeek) {
                return true;
            }
        }
        return false;
    }

    // 获取科室列表
    @GetMapping("/departments")
    public Result<List<Department>> getDepartments() {
        QueryWrapper<Department> wrapper = new QueryWrapper<>();
        wrapper.eq("status", 1);
        return Result.success(departmentMapper.selectList(wrapper));
    }

    // 获取病种列表
    @GetMapping("/diseases")
    public Result<List<Disease>> getDiseases() {
        QueryWrapper<Disease> wrapper = new QueryWrapper<>();
        wrapper.eq("status", 1);
        return Result.success(diseaseMapper.selectList(wrapper));
    }

    // 获取可挂号的医生列表（自由挂号）
    @GetMapping("/doctors")
    public Result<List<Map<String, Object>>> getDoctors(
            @RequestParam(required = false) Integer departmentId,
            @RequestParam(required = false) String date,
            @RequestParam(required = false) String timeSlot,
            @RequestParam(required = false) String sortBy) {
        
        QueryWrapper<Doctor> wrapper = new QueryWrapper<>();
        wrapper.eq("status", 1);
        if (departmentId != null) {
            wrapper.eq("department_id", departmentId);
        }
        
        List<Doctor> doctors = doctorMapper.selectList(wrapper);
        List<Map<String, Object>> result = new ArrayList<>();
        
        for (Doctor doc : doctors) {
            // 检查医生排班
            if (date != null && !isDoctorScheduled(doc, date)) {
                continue; // 该日期医生不上班
            }
            
            // 检查医生是否请假
            if (date != null && timeSlot != null) {
                int leaveCount = doctorLeaveMapper.checkLeaveExists(doc.getId(), date, timeSlot);
                if (leaveCount > 0) continue;
            }
            
            Map<String, Object> item = new HashMap<>();
            item.put("id", doc.getId());
            item.put("name", doc.getName());
            item.put("gender", doc.getGender());
            item.put("title", doc.getTitle());
            item.put("titlePriority", getTitlePriority(doc.getTitle()));
            item.put("departmentId", doc.getDepartmentId());
            item.put("phone", doc.getPhone());
            item.put("specialty", doc.getSpecialty());
            item.put("schedule", doc.getSchedule());
            
            Department dept = departmentMapper.selectById(doc.getDepartmentId());
            item.put("departmentName", dept != null ? dept.getName() : "-");
            
            // 获取当前时段已挂号人数
            int bookedCount = 0;
            if (date != null && timeSlot != null) {
                QueryWrapper<Registration> regWrapper = new QueryWrapper<>();
                regWrapper.eq("doctor_id", doc.getId())
                         .eq("appointment_date", date)
                         .eq("time_slot", timeSlot)
                         .eq("status", 1);
                bookedCount = Math.toIntExact(registrationMapper.selectCount(regWrapper));
            }
            item.put("bookedCount", bookedCount);
            
            List<Integer> diseaseIds = doctorDiseaseMapper.findDiseaseIdsByDoctorId(doc.getId());
            item.put("diseaseIds", diseaseIds);
            
            result.add(item);
        }
        
        // 排序
        Collator collator = Collator.getInstance(Locale.CHINA);
        
        if ("title".equals(sortBy)) {
            result.sort((a, b) -> {
                Integer pa = (Integer) a.get("titlePriority");
                Integer pb = (Integer) b.get("titlePriority");
                return pb.compareTo(pa); // 职称高的排前面
            });
        } else if ("available".equals(sortBy)) {
            result.sort((a, b) -> {
                Integer ca = (Integer) a.get("bookedCount");
                Integer cb = (Integer) b.get("bookedCount");
                return ca.compareTo(cb);
            });
        } else {
            // 默认排序：按医生姓氏首字母A到Z排序（中文按拼音排序）
            result.sort((a, b) -> {
                String na = (String) a.get("name");
                String nb = (String) b.get("name");
                return collator.compare(na, nb);
            });
        }
        
        return Result.success(result);
    }

    // 根据病种获取关联医生（定向挂号）
    @GetMapping("/doctors/byDisease")
    public Result<List<Map<String, Object>>> getDoctorsByDisease(
            @RequestParam Integer diseaseId,
            @RequestParam(required = false) String date,
            @RequestParam(required = false) String timeSlot,
            @RequestParam(required = false) String sortBy) {
        
        List<Integer> doctorIds = doctorDiseaseMapper.findDoctorIdsByDiseaseId(diseaseId);
        if (doctorIds.isEmpty()) {
            return Result.success(new ArrayList<>());
        }
        
        QueryWrapper<Doctor> wrapper = new QueryWrapper<>();
        wrapper.in("id", doctorIds).eq("status", 1);
        List<Doctor> doctors = doctorMapper.selectList(wrapper);
        
        List<Map<String, Object>> result = new ArrayList<>();
        for (Doctor doc : doctors) {
            // 检查医生排班
            if (date != null && !isDoctorScheduled(doc, date)) {
                continue; // 该日期医生不上班
            }
            
            if (date != null && timeSlot != null) {
                int leaveCount = doctorLeaveMapper.checkLeaveExists(doc.getId(), date, timeSlot);
                if (leaveCount > 0) continue;
            }
            
            Map<String, Object> item = new HashMap<>();
            item.put("id", doc.getId());
            item.put("name", doc.getName());
            item.put("gender", doc.getGender());
            item.put("title", doc.getTitle());
            item.put("titlePriority", getTitlePriority(doc.getTitle()));
            item.put("departmentId", doc.getDepartmentId());
            item.put("specialty", doc.getSpecialty());
            item.put("schedule", doc.getSchedule());
            
            Department dept = departmentMapper.selectById(doc.getDepartmentId());
            item.put("departmentName", dept != null ? dept.getName() : "-");
            
            int bookedCount = 0;
            if (date != null && timeSlot != null) {
                QueryWrapper<Registration> regWrapper = new QueryWrapper<>();
                regWrapper.eq("doctor_id", doc.getId())
                         .eq("appointment_date", date)
                         .eq("time_slot", timeSlot)
                         .eq("status", 1);
                bookedCount = Math.toIntExact(registrationMapper.selectCount(regWrapper));
            }
            item.put("bookedCount", bookedCount);
            
            result.add(item);
        }
        
        if ("title".equals(sortBy)) {
            result.sort((a, b) -> ((Integer) b.get("titlePriority")).compareTo((Integer) a.get("titlePriority")));
        } else if ("available".equals(sortBy)) {
            result.sort((a, b) -> ((Integer) a.get("bookedCount")).compareTo((Integer) b.get("bookedCount")));
        } else {
            // 默认排序：按医生姓氏首字母A到Z排序（中文按拼音排序）
            Collator collator = Collator.getInstance(Locale.CHINA);
            result.sort((a, b) -> collator.compare((String) a.get("name"), (String) b.get("name")));
        }
        
        return Result.success(result);
    }

    // 病人挂号
    @PostMapping("/book")
    public Result<String> book(@RequestBody Map<String, Object> data) {
        Integer patientId = Integer.valueOf(data.get("patientId").toString());
        Integer doctorId = Integer.valueOf(data.get("doctorId").toString());
        String appointmentDateStr = (String) data.get("appointmentDate");
        String timeSlot = (String) data.get("timeSlot");
        Integer isReturn = data.get("isReturn") != null ? Integer.valueOf(data.get("isReturn").toString()) : 0;
        Integer registrationType = data.get("registrationType") != null ? Integer.valueOf(data.get("registrationType").toString()) : 1;
        
        LocalDate appointmentDate = LocalDate.parse(appointmentDateStr);
        
        Doctor doctor = doctorMapper.selectById(doctorId);
        if (doctor == null) {
            return Result.error("医生不存在");
        }
        
        // 检查医生排班
        if (!isDoctorScheduled(doctor, appointmentDateStr)) {
            return Result.error("该医生在此日期不出诊，请选择其他日期或医生");
        }
        
        int leaveCount = doctorLeaveMapper.checkLeaveExists(doctorId, appointmentDateStr, timeSlot);
        if (leaveCount > 0) {
            return Result.error("该医生在此时段已请假，请选择其他时段或医生");
        }
        
        QueryWrapper<Registration> checkWrapper = new QueryWrapper<>();
        checkWrapper.eq("patient_id", patientId)
                   .eq("doctor_id", doctorId)
                   .eq("appointment_date", appointmentDateStr)
                   .eq("time_slot", timeSlot)
                   .eq("status", 1);
        if (registrationMapper.selectCount(checkWrapper) > 0) {
            return Result.error("您已在该时段挂过此医生的号");
        }
        
        // 处理patientTypes数组，转换为单个整数（使用位运算）
        Integer patientType = 0;
        if (data.get("patientTypes") != null) {
            java.util.List<Integer> types = (java.util.List<Integer>) data.get("patientTypes");
            for (Integer type : types) {
                patientType |= (1 << type);
            }
        }
        
        Registration reg = new Registration();
        reg.setPatientId(patientId);
        reg.setDoctorId(doctorId);
        reg.setDepartmentId(doctor.getDepartmentId());
        reg.setAppointmentDate(appointmentDate);
        reg.setTimeSlot(timeSlot);
        reg.setRegistrationType(registrationType);
        reg.setIsReturn(isReturn);
        reg.setPatientType(patientType);
        reg.setStatus(1);
        reg.setPriorityScore(calculatePriority(registrationType, isReturn, patientType));
        
        registrationMapper.insert(reg);
        
        return Result.success("挂号成功");
    }
    
    // 计算优先级分数
    // 计算优先级分数
    private int calculatePriority(Integer registrationType, Integer isReturn, Integer patientType) {
        int score = 100;
        
        // 急诊加分
        if (registrationType != null && registrationType == 2) {
            score += 1000;
        }
        
        // 复诊加分
        if (isReturn != null && isReturn == 1) {
            score += 50;
        }
        
        // 军人加分
        if ((patientType & (1 << 1)) != 0) {
            score += 30;
        }
        
        // 老人加分
        if ((patientType & (1 << 2)) != 0) {
            score += 20;
        }
        
        // 儿童加分
        if ((patientType & (1 << 3)) != 0) {
            score += 15;
        }
        
        return score;
    }

    // 获取病人的挂号记录
    @GetMapping("/myList")
    public Result<List<Map<String, Object>>> getMyRegistrations(@RequestParam Integer patientId) {
        QueryWrapper<Registration> wrapper = new QueryWrapper<>();
        wrapper.eq("patient_id", patientId).orderByDesc("appointment_date", "create_time");
        List<Registration> list = registrationMapper.selectList(wrapper);
        
        List<Map<String, Object>> result = new ArrayList<>();
        for (Registration reg : list) {
            Map<String, Object> item = new HashMap<>();
            item.put("id", reg.getId());
            item.put("appointmentDate", reg.getAppointmentDate() != null ? reg.getAppointmentDate().toString() : null);
            item.put("timeSlot", reg.getTimeSlot());
            item.put("status", reg.getStatus());
            item.put("createTime", reg.getCreateTime());
            
            if (reg.getDoctorId() != null) {
                Doctor doc = doctorMapper.selectById(reg.getDoctorId());
                if (doc != null) {
                    item.put("doctorId", doc.getId());
                    item.put("doctorName", doc.getName());
                    item.put("doctorTitle", doc.getTitle());
                }
            }
            
            Department dept = departmentMapper.selectById(reg.getDepartmentId());
            item.put("departmentName", dept != null ? dept.getName() : "-");
            
            result.add(item);
        }
        
        return Result.success(result);
    }

    // 获取排队列表（病人只能看到顺序，不能看到优先级）
    @GetMapping("/queue")
    public Result<List<Map<String, Object>>> getQueue(
            @RequestParam Integer doctorId,
            @RequestParam String date,
            @RequestParam String timeSlot,
            @RequestParam Integer patientId) {
        
        QueryWrapper<Registration> wrapper = new QueryWrapper<>();
        wrapper.eq("doctor_id", doctorId)
               .eq("appointment_date", date)
               .eq("time_slot", timeSlot)
               .eq("status", 1)
               .orderByDesc("priority_score");
        
        List<Registration> registrations = registrationMapper.selectList(wrapper);
        List<Map<String, Object>> result = new ArrayList<>();
        
        for (int i = 0; i < registrations.size(); i++) {
            Registration reg = registrations.get(i);
            Map<String, Object> item = new HashMap<>();
            item.put("queueNumber", i + 1);
            
            // 如果是当前患者，显示"您"，否则显示"患者N"
            if (reg.getPatientId().equals(patientId)) {
                item.put("patientName", "您");
            } else {
                item.put("patientName", "患者" + (i + 1));
            }
            
            result.add(item);
        }
        
        return Result.success(result);
    }

    // 取消挂号
    @PostMapping("/cancel/{id}")
    public Result<String> cancel(@PathVariable Integer id) {
        Registration reg = registrationMapper.selectById(id);
        if (reg == null) {
            return Result.error("挂号记录不存在");
        }
        if (reg.getStatus() != 1) {
            return Result.error("只能取消待就诊的挂号");
        }
        
        reg.setStatus(0);
        registrationMapper.updateById(reg);
        
        return Result.success("取消成功");
    }
}
