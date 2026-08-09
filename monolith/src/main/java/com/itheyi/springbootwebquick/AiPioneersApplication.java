package com.itheyi.springbootwebquick;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.ServletComponentScan;
/*用户登录流程：
Controller 接收登录请求

Service 处理登录业务逻辑

Jwt 生成令牌

Mapper 查询用户信息

POJO/DTO 封装数据

Controller 返回登录结果

Config包：项目配置，定义各种Bean和全局设置（理解配置类的主要作用）

Exception包：统一异常处理，提高代码健壮性

Util包：通用工具方法，避免代码重复
*/
@ServletComponentScan
@SpringBootApplication
public class AiPioneersApplication {

    public static void main(String[] args) {
        SpringApplication.run(AiPioneersApplication.class, args);
    }

}
