package com.itheyi.springbootwebquick.controller;

import com.itheyi.springbootwebquick.Service.MedicalImagePreviewService;
import com.itheyi.springbootwebquick.entity.MedicalImage;
import com.itheyi.springbootwebquick.mapper.MedicalImageRepository;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.CacheControl;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.File;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

@Slf4j
@RestController
@RequestMapping("/api/images")
@CrossOrigin(
        originPatterns = {
                "http://localhost:*",
                "http://127.0.0.1:*",
                "http://47.108.52.206",
                "http://47.108.52.206:*",
                "https://47.108.52.206",
                "https://47.108.52.206:*"
        },
        allowCredentials = "true"
)
public class ImageController {

    @Autowired
    private MedicalImageRepository medicalImageRepository;

    @Autowired
    private MedicalImagePreviewService medicalImagePreviewService;

    @GetMapping("/{imageId}")
    public ResponseEntity<Resource> getImage(@PathVariable Long imageId, HttpServletRequest request) {
        try {
            Optional<MedicalImage> imageOptional = medicalImageRepository.findById(imageId);
            if (imageOptional.isEmpty()) {
                return ResponseEntity.notFound().build();
            }

            MedicalImage image = imageOptional.get();
            File originalFile = new File(image.getFilePath());
            if (!originalFile.exists()) {
                return ResponseEntity.notFound().build();
            }

            File displayFile = medicalImagePreviewService.resolveDisplayFile(originalFile, image.getOriginalFilename());
            String contentType = determineContentType(displayFile.getName(), image.getOriginalFilename());
            String etag = buildEtag(imageId, displayFile);
            String ifNoneMatch = request.getHeader(HttpHeaders.IF_NONE_MATCH);

            if (etag.equals(ifNoneMatch)) {
                return ResponseEntity.status(304)
                        .eTag(etag)
                        .lastModified(displayFile.lastModified())
                        .cacheControl(CacheControl.maxAge(30, TimeUnit.DAYS).cachePublic().immutable())
                        .build();
            }

            Resource resource = new FileSystemResource(displayFile);
            return ResponseEntity.ok()
                    .contentType(MediaType.parseMediaType(contentType))
                    .contentLength(displayFile.length())
                    .cacheControl(CacheControl.maxAge(30, TimeUnit.DAYS).cachePublic().immutable())
                    .eTag(etag)
                    .lastModified(displayFile.lastModified())
                    .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"" + image.getOriginalFilename() + "\"")
                    .body(resource);
        } catch (Exception e) {
            log.error("Failed to get image {}", imageId, e);
            return ResponseEntity.internalServerError().build();
        }
    }

    private String buildEtag(Long imageId, File file) {
        return "\"" + imageId + "-" + file.length() + "-" + file.lastModified() + "\"";
    }

    private String determineContentType(String servedFilename, String originalFilename) {
        String filename = servedFilename != null ? servedFilename : originalFilename;
        if (filename == null) {
            return MediaType.IMAGE_JPEG_VALUE;
        }

        String lowerCaseFilename = filename.toLowerCase();
        if (lowerCaseFilename.endsWith(".png")) {
            return MediaType.IMAGE_PNG_VALUE;
        }
        if (lowerCaseFilename.endsWith(".gif")) {
            return MediaType.IMAGE_GIF_VALUE;
        }
        if (lowerCaseFilename.endsWith(".bmp")) {
            return "image/bmp";
        }
        return MediaType.IMAGE_JPEG_VALUE;
    }
}
