package com.itheyi.springbootwebquick.filter;
//限流过滤器--控制单位时间内来自同一IP、用户或全局的请求频率，防止恶意刷接口或流量过载导致服务崩溃。对http请求频率进行限流
import com.fasterxml.jackson.databind.ObjectMapper;
import com.itheyi.springbootwebquick.Jwt.JwtUtil;
import com.itheyi.springbootwebquick.dto.ErrorResponse;
import jakarta.servlet.*;
import lombok.extern.slf4j.Slf4j;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
public class RateLimitFilter implements Filter {

    private final JwtUtil jwtUtil;
    private final Map<String, Bucket> buckets = new ConcurrentHashMap<>();
    private final double refillRatePerSec = 5.0;
    private final double capacity = 10.0;

    public RateLimitFilter(JwtUtil jwtUtil) {
        this.jwtUtil = jwtUtil;
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        HttpServletRequest req = (HttpServletRequest) request;
        HttpServletResponse resp = (HttpServletResponse) response;

        String uri = req.getRequestURI();
        String method = req.getMethod();

        if (!shouldLimit(uri, method)) {
            chain.doFilter(request, response);
            return;
        }

        String key = resolveKey(req);
        Bucket bucket = buckets.computeIfAbsent(key, k -> new Bucket(capacity, refillRatePerSec));
        if (!bucket.tryConsume()) {
            writeTooManyRequests(resp);
            return;
        }

        chain.doFilter(request, response);
    }

    private boolean shouldLimit(String uri, String method) {
        if (uri == null || method == null) return false;
        if (!"POST".equalsIgnoreCase(method)) return false;
        if (uri.contains("/api/medical/analyze")) return true;
        if (uri.contains("/api/medical/analyze-deepseek")) return true;
        return false;
    }

    private String resolveKey(HttpServletRequest req) {
        String auth = req.getHeader("Authorization");
        if (auth != null && auth.startsWith("Bearer ")) {
            String token = auth.substring(7);
            try {
                Long uid = jwtUtil.getUserIdFromToken(token);
                if (uid != null) return "u:" + uid;
            } catch (Exception ignored) {
            }
        }
        String ip = req.getRemoteAddr();
        return "ip:" + (ip != null ? ip : "unknown");
    }

    private void writeTooManyRequests(HttpServletResponse resp) throws IOException {
        resp.setStatus(429);
        resp.setContentType("application/json;charset=UTF-8");
        ErrorResponse error = new ErrorResponse("RATE_LIMITED", "TOO_MANY_REQUESTS", 429);
        String json = new ObjectMapper().writeValueAsString(error);
        resp.getWriter().write(json);
    }

    static class Bucket {
        private final double capacity;
        private final double refillRatePerSec;
        private double tokens;
        private long lastRefillMillis;

        Bucket(double capacity, double refillRatePerSec) {
            this.capacity = capacity;
            this.refillRatePerSec = refillRatePerSec;
            this.tokens = capacity;
            this.lastRefillMillis = System.currentTimeMillis();
        }

        synchronized boolean tryConsume() {
            refill();
            if (tokens >= 1.0) {
                tokens -= 1.0;
                return true;
            }
            return false;
        }

        private void refill() {
            long now = System.currentTimeMillis();
            long elapsed = now - lastRefillMillis;
            if (elapsed <= 0) return;
            double add = (elapsed / 1000.0) * refillRatePerSec;
            tokens = Math.min(capacity, tokens + add);
            lastRefillMillis = now;
        }
    }
}

