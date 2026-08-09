package com.itheyi.springbootwebquick.routers;

import com.alibaba.cloud.nacos.NacosConfigManager;
import com.alibaba.nacos.api.config.listener.Listener;
import com.alibaba.nacos.api.exception.NacosException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.gateway.route.RouteDefinition;
import org.springframework.cloud.gateway.route.RouteDefinitionWriter;
import org.springframework.context.annotation.Configuration;
import reactor.core.publisher.Mono;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.Executor;

@Slf4j
@Configuration
@RequiredArgsConstructor
public class DynamicRouteLoader {
    private final NacosConfigManager nacosConfigManager;
    private final RouteDefinitionWriter routeDefinitionWriter;
    private final ObjectMapper objectMapper = new ObjectMapper();
    private final String dataId = "gateway-routes.json";
    private final String group = "DEFAULT_GROUP";
    private final Set<String> routeIds = new HashSet<>();
    @PostConstruct
    public void initRouteConfigListener() throws NacosException {
        //1.项目启动时，先拉取一次配置，并且添加配置监听器
        String configInfo = nacosConfigManager.getConfigService()
                .getConfigAndSignListener(dataId, group, 5000, new Listener() {
                    @Override
                    public Executor getExecutor() {
                        return null;
                    }

                    @Override
                    public void receiveConfigInfo(String configInfo) {
                        //2.监听到配置变更，需要去更新路由表
                        updateConfigInfo(configInfo);

                    }
                });
        //3.第一次读取到配置，也需要更新到路由表
        updateConfigInfo(configInfo);
    }
    //配置文件（Nacos）一变 → 代码自动监听到 → 代码自动去更新网关路由。
    /*
    * 3. Gateway 动态路由核心 API
        RouteDefinitionWriter
        Spring Cloud Gateway 提供的动态路由写入器，支持增删改路由。
        RouteDefinition
        网关路由的标准模型（id、uri、predicates、filters）
    * */
    public void updateConfigInfo(String configInfo){
        log.debug("监听到路由配置信息: {}", configInfo);
        try {
            //1.解析配置信息，转为RouteDefinition
            List<RouteDefinition> routeDefinitions = objectMapper.readValue(
                configInfo, 
                new TypeReference<List<RouteDefinition>>() {}
            );
            //2.删除旧的路由表
            for (String routeId : routeIds){
                routeDefinitionWriter.delete(Mono.just(routeId)).subscribe();
            }
            routeIds.clear();
            //3.更新路由表
            for (RouteDefinition routeDefinition : routeDefinitions){
                //更新路由表
                routeDefinitionWriter.save(Mono.just(routeDefinition)).subscribe();
                 //记录路由表ID
                routeIds.add(routeDefinition.getId());
            }
        } catch (Exception e) {
            log.error("更新路由配置失败", e);
        }
    //3.
    }
}

