package com.itheyi.springbootwebquick.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.util.List;
import java.util.Map;
//前端请求将数据传递给后端
@Data
public class DifyAssistantRequest {
    @NotBlank(message = "助手消息不能为空")
    private String message;
    private String routeName;
    private String routePath;
    private String pageTitle;
    private String contextTitle;
    private String userId;
    private List<Map<String, String>> history;
}
