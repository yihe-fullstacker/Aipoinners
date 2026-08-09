package com.itheyi.springbootwebquick.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
//后端返回给前端所有短信相关操作（发送、验证等）的标准响应格式
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SmsResponse {

    private Integer code;
    private String message;
    private Object data;

    public static SmsResponse success() {
        return SmsResponse.builder()
                .code(200)
                .message("ok")
                .build();
    }

    public static SmsResponse successMessage(String message) {
        return SmsResponse.builder()
                .code(200)
                .message(message)
                .build();
    }

    public static SmsResponse success(Object data) {
        return SmsResponse.builder()
                .code(200)
                .message("ok")
                .data(data)
                .build();
    }

    public static SmsResponse success(String message, Object data) {
        return SmsResponse.builder()
                .code(200)
                .message(message)
                .data(data)
                .build();
    }

    public static SmsResponse error(String message) {
        return SmsResponse.builder()
                .code(500)
                .message(message)
                .build();
    }

    public static SmsResponse error(Integer code, String message) {
        return SmsResponse.builder()
                .code(code)
                .message(message)
                .build();
    }
}

