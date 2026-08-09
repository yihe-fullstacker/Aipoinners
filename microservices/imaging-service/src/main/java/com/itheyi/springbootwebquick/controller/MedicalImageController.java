package com.itheyi.springbootwebquick.controller;

//这段代码完整实现了患者端的“AI 医学影像分析一站式服务”并将分析结果返回个前端？
import com.itheyi.springbootwebquick.Jwt.JwtUtil;
import com.itheyi.springbootwebquick.Service.MedicalImageService;
import com.itheyi.springbootwebquick.Service.PythonAIService;
import com.itheyi.springbootwebquick.dto.AnalysisResultDTO;
import com.itheyi.springbootwebquick.dto.ReportSummaryDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import jakarta.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/medical")
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
public class MedicalImageController {

    @Autowired
    private MedicalImageService medicalImageService;

    @Autowired
    private PythonAIService pythonAIService;

    @Autowired
    private JwtUtil jwtUtil;

    @GetMapping("/python/status")
    public ResponseEntity<Map<String, Object>> getPythonServiceStatus() {
        return ResponseEntity.ok(pythonAIService.getServiceStatus());
    }

    @PostMapping("/python/evaluate-model")
    public ResponseEntity<Map<String, Object>> evaluatePythonModel(@RequestBody Map<String, Object> payload) {
        return ResponseEntity.ok(pythonAIService.forwardJsonRequest("/evaluate_model", payload));
    }

    @PostMapping("/python/compare-models")
    public ResponseEntity<Map<String, Object>> comparePythonModels(@RequestBody Map<String, Object> payload) {
        return ResponseEntity.ok(pythonAIService.forwardJsonRequest("/compare_models", payload));
    }

    @PostMapping("/python/explain-prediction")
    public ResponseEntity<Map<String, Object>> explainPythonPrediction(@RequestBody Map<String, Object> payload) {
        return ResponseEntity.ok(pythonAIService.forwardJsonRequest("/explain_prediction", payload));
    }

    @PostMapping("/python/uncertain-cases")
    public ResponseEntity<Map<String, Object>> getPythonUncertainCases(@RequestBody Map<String, Object> payload) {
        return ResponseEntity.ok(pythonAIService.forwardJsonRequest("/get_uncertain_cases", payload));
    }

