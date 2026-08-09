package com.itheyi.springbootwebquick.dto;


//报告摘要信息，简洁版的分析结果，用于首页或列表页展示,将后端数据响应给前端
import lombok.Data;
import java.time.LocalDateTime;

@Data
public class ReportSummaryDTO {
    private Long id;
    private LocalDateTime date;
    private String imageUrl;
    private Double confidence;
    private Integer findingsCount;
}