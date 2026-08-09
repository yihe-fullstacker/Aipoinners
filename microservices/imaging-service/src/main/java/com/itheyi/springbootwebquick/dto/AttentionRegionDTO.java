package com.itheyi.springbootwebquick.dto;

import lombok.Data;
//响应DTO：表示图像中 AI 关注的区域（如高亮部分），用于在前端标注图像
@Data
public class AttentionRegionDTO {
    private String location;
    private String size;
    private Double score;
    private String description;
    private Double x;
    private Double y;
    private Double width;
    private Double height;
}


