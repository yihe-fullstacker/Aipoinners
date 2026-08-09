package com.itheyi.springbootwebquick.filter;
//请求日志过滤器--统一记录所有进入系统的请求信息，用于监控、调试和审计。
import jakarta.servlet.*;
import lombok.extern.slf4j.Slf4j;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;

@Slf4j
public class RequestLoggingFilter implements Filter {
    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        HttpServletRequest req = (HttpServletRequest) request;
        HttpServletResponse resp = (HttpServletResponse) response;
        long start = System.currentTimeMillis();
        chain.doFilter(request, response);
        long duration = System.currentTimeMillis() - start;
        String ip = req.getRemoteAddr();
        String method = req.getMethod();
        String uri = req.getRequestURI();
        int status = resp.getStatus();
        log.info("{} {} {} {}ms status={}", ip, method, uri, duration, status);
    }
}

