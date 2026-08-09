package com.itheyi.springbootwebquick.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
//后端将 Dify 智能体（工作流）生成的数据返回给前端
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DifyAssistantResponse {
    private String answer;
    private String workflowRunId;
    private String taskId;
}
