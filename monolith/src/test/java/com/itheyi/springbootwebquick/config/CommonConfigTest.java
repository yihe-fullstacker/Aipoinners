package com.itheyi.springbootwebquick.config;

import com.itheyi.springbootwebquick.repository.RedisChatMemoryStore;
import com.itheyi.springbootwebquick.tools.HealthConsultationTool;
import com.itheyi.springbootwebquick.tools.WebSearchTool;
import dev.langchain4j.model.chat.ChatLanguageModel;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.boot.test.context.runner.ApplicationContextRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.test.util.ReflectionTestUtils;

import static org.junit.jupiter.api.Assertions.assertEquals;

class CommonConfigTest {

//    private final ApplicationContextRunner contextRunner = new ApplicationContextRunner()
//            .withUserConfiguration(CommonConfig.class, MockDependencyConfig.class)
//            .withPropertyValues(
//                    "openai.base-url=https://dashscope.aliyuncs.com/compatible-mode/v1",
//                    "openai.api-key=test-key",
//                    "openai.chat.options.model-name=qwen-plus",
//                    "openai.chat.fallback.model-name=qwen-turbo",
//                    "openai.vision.model-name=qwen3-vl-plus"
//            );
//
//    @Test
//    void shouldReadIndependentChatAndVisionModelConfigs() {
//        contextRunner.run(context -> {
//            CommonConfig config = context.getBean(CommonConfig.class);
//            assertEquals("qwen-plus", config.getChatModelName());
//            assertEquals("qwen3-vl-plus", config.getVisionModelName());
//        });
//    }
//
//    @Test
//    void shouldBuildVisionBeanUsingVisionModelName() {
//        contextRunner.run(context -> {
//            ChatLanguageModel visionModel = (ChatLanguageModel) context.getBean("openAiVisionChatModel");
//            Object modelName = ReflectionTestUtils.getField(visionModel, "modelName");
//            assertEquals("qwen3-vl-plus", modelName);
//        });
//    }
//
//    @Test
//    void shouldBuildFallbackStreamingModelBean() {
//        contextRunner.run(context -> {
//            Object fallbackBean = context.getBean("openAiStreamingFallbackChatModel");
//            Object modelName = ReflectionTestUtils.getField(fallbackBean, "modelName");
//            assertEquals("qwen-turbo", modelName);
//        });
//    }
//
//    @Configuration
//    static class MockDependencyConfig {
//        @Bean
//        RedisChatMemoryStore redisChatMemoryStore() {
//            return Mockito.mock(RedisChatMemoryStore.class);
//        }
//
//        @Bean
//        HealthConsultationTool healthConsultationTool() {
//            return Mockito.mock(HealthConsultationTool.class);
//        }
//
//        @Bean
//        WebSearchTool webSearchTool() {
//            return Mockito.mock(WebSearchTool.class);
//        }
//    }
}
