package com.itheyi.springbootwebquick.filter;
//安全头过滤器--为HTTP响应自动添加一系列安全相关的响应头，增强Web应用的安全性，防御常见网络攻击。
import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;

public class SecurityHeadersFilter implements Filter {
    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        HttpServletResponse resp = (HttpServletResponse) response;//整体：把"通用响应对象"转换为"HTTP专用响应对象"
        resp.setHeader("X-Frame-Options", "SAMEORIGIN");
        resp.setHeader("X-Content-Type-Options", "nosniff");
        resp.setHeader("Referrer-Policy", "no-referrer-when-downgrade");
        // 更宽松的 Content-Security-Policy，允许图片加载和开发环境的跨域请求
        resp.setHeader("Content-Security-Policy", 
            "default-src 'self'; " +
            "img-src 'self' data: blob: http: https:; " +
            "script-src 'self' 'unsafe-inline' 'unsafe-eval'; " +
            "style-src 'self' 'unsafe-inline'; " +
            "connect-src 'self' " +
            "http://localhost:7070 http://localhost:8082 http://localhost:5000 ws://localhost:7070 " +
            "http://127.0.0.1:7070 http://127.0.0.1:8082 http://127.0.0.1:5000 ws://127.0.0.1:7070 " +
            "http://47.108.52.206 http://47.108.52.206:8082 https://47.108.52.206 https://47.108.52.206:8082;");
        chain.doFilter(request, response);//将请求与响应传递给下一个过滤器
    }
}
