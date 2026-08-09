package com.itheyi.springbootwebquick.dto;
//将后端AI分析的数据响应给前端：表示 AI 提取的影像特征（如“边缘模糊”、“密度不均”），用于辅助诊断
import lombok.Data;

@Data
public class FeatureDTO {
    private String type;
    private String name;
    private String description;
    private Double importance;
    private Double confidence;
}


