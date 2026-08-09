package com.itheyi.springbootwebquick.send;

import com.aliyun.dypnsapi20170525.Client;
import com.aliyun.dypnsapi20170525.models.SendSmsVerifyCodeRequest;
import com.aliyun.dypnsapi20170525.models.SendSmsVerifyCodeResponse;
import com.aliyun.teaopenapi.models.Config;
import com.aliyun.teautil.models.RuntimeOptions;
import com.google.gson.Gson;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.LinkedHashMap;
import java.util.Map;
//阿里云号码认证服务的发送端客户端封装，负责调用阿里云 sendSmsVerifyCode 接口，向指定的手机号发送短信验证码。
@Service
public class Sample {
    private static final Logger log = LoggerFactory.getLogger(Sample.class);
    private static final String GENERATED_CODE_PLACEHOLDER = "##code##";
    private static final String FIXED_TEMPLATE_CODE = "100001";

    @Value("${aliyun.accessKeyId:}")
    private String accessKeyId;

    @Value("${aliyun.accessKeySecret:}")
    private String accessKeySecret;

    @Value("${aliyun.signName:}")
    private String signName;

    @Value("${aliyun.templateCode:100001}")
    private String templateCode;

    @Value("${aliyun.sms.codeType:1}")
    private Long codeType;

    @Value("${aliyun.sms.codeLength:6}")
    private Long codeLength;

    @Value("${aliyun.sms.validTime:300}")
    private Long validTime;

    @Value("${aliyun.sms.interval:60}")
    private Long interval;

    private final Gson gson = new Gson();

    public SendSmsVerifyCodeResponse sendSms(String phoneNumber) throws Exception {
        validateConfig();

        Client client = createClient();
        String templateParam = buildTemplateParam();

        log.info("[SMS] SendSmsVerifyCode request, phone: {}, signName: {}, templateCode: {}, templateParam: {}",
                phoneNumber, signName, templateCode, templateParam);

        SendSmsVerifyCodeRequest request = new SendSmsVerifyCodeRequest()
                .setPhoneNumber(phoneNumber)
                .setSignName(signName)
                .setTemplateCode(templateCode)
                .setTemplateParam(templateParam)
                .setCodeType(codeType)
                .setCodeLength(codeLength)
                .setValidTime(validTime)
                .setInterval(interval);

        RuntimeOptions runtime = new RuntimeOptions();
        SendSmsVerifyCodeResponse response = client.sendSmsVerifyCodeWithOptions(request, runtime);
        var body = response != null ? response.getBody() : null;

        log.info("[SMS] SendSmsVerifyCode called, phone: {}, requestId: {}, providerCode: {}, providerMessage: {}",
                phoneNumber,
                body != null ? body.getRequestId() : "unknown",
                body != null ? body.getCode() : "unknown",
                body != null ? body.getMessage() : "unknown");

        return response;
    }

    String buildTemplateParam() {
        long effectiveValidTime = validTime != null && validTime > 0 ? validTime : 300L;
        long minutes = Math.max(1L, (effectiveValidTime + 59L) / 60L);

        Map<String, String> params = new LinkedHashMap<>();
        params.put("code", GENERATED_CODE_PLACEHOLDER);
        params.put("min", String.valueOf(minutes));
        return gson.toJson(params);
    }

    private void validateConfig() {
        if (!hasText(accessKeyId) || !hasText(accessKeySecret)) {
            throw new IllegalStateException("Aliyun AccessKey is missing");
        }
        if (!hasText(signName) || !hasText(templateCode)) {
            throw new IllegalStateException("Aliyun SMS signName/templateCode is missing");
        }
        if (!FIXED_TEMPLATE_CODE.equals(templateCode)) {
            throw new IllegalStateException("Aliyun PNVS gifted templateCode must be 100001");
        }
        if (codeLength != null && (codeLength < 4 || codeLength > 8)) {
            throw new IllegalStateException("Aliyun sms codeLength must be between 4 and 8");
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
