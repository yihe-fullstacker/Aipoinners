package com.itheyi.springbootwebquick.dto;
//差异诊断列表，比如“可能是肺癌、也可能是结核”，供医生参考
import lombok.Data;
import java.util.List;

@Data
public class DifferentialDiagnosisDTO {
    private String name;
    private Double probability;
    private String description;
    private List<String> supportingFeatures;
    private List<String> contradictingFeatures;
}


