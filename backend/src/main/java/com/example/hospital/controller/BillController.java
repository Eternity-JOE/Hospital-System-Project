package com.example.hospital.controller;

import com.example.hospital.common.Result;
import com.example.hospital.entity.Bill;
import com.example.hospital.mapper.BillMapper;
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

    // 查询所有账单
    @GetMapping("/list")
    public Result<?> list() {
        List<Bill> list = billMapper.selectList(null);
        return Result.success(list);
    }

    // 新增账单（缴费单生成）
    @PostMapping
    public Result<?> save(@RequestBody Bill bill) {
        bill.setCreateTime(LocalDateTime.now());
        billMapper.insert(bill);
        return Result.success(null);
    }

    // 确认缴费（修改状态）
    @PutMapping("/pay/{id}")
    public Result<?> pay(@PathVariable Integer id) {
        Bill bill = billMapper.selectById(id);
        if (bill != null) {
            bill.setStatus(1); // 设为已支付
            bill.setPayTime(LocalDateTime.now());
            billMapper.updateById(bill);
            return Result.success("支付成功");
        }
        return Result.error("未找到账单");
    }

    // 在 BillController.java 中添加
    @GetMapping("/total-revenue")
    public Result<?> getTotalRevenue() {
        List<Bill> list = billMapper.selectList(null);
        BigDecimal total = list.stream()
            .filter(b -> b.getStatus() == 1) // 只计算已支付的
            .map(Bill::getTotalAmount)
            .reduce(BigDecimal.ZERO, BigDecimal::add);
        return Result.success(total);
    }
}