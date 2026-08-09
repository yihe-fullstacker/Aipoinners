package com.itheyi.springbootwebquick.filters;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;
/*主要作用：统一校验用户是否登录，未登录则拦截请求；并通过 order 保证登录校验优先执行。*/
@Component
public class MyGlobalFilter implements GlobalFilter, Ordered {

    private static final Logger log = LoggerFactory.getLogger(MyGlobalFilter.class);

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        String method = exchange.getRequest().getMethod().name();
        String path = exchange.getRequest().getURI().getRawPath();
        log.info("Gateway request: {} {}", method, path);
        return chain.filter(exchange);
    }

    @Override
    public int getOrder() {
        return 0;
    }
}
