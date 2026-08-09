/*这块代码是验证码生成和验证服务*/
package com.itheyi.springbootwebquick.Service;

import com.itheyi.springbootwebquick.dto.CaptchaResponse;
import org.springframework.stereotype.Service;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class CaptchaService {

    // 存储验证码，key为captchaId，value为验证码文本
    private final Map<String, String> captchaStore = new ConcurrentHashMap<>();
    private final Random random = new Random();
    
    // 验证码过期时间（5分钟）
    private static final long CAPTCHA_EXPIRE_TIME = 5 * 60 * 1000;
    
    // 存储验证码创建时间
    private final Map<String, Long> captchaTimeStore = new ConcurrentHashMap<>();

    /**
     * 生成验证码
     */
    public CaptchaResponse generateCaptcha() {
        String captchaId = UUID.randomUUID().toString();
        String code = generateRandomCode();
        
        // 存储验证码和创建时间
        captchaStore.put(captchaId, code);
        captchaTimeStore.put(captchaId, System.currentTimeMillis());
        
        // 生成验证码图片
        String imageBase64 = generateCaptchaImage(code);
        
        return new CaptchaResponse(captchaId, imageBase64);
    }

    /**
     * 验证验证码
     */
    public boolean validateCaptcha(String captchaId, String code) {
        if (captchaId == null || code == null) {
            return false;
        }
        
        // 检查验证码是否存在
        String storedCode = captchaStore.get(captchaId);
        if (storedCode == null) {
            return false;
        }
        
        // 检查是否过期
        Long createTime = captchaTimeStore.get(captchaId);
        if (createTime == null || System.currentTimeMillis() - createTime > CAPTCHA_EXPIRE_TIME) {
            // 清理过期的验证码
            captchaStore.remove(captchaId);
            captchaTimeStore.remove(captchaId);
            return false;
        }
        
        // 验证码不区分大小写
        boolean isValid = storedCode.equalsIgnoreCase(code);
        
        // 验证后删除验证码（一次性使用）
        if (isValid) {
            captchaStore.remove(captchaId);
            captchaTimeStore.remove(captchaId);
        }
        
        return isValid;
    }

    /**
     * 生成随机验证码（4位数字+字母）
     */
    private String generateRandomCode() {
        String chars = "ABCDEFGHJKLMNPQRSTUVWXYZ23456789"; // 排除容易混淆的字符
        StringBuilder code = new StringBuilder();
        for (int i = 0; i < 4; i++) {
            code.append(chars.charAt(random.nextInt(chars.length())));
        }
        return code.toString();
    }

    /**
     * 生成验证码图片
     */
    private String generateCaptchaImage(String code) {
        int width = 120;
        int height = 40;
        
        BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        Graphics2D g = image.createGraphics();
        
        // 设置抗锯齿
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        
        // 绘制背景
        g.setColor(Color.WHITE);
        g.fillRect(0, 0, width, height);
        
        // 绘制干扰线
        g.setColor(new Color(200, 200, 200));
        for (int i = 0; i < 5; i++) {
            int x1 = random.nextInt(width);
            int y1 = random.nextInt(height);
            int x2 = random.nextInt(width);
            int y2 = random.nextInt(height);
            g.drawLine(x1, y1, x2, y2);
        }
        
        // 绘制验证码文字
        g.setFont(new Font("Arial", Font.BOLD, 24));
        for (int i = 0; i < code.length(); i++) {
            // 随机颜色
            g.setColor(new Color(
                random.nextInt(100) + 50,
                random.nextInt(100) + 50,
                random.nextInt(100) + 50
            ));
            
            // 随机位置
            int x = 20 + i * 25 + random.nextInt(5);
            int y = 25 + random.nextInt(10);
            
            // 随机旋转角度
            double angle = (random.nextDouble() - 0.5) * 0.3;
            g.rotate(angle, x, y);
            g.drawString(String.valueOf(code.charAt(i)), x, y);
            g.rotate(-angle, x, y);
        }
        
        g.dispose();
        
        // 转换为Base64
        try {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            ImageIO.write(image, "png", baos);
            byte[] imageBytes = baos.toByteArray();
            return "data:image/png;base64," + Base64.getEncoder().encodeToString(imageBytes);
        } catch (IOException e) {
            throw new RuntimeException("生成验证码图片失败", e);
        }
    }
    
    /**
     * 定期清理过期验证码（可选，由定时任务调用）
     */
    public void cleanExpiredCaptchas() {
        long currentTime = System.currentTimeMillis();
        captchaTimeStore.entrySet().removeIf(entry -> {
            if (currentTime - entry.getValue() > CAPTCHA_EXPIRE_TIME) {
                captchaStore.remove(entry.getKey());
                return true;
            }
            return false;
        });
    }
}


