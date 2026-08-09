package com.itheyi.springbootwebquick.verify;

import com.aliyun.dypnsapi20170525.Client;
import com.aliyun.dypnsapi20170525.models.CheckSmsVerifyCodeRequest;
import com.aliyun.dypnsapi20170525.models.CheckSmsVerifyCodeResponse;
import com.aliyun.teaopenapi.models.Config;
import com.aliyun.teautil.models.RuntimeOptions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
//后端与阿里云交互：阿里云号码认证服务（Dypnsapi）的底层客户端封装类，专门用于调用阿里云的 验证短信验证码 接口（checkSmsVerifyCode）。
@Service
public class VerifySample {
    private static final Logger log = LoggerFactory.getLogger(VerifySample.class);

    @Value("${aliyun.accessKeyId:}")
    private String accessKeyId;

    @Value("${aliyun.accessKeySecret:}")
    private String accessKeySecret;

    public CheckSmsVerifyCodeResponse verifySms(String phoneNumber, String verifyCode) throws Exception {
        validateConfig();

        Client client = createClient();
        CheckSmsVerifyCodeRequest request = new CheckSmsVerifyCodeRequest()
                .setPhoneNumber(phoneNumber)
                .setVerifyCode(verifyCode);

        RuntimeOptions runtime = new RuntimeOptions();
        CheckSmsVerifyCodeResponse response = client.checkSmsVerifyCodeWithOptions(request, runtime);
        String providerCode = response != null && response.getBody() != null
                ? response.getBody().getCode()
                : "unknown";
        String verifyResult = response != null
                && response.getBody() != null
                && response.getBody().getModel() != null
                ? response.getBody().getModel().getVerifyResult()
                : "unknown";
        log.info("[SMS] CheckSmsVerifyCode called, phone: {}, providerCode: {}, verifyResult: {}",
                phoneNumber, providerCode, verifyResult);
        return response;
    }

    private void validateConfig() {
        if (!hasText(accessKeyId) || !hasText(accessKeySecret)) {
            throw new IllegalStateException("Aliyun AccessKey is missing");
        }
    }

    private boolean hasText(String value) {
        return value != null && !value.trim().isEmpty();
    }

    private Client createClient() throws Exception {
        Config config = new Config()
                .setAccessKeyId(accessKeyId)
                .setAccessKeySecret(accessKeySecret);
        config.endpoint = "dypnsapi.aliyuncs.com";
        return new Client(config);
    }
}
