package com.itheyi.springbootwebquick.dto;

import lombok.Data;
//将后端AI分析的数据描述病灶信息（位置、大小、形态），常用于可视化标注
@Data
public class LesionDTO {
    private String type;
    private Double confidence;
    private String location;
    private String description;
}