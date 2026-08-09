package com.itheyi.springbootwebquick.filters;

import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;
/*：网关统一登录校验过滤器，所有请求经过网关时，先在这里检查是否登录，没登录就直接拦截，不让访问后端服务。*/
@Component
public class AuthGlobalFilter implements GlobalFilter, Ordered {
    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        //1.获取request

        //2.判断是否需要做登录拦截

        //3.获取token

        //4.校验并解析token
        Long userId=null;

        //5.传递用户信息（把获取到的用户写入请求头）
        /*
        * 把登录校验后得到的用户 ID，写入请求头，传给下游微服务。
          微服务直接从请求头拿 user-info
        * */
        String userinfo = userId.toString();
        ServerWebExchange swe = exchange.mutate()//mutate就是对下游请求做更改
                .request(builder -> builder.header("user-info", userinfo))
                .build();

        //6.放行
        return chain.filter(swe);
    }
    @Override
    public int getOrder() {
        return 0;
    }
}
