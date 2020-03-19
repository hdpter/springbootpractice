package com.xxx.demo.configurer.core;

import com.alibaba.fastjson.JSON;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * 统一API响应结果封装
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Result<T> implements Serializable {

    private static final String DEFAULT_SUCCESS_MESSAGE = "SUCCESS";

    private int code;
    private String message;
    private T data;

    public T getData() {
        return data;
    }

    public Result setData(T data) {
        this.data = data;
        return this;
    }

    public Result setCode(ResultCode resultCode) {
        this.code = resultCode.code();
        return this;
    }


    public static Result genSuccessResult() {
        return Result.builder()
                .code(ResultCode.SUCCESS.code())
                .message(DEFAULT_SUCCESS_MESSAGE)
                .build();
    }

    public static <T> Result<T> genSuccessResult(T data) {
        return Result.builder()
                .code(ResultCode.SUCCESS.code())
                .message(DEFAULT_SUCCESS_MESSAGE)
                .build()
                .setData(data);
    }

    public static Result genFailResult(String message) {
        return Result.builder()
                .code(ResultCode.FAIL.code())
                .message(message).build();
    }

    @Override
    public String toString() {
        return JSON.toJSONString(this);
    }

    public static enum ResultCode {
        SUCCESS(200),//成功
        FAIL(400),//失败
        UNAUTHORIZED(401),//未认证（签名错误）
        NOT_FOUND(404),//接口不存在
        INTERNAL_SERVER_ERROR(500);//服务器内部错误

        private final int code;

        ResultCode(int code) {
            this.code = code;
        }

        public int code() {
            return code;
        }
    }


}