    @PostMapping(value = "/python/heatmap", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<Map<String, Object>> generatePythonHeatmap(@RequestParam("image") MultipartFile imageFile) {
        return ResponseEntity.ok(pythonAIService.generateHeatmap(imageFile));
    }

    @PostMapping(value = "/python/heatmap", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Map<String, Object>> generatePythonHeatmapFromJson(@RequestBody Map<String, Object> payload) {
        return ResponseEntity.ok(pythonAIService.forwardJsonRequest("/heatmap", payload));
    }

    @PostMapping("/analyze")
    public ResponseEntity<?> analyzeImage(
            @RequestParam("image") MultipartFile imageFile,
            @RequestParam(value = "complaint", required = false) String complaint,
            HttpServletRequest request) {

        try {
            Long userId = getUserIdFromToken(request);
            AnalysisResultDTO result = medicalImageService.analyzeAndSave(imageFile, userId, complaint);
            return ResponseEntity.ok(result);
        } catch (RuntimeException e) {
            // 文件验证失败或其他业务逻辑错误
            if (e.getMessage().contains("不支持") || e.getMessage().contains("格式") || 
                e.getMessage().contains("文件大小") || e.getMessage().contains("文件为空")) {
                return ResponseEntity.badRequest().body("{\"error\":\"" + e.getMessage() + "\"}");
            }
            if (e.getMessage().contains("认证") || e.getMessage().contains("token")) {
                return ResponseEntity.status(401).body("{\"error\":\"" + e.getMessage() + "\"}");
            }
            return ResponseEntity.badRequest().body("{\"error\":\"" + e.getMessage() + "\"}");
        } catch (IOException e) {
            return ResponseEntity.badRequest().body("{\"error\":\"文件处理失败: " + e.getMessage() + "\"}");
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("{\"error\":\"服务器错误: " + e.getMessage() + "\"}");
        }
    }

    /**
     * 医疗影像分析接口 - 调用Deepseek AI模型
     * （3）代码注释中已标明AI辅助编写部分
     *
     * @author 人工编写 + AI辅助
     * @date 2025
     */
    @PostMapping("/analyze-deepseek")
    public ResponseEntity<?> analyzeImageDeepseek(
            @RequestParam("image") MultipartFile imageFile,
            HttpServletRequest request) {

        try {
            // 人工编写：从请求token中获取用户ID
            Long userId = getUserIdFromToken(request);

            // ======================== AI辅助编写核心业务逻辑 ========================
            // AI辅助：调用Deepseek大模型分析影像并保存分析结果
            AnalysisResultDTO result = medicalImageService.analyzeAndSaveWithDeepseek(imageFile, userId);
            // ======================================================================

            return ResponseEntity.ok(result);
        } catch (RuntimeException e) {
            // ======================== AI辅助编写：异常分类处理逻辑 ========================
            // AI辅助：根据异常信息分类返回不同HTTP状态码（文件格式/大小/空文件校验）
            if (e.getMessage().contains("不支持") || e.getMessage().contains("格式") ||
                    e.getMessage().contains("文件大小") || e.getMessage().contains("文件为空")) {
                return ResponseEntity.badRequest().body("{\"error\":\"" + e.getMessage() + "\"}");
            }

            // AI辅助：认证/Token异常返回401未授权
            if (e.getMessage().contains("认证") || e.getMessage().contains("token")) {
                return ResponseEntity.status(401).body("{\"error\":\"" + e.getMessage() + "\"}");
            }

            // AI辅助：其他运行时异常统一返回400
            return ResponseEntity.badRequest().body("{\"error\":\"" + e.getMessage() + "\"}");
            // ==========================================================================
        } catch (IOException e) {
            // AI辅助：文件IO异常处理
            return ResponseEntity.badRequest().body("{\"error\":\"文件处理失败: " + e.getMessage() + "\"}");
        } catch (Exception e) {
            // 人工编写：兜底服务器异常
            return ResponseEntity.internalServerError().body("{\"error\":\"服务器错误: " + e.getMessage() + "\"}");
        }
    }
    @GetMapping("/reports")
    public ResponseEntity<?> getUserReports(HttpServletRequest request) {
        try {
            Long userId = getUserIdFromToken(request);
            List<ReportSummaryDTO> reports = medicalImageService.getUserReports(userId);
            return ResponseEntity.ok(reports);
        } catch (RuntimeException e) {
            return ResponseEntity.status(401).body("认证失败: " + e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("获取报告失败: " + e.getMessage());
        }
    }

    @GetMapping("/reports/{reportId}")
    public ResponseEntity<?> getReportDetail(
            @PathVariable Long reportId,
            HttpServletRequest request) {

        try {
            Long userId = getUserIdFromToken(request);
            AnalysisResultDTO report = medicalImageService.getReportDetail(reportId, userId);
            return ResponseEntity.ok(report);
        } catch (RuntimeException e) {
            return ResponseEntity.status(404).body("报告不存在或无权访问: " + e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("获取报告详情失败: " + e.getMessage());
        }
    }

    @GetMapping("/reports/{reportId}/heatmap")
    public ResponseEntity<?> getReportHeatmap(
            @PathVariable Long reportId,
            HttpServletRequest request) {
        try {
            Long userId = getUserIdFromToken(request);
            Map<String, Object> heatmap = medicalImageService.getReportHeatmap(reportId, userId);
            return ResponseEntity.ok(heatmap);
        } catch (RuntimeException e) {
            return ResponseEntity.status(404).body("报告不存在或无权访问: " + e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("获取热力图失败: " + e.getMessage());
        }
    }

    @DeleteMapping("/reports/{reportId}")
    public ResponseEntity<?> deleteReport(
            @PathVariable Long reportId,
            HttpServletRequest request) {

        try {
            Long userId = getUserIdFromToken(request);
            boolean deleted = medicalImageService.deleteReport(reportId, userId);
            if (deleted) {
                return ResponseEntity.ok().body("报告删除成功");
            } else {
                return ResponseEntity.status(404).body("报告不存在或无权删除");
            }
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("删除报告失败: " + e.getMessage());
        }
    }

    private Long getUserIdFromToken(HttpServletRequest request) {
        String authHeader = request.getHeader("Authorization");
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            throw new RuntimeException("未提供有效的认证token");
        }

        String token = authHeader.substring(7);
        if (!jwtUtil.validateToken(token)) {
            throw new RuntimeException("token无效或已过期");
        }

        return jwtUtil.getUserIdFromToken(token);
    }
}
