package com.itheyi.springbootwebquick.config;

import com.itheyi.springbootwebquick.interceptors.Userinfointerceptor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
@Configuration

/*
* 作用：
* 必须注册到配置类才会生效
* 注册并启用用户信息拦截器 UserInfoInterceptor，
* 让它在请求到达 Controller 之前执行，完成用户信息的解析和存入 UserContext
* */
public class MvcConfig implements WebMvcConfigurer {

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        // 手动创建拦截器实例并注册
        registry.addInterceptor(new Userinfointerceptor())
                .addPathPatterns("/**");
    }
}
