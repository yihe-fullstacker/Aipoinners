package com.itheyi.springbootwebquick.filter;
//登录检查过滤器---拦截需要认证的请求，验证用户是否已登录（例如检查Session或JWT令牌）
import com.fasterxml.jackson.databind.ObjectMapper;
import com.itheyi.springbootwebquick.Jwt.JwtUtil;
import com.itheyi.springbootwebquick.dto.ErrorResponse;
import jakarta.servlet.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.StringUtils;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

public class LoginCheckFilter implements Filter {

    private static final Logger log = LoggerFactory.getLogger(LoginCheckFilter.class);

    private static final List<String> ALLOWED_ORIGIN_PREFIXES = List.of(
            "http://localhost:",
            "http://127.0.0.1:",
            "http://47.108.52.206",
            "https://47.108.52.206"
    );

    private final JwtUtil jwtUtil;//声明一个不可变的JwtUtil引用，用于存储JWT验证工具
    //接收外部传入的JwtUtil实例并保存起来--为了让让这个Filter能够"获得并使用"JWT验证的能力。
    public LoginCheckFilter(JwtUtil jwtUtil) {
        this.jwtUtil = jwtUtil;
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        HttpServletRequest req = (HttpServletRequest) request;//将通用的ServletRequest/ServletResponse转换为HTTP专用对象,可以有HTTP特有信息
        HttpServletResponse resp = (HttpServletResponse) response;//获取请求的URI（如/api/users）和HTTP方法（如GET、POST）,能设置HTTP特有响应

        String uri = req.getRequestURI();//获取请求路径
        String method = req.getMethod();//获取HTTP方法

        applyCorsHeaders(req, resp);
        
        log.info("LoginCheckFilter - URI: {}, Method: {}", uri, method);
        
        //检查当前请求URI是否在白名单中:如果是白名单里面的请求，直接放行
        if ("OPTIONS".equalsIgnoreCase(method)) {
            log.info("LoginCheckFilter - 预检请求，直接返回 200: {}", uri);
            resp.setStatus(HttpServletResponse.SC_OK);
            return;
        }

        if (isWhitelisted(uri)) {
            log.info("LoginCheckFilter - 白名单路径，直接放行: {}", uri);
            chain.doFilter(request, response);
            return;
        }
        
        log.info("LoginCheckFilter - 需要认证的路径: {}", uri);
        
        //验证请求是否携带正确格式的认证令牌
        String auth = req.getHeader("Authorization");
        if (!StringUtils.hasLength(auth) || !auth.startsWith("Bearer ")) {
            log.warn("LoginCheckFilter - 缺少Authorization header或格式错误");
            writeUnauthorized(resp);
            return;
        }
        //JWT令牌验证的核心逻辑
        String token = auth.substring(7);
        boolean valid;
        try {
            valid = jwtUtil.validateToken(token);
        } catch (Exception e) {
            log.error("LoginCheckFilter - Token验证异常", e);
            valid = false;
        }
        if (!valid) {
            log.warn("LoginCheckFilter - Token无效");
            writeUnauthorized(resp);
            return;
        }

        log.info("LoginCheckFilter - Token验证成功，放行");
        chain.doFilter(request, response);
    }

    private void applyCorsHeaders(HttpServletRequest req, HttpServletResponse resp) {
        String origin = req.getHeader("Origin");
        if (origin != null && isAllowedOrigin(origin)) {
            resp.setHeader("Access-Control-Allow-Origin", origin);
            resp.setHeader("Vary", "Origin");
            resp.setHeader("Access-Control-Allow-Credentials", "true");
            resp.setHeader("Access-Control-Allow-Methods", "GET,POST,PUT,DELETE,OPTIONS");
            resp.setHeader("Access-Control-Allow-Headers", "Authorization,Content-Type,X-Requested-With,Accept,Origin");
            resp.setHeader("Access-Control-Max-Age", "3600");
        }
    }

    private boolean isAllowedOrigin(String origin) {
        for (String prefix : ALLOWED_ORIGIN_PREFIXES) {
            if (origin.startsWith(prefix)) {
                return true;
            }
        }
        return false;
    }

    private boolean isWhitelisted(String uri) {
        //这些路径不需要登录就能访问
        if (uri == null) return true;
        if (uri.contains("/api/auth/")) return true;//登录注册接口
        if (uri.contains("/api/sms/")) return true;//短信验证接口
        if (uri.contains("/api/smart-qa/")) return true;//智能问答接口
        if (uri.contains("/api/medical/")) return true;//医学影像分析接口
        if (uri.contains("/api/images/")) return true;//图片资源
        if (uri.contains("/swagger-ui")) return true;//API文档
        if (uri.contains("/v3/api-docs")) return true;//OpenAPI文档
        if (uri.equals("/favicon.ico")) return true;//网站图标
        if (uri.equals("/error")) return true;//错误页面
        return false;
    }
    //统一返回401未授权错误响应的方法  表示请求需要用户认证
    private void writeUnauthorized(HttpServletResponse resp) throws IOException {
        resp.setStatus(HttpServletResponse.SC_UNAUTHORIZED);//HttpServletResponse.SC_UNAUTHORIZED 是 Java Servlet API 中预定义的一个常量，其值就是 401
        resp.setContentType("application/json;charset=UTF-8");
        ErrorResponse error = new ErrorResponse("NOT_LOGIN", "UNAUTHORIZED", 401);
        String json = new ObjectMapper().writeValueAsString(error);
        resp.getWriter().write(json);
    }
}
