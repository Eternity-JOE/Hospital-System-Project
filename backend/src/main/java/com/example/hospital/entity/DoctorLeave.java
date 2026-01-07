package com.example.hospital.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@TableName("sys_doctor_leave")
public class DoctorLeave {
    @TableId(type = IdType.AUTO)
    private Integer id;
    private Integer doctorId;
    private LocalDate leaveDate;
    private String timeSlot;  // AM or PM
    private String reason;
    private Integer status;   // 0-待审批，1-已批准，2-已拒绝
    private LocalDateTime applyTime;
    private LocalDateTime approveTime;
    private Integer approverId;
    
    @TableField(exist = false)
    private String doctorName;  // 医生姓名（查询用）
}
