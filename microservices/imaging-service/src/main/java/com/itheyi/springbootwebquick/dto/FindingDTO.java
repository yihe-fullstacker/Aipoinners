package com.itheyi.springbootwebquick.dto;

//将后端AI分析的数据响应给前端影像学发现描述（如“左肺下叶见结节影”），属于报告内容的一部分
import lombok.Data;
import java.util.List;

@Data
public class FindingDTO {
    private String type;
    private String location;
    private Double confidence;
    private String size;
    private List<String> characteristics;
}