package com.itheyi.springbootwebquick.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Data;
//前端请求后端发送短信验证码时携带的数据
@Data
public class SendSmsRequest {
    
    @NotBlank(message = "手机号不能为空")
    @Pattern(regexp = "^1[3-9]\\d{9}$", message = "请输入正确的手机号")
    private String phoneNumber;
}
