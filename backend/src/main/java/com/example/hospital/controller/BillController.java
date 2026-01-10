package com.example.hospital.controller;

import com.example.hospital.common.Result;
import com.example.hospital.entity.Bill;
import com.example.hospital.mapper.BillMapper;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.time.LocalDateTime;
import java.util.List;
import java.math.BigDecimal;

@RestController
@RequestMapping("/bill")
public class BillController {

    @Autowired
    private BillMapper billMapper;

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
        wrapper.eq(Bill::getPatientId, patientId);
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
}
