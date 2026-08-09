package com.itheyi.springbootwebquick.config;

import com.itheyi.springbootwebquick.aiservice.HealthConsultantService;
import com.itheyi.springbootwebquick.repository.RedisChatMemoryStore;
import com.itheyi.springbootwebquick.tools.HealthConsultationTool;
import com.itheyi.springbootwebquick.tools.WebSearchTool;
import dev.langchain4j.memory.ChatMemory;
import dev.langchain4j.memory.chat.ChatMemoryProvider;
import dev.langchain4j.memory.chat.MessageWindowChatMemory;
import dev.langchain4j.model.chat.ChatLanguageModel;
import dev.langchain4j.model.chat.StreamingChatLanguageModel;
import dev.langchain4j.model.openai.OpenAiChatModel;
import dev.langchain4j.model.openai.OpenAiStreamingChatModel;
import dev.langchain4j.service.AiServices;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/*
* controller层依赖于aiservice层，aiservice层依赖的于config
* */
@Configuration
public class CommonConfig {
    /*
    * @Value - 属性注入
   作用：从配置文件（application.yaml/yml）中读取值并注入到变量中。
   */
    @Value("${openai.base-url:https://dashscope.aliyuncs.com/compatible-mode/v1}")
    private String baseUrl;

    @Value("${openai.api-key:}")
    private String apiKey;

    @Value("${openai.chat.options.model-name:qwen-plus}")
    private String chatModelName;

    @Value("${openai.chat.fallback.model-name:qwen-turbo}")
    private String fallbackChatModelName;

    @Value("${openai.vision.model-name:qwen3-vl-plus}")
    private String visionModelName;
    /*
    * @Autowired - 依赖注入
    作用：自动注入Spring容器中管理的Bean，实现依赖注入。
    * */
    @Autowired
    private RedisChatMemoryStore redisChatMemoryStore;

    @Autowired
    private HealthConsultationTool healthConsultationTool;

    @Autowired
    private WebSearchTool webSearchTool;
    /*
    * application.yml (配置文件)
        ↓
    @Value 注入
        ↓
    CommonConfig (配置类成员变量)
        ↓
    @Bean 方法使用
        ↓
    创建模型对象 (ChatLanguageModel等)
        ↓
        @Autowired 注入
        ↓
    Service/Controller 使用
    *
    * */
    @Bean  //作用创建同步对话模型
    public ChatLanguageModel openAiChatModel() {
        return OpenAiChatModel.builder()
                .baseUrl(baseUrl)
                .apiKey(apiKey)
                .modelName(chatModelName)
                .build();
    }

    /*
    * @Bean - 方法返回值注册为Bean
    作用：将方法的返回值注册为Spring容器中的Bean。
    *
    @Bean注入对象，可以通过 @Autowired注解使用这个对象
    * */
    @Bean//作用：创建流式对话模型（主模型）
    public StreamingChatLanguageModel openAiStreamingChatModel() {
        return OpenAiStreamingChatModel.builder()
                .baseUrl(baseUrl)
                .apiKey(apiKey)
                .modelName(chatModelName)
                .build();
    }

    @Bean//作用：创建降级备用流式模型qwen-turbo,防止另一个模型崩溃
    public StreamingChatLanguageModel openAiStreamingFallbackChatModel() {
        return OpenAiStreamingChatModel.builder()
                .baseUrl(baseUrl)
                .apiKey(apiKey)
                .modelName(fallbackChatModelName)
                .build();
    }

    @Bean//作用：创建支持函数调用的流式模型,
    public StreamingChatLanguageModel openAiStreamingChatModelWithSearch() {
        return OpenAiStreamingChatModel.builder()
                .baseUrl(baseUrl)
                .apiKey(apiKey)
                .modelName(chatModelName)
                .strictTools(false)
                .build();
    }

