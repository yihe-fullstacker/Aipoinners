package com.itheyi.springbootwebquick.dto;
//验证码服务返回的数据，如 base64 图片、验证码 ID
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CaptchaResponse {
    private String captchaId;
    private String imageBase64;
}


