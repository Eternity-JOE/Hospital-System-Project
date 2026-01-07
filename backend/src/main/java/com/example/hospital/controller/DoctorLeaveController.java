package com.example.hospital.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.example.hospital.common.Result;
import com.example.hospital.entity.DoctorLeave;
import com.example.hospital.entity.Registration;
import com.example.hospital.mapper.DoctorLeaveMapper;
import com.example.hospital.mapper.RegistrationMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/doctor/leave")
public class DoctorLeaveController {

    @Autowired
    private DoctorLeaveMapper doctorLeaveMapper;
    
    @Autowired
    private RegistrationMapper registrationMapper;

    // 获取医生的请假记录
    @GetMapping("/list")
    public Result<List<DoctorLeave>> list(@RequestParam Integer doctorId) {
        List<DoctorLeave> list = doctorLeaveMapper.findByDoctorId(doctorId);
        return Result.success(list);
    }

    // 获取本月已用请假次数
    @GetMapping("/monthCount")
    public Result<Integer> getMonthCount(@RequestParam Integer doctorId) {
        String yearMonth = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy-MM"));
        int count = doctorLeaveMapper.countApprovedLeaveInMonth(doctorId, yearMonth);
        return Result.success(count);
    }

    // 申请请假
    @PostMapping("/apply")
    public Result<String> apply(@RequestBody Map<String, Object> data) {
        Integer doctorId = Integer.valueOf(data.get("doctorId").toString());
        String leaveDateStr = (String) data.get("leaveDate");
        String timeSlot = (String) data.get("timeSlot");
        String reason = (String) data.get("reason");

        LocalDate leaveDate = LocalDate.parse(leaveDateStr);
        
        // 检查是否是未来日期
        if (!leaveDate.isAfter(LocalDate.now())) {
            return Result.error("只能申请未来日期的请假");
        }

        // 检查本月请假次数
        String yearMonth = leaveDate.format(DateTimeFormatter.ofPattern("yyyy-MM"));
        int monthCount = doctorLeaveMapper.countApprovedLeaveInMonth(doctorId, yearMonth);
        if (monthCount >= 3) {
            return Result.error("本月请假次数已达上限（3次）");
        }

        // 检查该时段是否有病人预约
        QueryWrapper<Registration> wrapper = new QueryWrapper<>();
        wrapper.eq("doctor_id", doctorId)
               .eq("appointment_date", leaveDateStr)
               .eq("time_slot", timeSlot)
               .in("status", 1); // 待就诊状态
        Long patientCount = registrationMapper.selectCount(wrapper);
        if (patientCount > 0) {
            return Result.error("该时段已有病人预约，无法请假");
        }

        // 检查是否已申请过
        QueryWrapper<DoctorLeave> leaveWrapper = new QueryWrapper<>();
        leaveWrapper.eq("doctor_id", doctorId)
                    .eq("leave_date", leaveDateStr)
                    .eq("time_slot", timeSlot);
        if (doctorLeaveMapper.selectCount(leaveWrapper) > 0) {
            return Result.error("该时段已有请假申请");
        }

        // 创建请假申请
        DoctorLeave leave = new DoctorLeave();
        leave.setDoctorId(doctorId);
        leave.setLeaveDate(leaveDate);
        leave.setTimeSlot(timeSlot);
        leave.setReason(reason);
        leave.setStatus(0);
        leave.setApplyTime(LocalDateTime.now());
        doctorLeaveMapper.insert(leave);

        return Result.success("申请已提交，等待审批");
    }

    // 取消请假申请（仅待审批状态可取消）
    @DeleteMapping("/{id}")
    public Result<String> cancel(@PathVariable Integer id) {
        DoctorLeave leave = doctorLeaveMapper.selectById(id);
        if (leave == null) {
            return Result.error("请假记录不存在");
        }
        if (leave.getStatus() != 0) {
            return Result.error("只能取消待审批的申请");
        }
        doctorLeaveMapper.deleteById(id);
        return Result.success("已取消");
    }
}

// 管理员审批请假
@RestController
@RequestMapping("/api/admin/leave")
class AdminLeaveController {

    @Autowired
    private DoctorLeaveMapper doctorLeaveMapper;

    // 获取待审批列表
    @GetMapping("/pending")
    public Result<List<DoctorLeave>> getPendingList() {
        List<DoctorLeave> list = doctorLeaveMapper.findPendingLeaves();
        return Result.success(list);
    }

    // 获取所有请假记录
    @GetMapping("/all")
    public Result<List<DoctorLeave>> getAllList() {
        QueryWrapper<DoctorLeave> wrapper = new QueryWrapper<>();
        wrapper.orderByDesc("apply_time");
        List<DoctorLeave> list = doctorLeaveMapper.selectList(wrapper);
        return Result.success(list);
    }

    // 审批请假
    @PostMapping("/approve")
    public Result<String> approve(@RequestBody Map<String, Object> data) {
        Integer id = Integer.valueOf(data.get("id").toString());
        Integer status = Integer.valueOf(data.get("status").toString()); // 1-批准，2-拒绝
        Integer approverId = data.get("approverId") != null ? Integer.valueOf(data.get("approverId").toString()) : null;

        DoctorLeave leave = doctorLeaveMapper.selectById(id);
        if (leave == null) {
            return Result.error("请假记录不存在");
        }
        if (leave.getStatus() != 0) {
            return Result.error("该申请已处理");
        }

        leave.setStatus(status);
        leave.setApproveTime(LocalDateTime.now());
        leave.setApproverId(approverId);
        doctorLeaveMapper.updateById(leave);

        return Result.success(status == 1 ? "已批准" : "已拒绝");
    }
}
