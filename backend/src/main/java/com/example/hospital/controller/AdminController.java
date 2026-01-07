package com.example.hospital.controller;

import com.example.hospital.common.Result;
import com.example.hospital.entity.User;
import com.example.hospital.mapper.UserMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/admin")
public class AdminController {

    @Autowired
    private UserMapper userMapper;

    // 获取管理员列表
    @GetMapping("/list")
    public Result<List<User>> list() {
        List<User> admins = userMapper.findAllAdmins();
        return Result.success(admins);
    }

    // 新增管理员
    @PostMapping
    public Result<String> add(@RequestBody Map<String, String> data) {
        String username = data.get("username");
        String password = data.get("password");

        if (username == null || password == null || username.isEmpty() || password.isEmpty()) {
            return Result.error("账号和密码不能为空");
        }

        // 检查用户名是否已存在
        User existUser = userMapper.findByUsername(username);
        if (existUser != null) {
            return Result.error("该账号已被使用");
        }

        int result = userMapper.insertUser(username, password, "admin", null);
        if (result > 0) {
            return Result.success("新增成功");
        } else {
            return Result.error("新增失败");
        }
    }

    // 删除管理员
    @DeleteMapping("/{id}")
    public Result<String> delete(@PathVariable Integer id) {
        User user = userMapper.findById(id);
        if (user != null && "admin".equals(user.getUsername())) {
            return Result.error("默认管理员账号不能删除");
        }
        
        int result = userMapper.deleteById(id);
        if (result > 0) {
            return Result.success("删除成功");
        } else {
            return Result.error("删除失败");
        }
    }
}
