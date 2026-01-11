package com.example.hospital.controller;

import com.example.hospital.common.Result;
import com.example.hospital.entity.Bill;
import com.example.hospital.entity.Medicine;
import com.example.hospital.entity.Registration;
import com.example.hospital.mapper.BillMapper;
import com.example.hospital.mapper.MedicineMapper;
import com.example.hospital.mapper.RegistrationMapper;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.math.BigDecimal;

@RestController
@RequestMapping("/bill")
public class BillController {

    @Autowired
    private BillMapper billMapper;
    
    @Autowired
    private MedicineMapper medicineMapper;
    
    @Autowired
    private RegistrationMapper registrationMapper;

    @GetMapping("/list")
    public Result<?> list() {
        List<Bill> list = billMapper.selectList(null);
        return Result.success(list);
    }

    @PostMapping
    public Result<?> save(@RequestBody Bill bill) {
        bill.setCreateTime(LocalDateTime.now());
        billMapper.insert(bill);
        return Result.success(null);
    }

    @PutMapping("/pay/{id}")
    public Result<?> pay(@PathVariable Integer id) {
        Bill bill = billMapper.selectById(id);
        if (bill != null) {
            bill.setStatus(1);
            bill.setPayTime(LocalDateTime.now());
            billMapper.updateById(bill);
            return Result.success("支付成功");
        }
        return Result.error("未找到账单");
    }

    @GetMapping("/total-revenue")
    public Result<?> getTotalRevenue() {
        List<Bill> list = billMapper.selectList(null);
        BigDecimal total = list.stream()
                .filter(b -> b.getStatus() == 1)
                .map(Bill::getTotalAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        return Result.success(total);
    }

    @GetMapping("/doctor/list")
    public Result<?> getDoctorBillList(@RequestParam Integer doctorId) {
        LambdaQueryWrapper<Bill> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Bill::getDoctorId, doctorId);
        List<Bill> billList = billMapper.selectList(wrapper);
        return Result.success(billList);
    }

    @PostMapping("/doctor/create")
    public Result<?> createDoctorBill(@RequestBody Bill bill) {
        try {
            bill.setStatus(0);
            bill.setCreateTime(LocalDateTime.now());
            billMapper.insert(bill);
            return Result.success("账单新增成功");
        } catch (Exception e) {
            e.printStackTrace();
            return Result.error("账单创建失败，请重试");
        }
    }

    @PutMapping("/doctor/pay/{id}")
    public Result<?> payDoctorBill(@PathVariable Integer id) {
        Bill bill = billMapper.selectById(id);
        if (bill == null) {
            return Result.error("未找到账单");
        }
        bill.setStatus(1);
        bill.setPayTime(LocalDateTime.now());
        billMapper.updateById(bill);
        return Result.success("缴费成功");
    }

    @GetMapping("/patient/list")
    public Result<?> getPatientBillList(@RequestParam Integer patientId) {
        LambdaQueryWrapper<Bill> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Bill::getPatientId, patientId)
               .orderByDesc(Bill::getCreateTime);
        List<Bill> billList = billMapper.selectList(wrapper);
        return Result.success(billList);
    }

    @PutMapping("/patient/pay/{id}")
    public Result<?> payPatientBill(@PathVariable Integer id) {
        Bill bill = billMapper.selectById(id);
        if (bill == null) {
            return Result.error("未找到账单");
        }
        bill.setStatus(1);
        bill.setPayTime(LocalDateTime.now());
        billMapper.updateById(bill);
        return Result.success("缴费成功");
    }
    
    // 医生诊断开药接口
    @PostMapping("/diagnose")
    public Result<?> diagnose(@RequestBody Map<String, Object> data) {
        try {
            Integer doctorId = (Integer) data.get("doctorId");
            Integer patientId = (Integer) data.get("patientId");
            Integer registrationId = (Integer) data.get("registrationId");
            String diagnosis = (String) data.get("diagnosis");
            List<Map<String, Object>> medicines = (List<Map<String, Object>>) data.get("medicines");
            Double registrationFee = data.get("registrationFee") != null ? ((Number) data.get("registrationFee")).doubleValue() : 15.0;
            Double totalAmount = data.get("totalAmount") != null ? ((Number) data.get("totalAmount")).doubleValue() : 0.0;
            
            // 验证并扣减药品库存
            if (medicines != null) {
                for (Map<String, Object> med : medicines) {
                    Integer medicineId = (Integer) med.get("medicineId");
                    Integer quantity = (Integer) med.get("quantity");
                    
                    Medicine medicine = medicineMapper.selectById(medicineId);
                    if (medicine == null) {
                        return Result.error("药品不存在");
                    }
                    if (medicine.getStock() < quantity) {
                        return Result.error("药品\"" + medicine.getName() + "\"库存不足");
                    }
                    
                    // 扣减库存
                    medicine.setStock(medicine.getStock() - quantity);
                    medicineMapper.updateById(medicine);
                }
            }
            
            // 创建账单
            Bill bill = new Bill();
            bill.setDoctorId(doctorId);
            bill.setPatientId(patientId);
            bill.setRegistrationId(registrationId);
            bill.setDiagnosis(diagnosis);
            bill.setMedicines(medicines != null ? new ObjectMapper().writeValueAsString(medicines) : null);
            bill.setOtherCost(BigDecimal.valueOf(registrationFee)); // 挂号费存到otherCost字段
            bill.setTotalAmount(BigDecimal.valueOf(totalAmount));
            bill.setStatus(0);
            bill.setCreateTime(LocalDateTime.now());
            billMapper.insert(bill);
            
            // 更新挂号状态为已完成
            Registration registration = registrationMapper.selectById(registrationId);
            if (registration != null) {
                registration.setStatus(2); // 已完成
                registrationMapper.updateById(registration);
            }
            
            return Result.success("诊断完成，账单已生成");
        } catch (Exception e) {
            e.printStackTrace();
            return Result.error("操作失败：" + e.getMessage());
        }
    }
}
