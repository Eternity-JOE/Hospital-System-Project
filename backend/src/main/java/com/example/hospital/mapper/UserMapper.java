package com.example.hospital.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.hospital.entity.User;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Update;
import org.apache.ibatis.annotations.Delete;
import java.util.List;

@Mapper
public interface UserMapper extends BaseMapper<User> {
    
    @Select("SELECT * FROM sys_user WHERE username = #{username} AND password = #{password} AND role = #{role} AND status = 1")
    User login(@Param("username") String username, @Param("password") String password, @Param("role") String role);
    
    @Select("SELECT * FROM sys_user WHERE username = #{username}")
    User findByUsername(@Param("username") String username);
    
    @Insert("INSERT INTO sys_user (username, password, role, status) VALUES (#{username}, #{password}, 'patient', 1)")
    int registerPatient(@Param("username") String username, @Param("password") String password);
    
    @Select("SELECT * FROM sys_user WHERE id = #{id}")
    User findById(@Param("id") Integer id);
    
    @Update("UPDATE sys_user SET ref_id = #{refId} WHERE id = #{id}")
    int updateRefId(@Param("id") Integer id, @Param("refId") Integer refId);
    
    @Insert("INSERT INTO sys_user (username, password, role, ref_id, status) VALUES (#{username}, #{password}, #{role}, #{refId}, 1)")
    int insertUser(@Param("username") String username, @Param("password") String password, @Param("role") String role, @Param("refId") Integer refId);
    
    @Select("SELECT * FROM sys_user WHERE role = 'admin'")
    List<User> findAllAdmins();
    
    @Delete("DELETE FROM sys_user WHERE id = #{id}")
    int deleteById(@Param("id") Integer id);
}
