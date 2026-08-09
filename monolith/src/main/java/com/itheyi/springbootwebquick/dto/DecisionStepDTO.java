package com.itheyi.springbootwebquick.dto;
//表示 AI 决策过程中的每一步（如“是否良性？”），用于展示推理路径
import lombok.Data;

@Data
public class DecisionStepDTO {
    private String stage;
    private String description;
    private Double confidence;
    private Double weight;
}


