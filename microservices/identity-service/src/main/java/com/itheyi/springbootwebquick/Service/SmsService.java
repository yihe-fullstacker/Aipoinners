package com.itheyi.springbootwebquick.Service;

import com.aliyun.dypnsapi20170525.models.CheckSmsVerifyCodeResponse;
import com.aliyun.dypnsapi20170525.models.SendSmsVerifyCodeResponse;
import com.itheyi.springbootwebquick.dto.SmsResponse;
import com.itheyi.springbootwebquick.mapper.UserRepository;
import com.itheyi.springbootwebquick.send.Sample;
import com.itheyi.springbootwebquick.verify.VerifySample;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class SmsService {
    private static final Logger log = LoggerFactory.getLogger(SmsService.class);

    @Value("${aliyun.accessKeyId:}")
    private String accessKeyId;

    @Value("${aliyun.accessKeySecret:}")
    private String accessKeySecret;

    private final Sample smsSendService;
    private final VerifySample smsVerifyService;
    private final UserRepository userRepository;

    private static final Map<String, String> CODE_CACHE = new ConcurrentHashMap<>();
    private static final Map<String, Long> SEND_TIME_CACHE = new ConcurrentHashMap<>();

    private static final long SMS_CODE_EXPIRE_MINUTES = 5;
    private static final long SMS_SEND_INTERVAL_SECONDS = 60;

    private final Random random = new Random();

    public SmsService(Sample smsSendService, VerifySample smsVerifyService, UserRepository userRepository) {
        this.smsSendService = smsSendService;
        this.smsVerifyService = smsVerifyService;
        this.userRepository = userRepository;
    }

    public SmsResponse sendSms(String phoneNumber) {
        String normalizedPhone = normalizePhone(phoneNumber);
        log.info("[SMS Service] send request, phone: {}", normalizedPhone);

        if (!userRepository.existsByPhone(normalizedPhone)) {
            return SmsResponse.error(404, "该手机号未注册，无法发送短信验证码");
        }

        long nowSeconds = System.currentTimeMillis() / 1000;
        Long lastSendTime = SEND_TIME_CACHE.get(normalizedPhone);
        if (lastSendTime != null && nowSeconds - lastSendTime < SMS_SEND_INTERVAL_SECONDS) {
            long remaining = SMS_SEND_INTERVAL_SECONDS - (nowSeconds - lastSendTime);
            return SmsResponse.error(429, "短信发送过于频繁，请在 " + remaining + " 秒后重试");
        }

        if (isAliyunEnabled()) {
            return sendByAliyun(normalizedPhone, nowSeconds);
        }

        return sendByLocalFallback(normalizedPhone, nowSeconds);
    }

    public SmsResponse verifySms(String phoneNumber, String verifyCode) {
        String normalizedPhone = normalizePhone(phoneNumber);
        log.info("[SMS Service] verify request, phone: {}", normalizedPhone);

        if (!userRepository.existsByPhone(normalizedPhone)) {
            return SmsResponse.error(404, "该手机号未注册");
        }

        if (isAliyunEnabled()) {
            return verifyByAliyun(normalizedPhone, verifyCode);
        }

        return verifyByLocalFallback(normalizedPhone, verifyCode);
    }

    private SmsResponse sendByAliyun(String phoneNumber, long nowSeconds) {
        try {
            SendSmsVerifyCodeResponse response = smsSendService.sendSms(phoneNumber);
            var body = response != null ? response.getBody() : null;

            String providerCode = body != null ? body.getCode() : null;
            String providerMessage = body != null ? body.getMessage() : null;
            Boolean providerSuccess = body != null ? body.getSuccess() : null;

            if (!isProviderCallSuccess(providerCode, providerSuccess)) {
                if (isProviderFrequencyLimited(providerCode, providerMessage)) {
                    SEND_TIME_CACHE.put(phoneNumber, nowSeconds);
                }
                log.warn("[SMS Service] Aliyun send failed, code: {}, message: {}", providerCode, providerMessage);
                return mapAliyunSendError(providerCode, providerMessage);
            }

            SEND_TIME_CACHE.put(phoneNumber, nowSeconds);
            return SmsResponse.success("验证码已发送", body != null ? body.getRequestId() : null);
        } catch (Exception e) {
            log.error("[SMS Service] Aliyun send exception, phone: {}", phoneNumber, e);
            return SmsResponse.error(500, "验证码发送失败，请稍后重试");
        }
    }

    private SmsResponse verifyByAliyun(String phoneNumber, String verifyCode) {
        try {
            CheckSmsVerifyCodeResponse response = smsVerifyService.verifySms(phoneNumber, verifyCode);
            var body = response != null ? response.getBody() : null;

            String providerCode = body != null ? body.getCode() : null;
            String providerMessage = body != null ? body.getMessage() : null;
            Boolean providerSuccess = body != null ? body.getSuccess() : null;

            if (!isProviderCallSuccess(providerCode, providerSuccess)) {
                log.warn("[SMS Service] Aliyun verify failed, code: {}, message: {}", providerCode, providerMessage);
                return mapAliyunVerifyError(providerCode, providerMessage);
            }

            String verifyResult = body != null && body.getModel() != null
                    ? body.getModel().getVerifyResult()
                    : null;

            if (!"PASS".equalsIgnoreCase(verifyResult)) {
                log.warn("[SMS Service] Aliyun verify result is not PASS, phone: {}, result: {}", phoneNumber, verifyResult);
                return SmsResponse.error(400, "验证码错误或已过期");
            }

            return SmsResponse.successMessage("验证码验证通过");
        } catch (Exception e) {
            log.error("[SMS Service] Aliyun verify exception, phone: {}", phoneNumber, e);
            return SmsResponse.error(500, "验证码校验失败，请稍后重试");
        }
    }

    private SmsResponse sendByLocalFallback(String phoneNumber, long nowSeconds) {
        String code = generateRandomCode();
        CODE_CACHE.put(phoneNumber, code);
        SEND_TIME_CACHE.put(phoneNumber, nowSeconds);
        startCleanupThread(phoneNumber);
        log.warn("[SMS Service] Aliyun key is not configured, local fallback code for {} is {}", phoneNumber, code);
        return SmsResponse.successMessage("验证码已发送（开发模式验证码：" + code + "）");
    }

    private SmsResponse verifyByLocalFallback(String phoneNumber, String verifyCode) {
        String storedCode = CODE_CACHE.get(phoneNumber);
        if (!hasText(storedCode)) {
            return SmsResponse.error(400, "验证码已过期，请重新获取");
        }

        if (!storedCode.equals(verifyCode)) {
            return SmsResponse.error(400, "验证码不正确");
        }

        CODE_CACHE.remove(phoneNumber);
        return SmsResponse.successMessage("验证码验证通过");
    }

    private String generateRandomCode() {
        return String.valueOf(100000 + random.nextInt(900000));
    }

    private void startCleanupThread(String phoneNumber) {
        new Thread(() -> {
            try {
                Thread.sleep(SMS_CODE_EXPIRE_MINUTES * 60 * 1000);
                CODE_CACHE.remove(phoneNumber);
                log.info("[SMS Service] local code expired, phone: {}", phoneNumber);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }, "sms-code-cleanup-" + phoneNumber).start();
    }

    private boolean isAliyunEnabled() {
        return hasText(accessKeyId) && hasText(accessKeySecret);
    }

    private boolean hasText(String value) {
        return value != null && !value.trim().isEmpty();
    }

    private String normalizePhone(String phoneNumber) {
        return phoneNumber == null ? null : phoneNumber.trim();
    }

    private boolean isProviderCallSuccess(String providerCode, Boolean providerSuccess) {
        if (!"OK".equalsIgnoreCase(providerCode)) {
            return false;
        }
        return providerSuccess == null || providerSuccess;
    }

    private SmsResponse mapAliyunSendError(String providerCode, String providerMessage) {
        String normalizedCode = normalizeProviderCode(providerCode);

        if (isProviderFrequencyLimited(providerCode, providerMessage)) {
            return SmsResponse.error(429, "短信发送过于频繁，请 60 秒后重试");
        }

        if (isProviderInvalidSignOrTemplate(providerCode, providerMessage)) {
            return SmsResponse.error(500, "阿里云短信签名或模板配置无效，请检查后台配置");
        }

        if (isProviderInvalidTemplateParam(normalizedCode, providerMessage)) {
            return SmsResponse.error(500, "阿里云短信模板参数无效，请检查模板变量");
        }

        if ("MOBILE_NUMBER_ILLEGAL".equalsIgnoreCase(normalizedCode)) {
            return SmsResponse.error(400, "手机号格式不正确");
        }

        if (hasText(providerMessage)) {
            return SmsResponse.error(500, "验证码发送失败：" + providerMessage);
        }
        return SmsResponse.error(500, "验证码发送失败");
    }

    private SmsResponse mapAliyunVerifyError(String providerCode, String providerMessage) {
        String normalizedCode = normalizeProviderCode(providerCode);
        if ("MOBILE_NUMBER_ILLEGAL".equalsIgnoreCase(normalizedCode)) {
            return SmsResponse.error(400, "手机号格式不正确");
        }

        if (hasText(providerMessage)) {
            return SmsResponse.error(400, "验证码校验失败：" + providerMessage);
        }
        return SmsResponse.error(400, "验证码校验失败");
    }

    private boolean isProviderFrequencyLimited(String providerCode, String providerMessage) {
        String normalizedCode = normalizeProviderCode(providerCode);
        if ("FREQUENCY_FAIL".equalsIgnoreCase(normalizedCode) || "BUSINESS_LIMIT_CONTROL".equalsIgnoreCase(normalizedCode)) {
            return true;
        }
        return hasText(providerMessage) && providerMessage.toLowerCase().contains("check frequency fail");
    }

    private boolean isProviderInvalidSignOrTemplate(String providerCode, String providerMessage) {
        String normalizedCode = normalizeProviderCode(providerCode);
        if (!"INVALID_PARAMETERS".equalsIgnoreCase(normalizedCode) || !hasText(providerMessage)) {
            return false;
        }
        String lower = providerMessage.toLowerCase();
        return lower.contains("sign") || lower.contains("template") || providerMessage.contains("签名") || providerMessage.contains("模板");
    }

    private boolean isProviderInvalidTemplateParam(String normalizedCode, String providerMessage) {
        if (!"INVALID_PARAMETERS".equalsIgnoreCase(normalizedCode) || !hasText(providerMessage)) {
            return false;
        }
        return providerMessage.contains("模板变量")
                || providerMessage.contains("TemplateParam")
                || providerMessage.toLowerCase().contains("template param");
    }

    private String normalizeProviderCode(String providerCode) {
        if (!hasText(providerCode)) {
            return "";
        }
        String code = providerCode.trim();
        int dotIndex = code.lastIndexOf('.');
        if (dotIndex >= 0 && dotIndex < code.length() - 1) {
            return code.substring(dotIndex + 1);
        }
        return code;
    }
}