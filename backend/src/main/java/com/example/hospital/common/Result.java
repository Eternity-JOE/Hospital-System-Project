package com.example.hospital.common;
import lombok.Data;

@Data
public class Result<T> {
    private String code; // "200"成功, "500"失败
    private String msg;
    private T data;

    public static <T> Result<T> success(T data) {
        Result<T> r = new Result<>();
        r.setCode("200");
        r.setMsg("success");
        r.setData(data);
        return r;
    }

    public static <T> Result<T> error(String msg) {
        Result<T> r = new Result<>();
        r.setCode("500");
        r.setMsg(msg);
        return r;
    }
}