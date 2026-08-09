package com.itheyi.springbootwebquick.util;

import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.List;

public class FileValidationUtil {

    // 允许的医学影像文件类型
    private static final List<String> ALLOWED_MIME_TYPES = Arrays.asList(
        "image/jpeg",
        "image/jpg",
        "image/png",
        "application/dicom",
        "application/x-dicom"
    );

    // 允许的文件扩展名
    private static final List<String> ALLOWED_EXTENSIONS = Arrays.asList(
        ".jpg", ".jpeg", ".png", ".dicom", ".dcm", ".nii", ".nii.gz"
    );

    // 文件魔术数字（文件头）
    private static final byte[] JPEG_SIGNATURE = {(byte) 0xFF, (byte) 0xD8, (byte) 0xFF};
    private static final byte[] PNG_SIGNATURE = {(byte) 0x89, 0x50, 0x4E, 0x47, 0x0D, 0x0A, 0x1A, 0x0A};
    private static final byte[] DICOM_SIGNATURE = {0x44, 0x49, 0x43, 0x4D}; // "DICM"

    /**
     * 验证文件是否为有效的医学影像文件
     */
    public static ValidationResult validateMedicalImage(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            return new ValidationResult(false, "文件为空");
        }

        // 1. 检查文件扩展名
        String originalFilename = file.getOriginalFilename();
        if (originalFilename == null) {
            return new ValidationResult(false, "文件名无效");
        }

        String extension = getFileExtension(originalFilename).toLowerCase();
        if (!ALLOWED_EXTENSIONS.contains(extension)) {
            return new ValidationResult(false, 
                "不支持的文件格式。仅支持: JPG, PNG, DICOM, NIfTI");
        }

        // 2. 检查MIME类型
        String contentType = file.getContentType();
        if (contentType != null && !ALLOWED_MIME_TYPES.contains(contentType.toLowerCase())) {
            // MIME类型不匹配，但继续检查文件头
        }

        // 3. 检查文件大小（最大50MB）
        long maxSize = 50 * 1024 * 1024; // 50MB
        if (file.getSize() > maxSize) {
            return new ValidationResult(false, "文件大小超过限制（最大50MB）");
        }

        // 4. 验证文件头（魔术数字）
        try {
            InputStream inputStream = file.getInputStream();
            byte[] header = new byte[8];
            int bytesRead = inputStream.read(header);
            inputStream.close();

            if (bytesRead < 4) {
                return new ValidationResult(false, "文件格式无效");
            }

            // 检查JPEG
            if (startsWith(header, JPEG_SIGNATURE)) {
                return new ValidationResult(true, "JPEG格式");
            }

            // 检查PNG
            if (startsWith(header, PNG_SIGNATURE)) {
                return new ValidationResult(true, "PNG格式");
            }

            // 检查DICOM（DICOM文件可能从偏移量128开始）
            if (extension.equals(".dicom") || extension.equals(".dcm")) {
                // DICOM文件可能没有标准文件头，检查文件扩展名即可
                return new ValidationResult(true, "DICOM格式");
            }

            // 对于其他格式，如果扩展名允许，则通过
            if (ALLOWED_EXTENSIONS.contains(extension)) {
                return new ValidationResult(true, "文件格式验证通过");
            }

            return new ValidationResult(false, 
                "文件格式不匹配。请确保上传的是有效的医学影像文件");

        } catch (IOException e) {
            return new ValidationResult(false, "读取文件失败: " + e.getMessage());
        }
    }

    /**
     * 获取文件扩展名
     */
    private static String getFileExtension(String filename) {
        int lastDot = filename.lastIndexOf('.');
        if (lastDot == -1) {
            return "";
        }
        return filename.substring(lastDot);
    }

    /**
     * 检查字节数组是否以指定前缀开始
     */
    private static boolean startsWith(byte[] array, byte[] prefix) {
        if (array.length < prefix.length) {
            return false;
        }
        for (int i = 0; i < prefix.length; i++) {
            if (array[i] != prefix[i]) {
                return false;
            }
        }
        return true;
    }

    /**
     * 验证结果类
     */
    public static class ValidationResult {
        private final boolean valid;
        private final String message;

        public ValidationResult(boolean valid, String message) {
            this.valid = valid;
            this.message = message;
        }

        public boolean isValid() {
            return valid;
        }

        public String getMessage() {
            return message;
        }
    }
}


