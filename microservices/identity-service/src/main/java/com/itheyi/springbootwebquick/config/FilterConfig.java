package com.itheyi.springbootwebquick.config;

import com.itheyi.springbootwebquick.Jwt.JwtUtil;
import com.itheyi.springbootwebquick.filter.LoginCheckFilter;
import com.itheyi.springbootwebquick.filter.RateLimitFilter;
import com.itheyi.springbootwebquick.filter.RequestLoggingFilter;
import com.itheyi.springbootwebquick.filter.SecurityHeadersFilter;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

import java.util.List;

@Configuration
public class FilterConfig {

    @Bean
    public FilterRegistrationBean<CorsFilter> corsFilter() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowCredentials(true);
        configuration.setAllowedOriginPatterns(List.of(
                "http://localhost:*",
                "http://127.0.0.1:*",
                "http://47.108.52.206",
                "http://47.108.52.206:*",
                "https://47.108.52.206",
                "https://47.108.52.206:*"
        ));
        configuration.setAllowedHeaders(List.of("*"));
        configuration.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        configuration.setExposedHeaders(List.of("Authorization", "Content-Type"));
        configuration.setMaxAge(3600L);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);

        FilterRegistrationBean<CorsFilter> bean = new FilterRegistrationBean<>(new CorsFilter(source));
        bean.setOrder(0);
        return bean;
    }

    //登录检查过滤器
    /*在请求处理前，验证用户是否已登录（通过JWT ToKen）,如果未登录或ToKen无效，直接返回401，阻止后续处理*/

    @Bean
    public FilterRegistrationBean<LoginCheckFilter> loginCheckFilter(JwtUtil jwtUtil) {
        FilterRegistrationBean<LoginCheckFilter> bean = new FilterRegistrationBean<>();
        bean.setFilter(new LoginCheckFilter(jwtUtil)); //注入JWT工具
        bean.addUrlPatterns("/*"); //对所有路径生效
        bean.setOrder(1); //执行顺序，   第一位
        return bean;
    }

    //限流过滤器
    /*防止恶意刷接口（如高频调用 AI 诊断接口）。
    * 可基于 IP 或 用户 ID（从 JWT 中解析）进行限流。
    * 在认证之后执行（Order=2），因为需要知道“谁在请求”才能做精准限流。*/

    @Bean
    public FilterRegistrationBean<RateLimitFilter> rateLimitFilter(JwtUtil jwtUtil) {
        FilterRegistrationBean<RateLimitFilter> bean = new FilterRegistrationBean<>();
        bean.setFilter(new RateLimitFilter(jwtUtil));
        bean.addUrlPatterns("/*");
        bean.setOrder(2);
        return bean;
    }

    //请求日志过滤器
    @Bean
    public FilterRegistrationBean<RequestLoggingFilter> requestLoggingFilter() {
        FilterRegistrationBean<RequestLoggingFilter> bean = new FilterRegistrationBean<>();
        bean.setFilter(new RequestLoggingFilter());
        bean.addUrlPatterns("/*");
        bean.setOrder(3);
        return bean;
    }

    @Bean
    public FilterRegistrationBean<SecurityHeadersFilter> securityHeadersFilter() {
        FilterRegistrationBean<SecurityHeadersFilter> bean = new FilterRegistrationBean<>();
        bean.setFilter(new SecurityHeadersFilter());
        bean.addUrlPatterns("/*");
        bean.setOrder(4);
        return bean;
    }
}

