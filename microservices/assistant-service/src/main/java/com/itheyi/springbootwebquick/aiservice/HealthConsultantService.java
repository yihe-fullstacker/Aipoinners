package com.itheyi.springbootwebquick.aiservice;

import dev.langchain4j.service.SystemMessage;
import dev.langchain4j.service.UserMessage;
import dev.langchain4j.service.V;
import reactor.core.publisher.Flux;

//这一个aiservice层作用是实现AI对话能力的接口定义层。核心功能：加载提示词、管理对话记忆、流式输出
//HealthConsultantService 就是 AI 助手的"大脑接口"，定义了这个 AI 助手能做什么、怎么说话、如何记忆。你只需要定义接口，框架自动帮你实现所有复杂的 AI 对话逻辑。
//这里实现的是健康咨询助手，还可以实现影响诊断助手，报告解读助手，症状预诊助手，创建在该层中
public interface HealthConsultantService {

    @SystemMessage(fromResource = "system.txt")
    //流式输出响应结果
    Flux<String> chat(@V("memoryId") String memoryId, @UserMessage String userMessage);
}