    @Bean//创建降级版本的、支持函数调用的流式模型。
    public StreamingChatLanguageModel openAiStreamingFallbackChatModelWithSearch() {
        return OpenAiStreamingChatModel.builder()
                .baseUrl(baseUrl)
                .apiKey(apiKey)
                .modelName(fallbackChatModelName)
                .strictTools(false)
                .build();
    }
    //作用是创建支持视觉理解的多模态模型，能够看懂和分析图片内容。
    @Bean(name = "openAiVisionChatModel")
    public ChatLanguageModel openAiVisionChatModel(CommonConfig config) {
        return OpenAiChatModel.builder()
                .baseUrl(baseUrl)
                .apiKey(apiKey)
                .modelName(visionModelName) //使用视觉模型名称（如 qwen3-vl-plus）
                .build();
    }

    @Bean//作用是创建单个会话的记忆管理对象，用于存储对话历史，保持上下文连续性。
    public ChatMemory chatMemory() {
        return MessageWindowChatMemory.builder()
                .maxMessages(20)
                .chatMemoryStore(redisChatMemoryStore)
                .build();
    }

    @Bean//这个Bean的作用是创建多会话记忆管理器，能够为每个用户/会话独立存储对话历史，实现多用户之间的记忆隔离。
    public ChatMemoryProvider chatMemoryProvider() {
        return new ChatMemoryProvider() {
            @Override
            public ChatMemory get(Object memoryId) {
                return MessageWindowChatMemory.builder()
                        .id(memoryId)
                        .maxMessages(20)
                        .chatMemoryStore(redisChatMemoryStore)
                        .build();
            }
        };
    }

    @Bean(name = "healthConsultantService")//这个Bean的作用是创建基础版健康咨询服务的核心业务对象，它是整个医疗AI助手的服务入口，整合了模型、记忆和工具三大能力
    public HealthConsultantService healthConsultantService(
            @Qualifier("openAiStreamingChatModel") StreamingChatLanguageModel openAiStreamingChatModel,
            ChatMemoryProvider chatMemoryProvider) {
        return AiServices.builder(HealthConsultantService.class)
                .streamingChatLanguageModel(openAiStreamingChatModel)
                .chatMemoryProvider(chatMemoryProvider)
                .tools(healthConsultationTool)
                .build();
    }

    @Bean(name = "healthConsultantServiceWithSearch")
    public HealthConsultantService healthConsultantServiceWithSearch(
            @Qualifier("openAiStreamingChatModelWithSearch") StreamingChatLanguageModel openAiStreamingChatModelWithSearch,
            ChatMemoryProvider chatMemoryProvider) {
        return AiServices.builder(HealthConsultantService.class)
                .streamingChatLanguageModel(openAiStreamingChatModelWithSearch)
                .chatMemoryProvider(chatMemoryProvider)
                .tools(healthConsultationTool, webSearchTool)
                .build();
    }

    @Bean(name = "healthConsultantServiceFallback")
    public HealthConsultantService healthConsultantServiceFallback(
            @Qualifier("openAiStreamingFallbackChatModel") StreamingChatLanguageModel openAiStreamingFallbackChatModel,
            ChatMemoryProvider chatMemoryProvider) {
        return AiServices.builder(HealthConsultantService.class)
                .streamingChatLanguageModel(openAiStreamingFallbackChatModel)
                .chatMemoryProvider(chatMemoryProvider)
                .tools(healthConsultationTool)
                .build();
    }

    @Bean(name = "healthConsultantServiceWithSearchFallback")
    public HealthConsultantService healthConsultantServiceWithSearchFallback(
            @Qualifier("openAiStreamingFallbackChatModelWithSearch") StreamingChatLanguageModel openAiStreamingFallbackChatModelWithSearch,
            ChatMemoryProvider chatMemoryProvider) {
        return AiServices.builder(HealthConsultantService.class)
                .streamingChatLanguageModel(openAiStreamingFallbackChatModelWithSearch)
                .chatMemoryProvider(chatMemoryProvider)
                .tools(healthConsultationTool, webSearchTool)
                .build();
    }

    public String getChatModelName() {
        return chatModelName;
    }

    public String getVisionModelName() {
        return visionModelName;
    }
}

/*
*   Config层	配置管理、Bean创建	@Value、@Bean、@Configuration	业务逻辑
    Service层	业务逻辑	业务流程、数据校验、事务管理	配置读取、Bean创建
    Controller层	请求处理	参数接收、响应返回	业务逻辑、配置
* */
