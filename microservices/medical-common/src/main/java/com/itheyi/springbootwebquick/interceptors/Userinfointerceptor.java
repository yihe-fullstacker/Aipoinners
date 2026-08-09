
package com.itheyi.springbootwebquick.interceptors;

import com.itheyi.springbootwebquick.utils.UserContext;
import io.jsonwebtoken.Claims;
import io.micrometer.common.util.StringUtils;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.web.servlet.HandlerExceptionResolver;
import org.springframework.web.servlet.HandlerInterceptor;

//不做任何拦截，只做用户信息获取即可
public class Userinfointerceptor implements HandlerInterceptor {
    /*
    * 请求预处理，登录校验 + 绑定用户信息
    它在请求到达你的 Controller 接口之前就会执行，是拦截器最核心的方法，一般会做这些事：
    从请求头 / Token 中解析用户身份
    比如从 request 里拿到前端传的 JWT Token，解析出用户 ID、角色等信息。
    登录校验
    如果 Token 无效 / 过期 / 不存在，直接返回false，并给response设置401状态码，请求不会进入 Controller，直接拦截。
    如果校验通过，返回true，请求继续向下执行。
    绑定用户信息到当前请求
    把解析出来的用户 ID、角色等信息存入request的attribute，或者存入ThreadLocal，后续 Controller 里就能直接获取当前登录用户，不用每次都解析 Token。
    * */
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception{
        //1.获取用户信息
        String userinfo = request.getHeader("user-info");
        //2.判断是否获取了用户，如果有，存入ThreadLocal
        if(StringUtils.isNotBlank(userinfo)){
            UserContext.setUser(Long.valueOf(userinfo));
        }
        //3.放行
        return true;
    }
    /*
    * 请求后清理，释放资源
    它在 Controller 处理完请求、响应返回给客户端之后执行，一般做这些事：
    清理 ThreadLocal 中的用户信息
    避免线程复用（比如 Tomcat 线程池）导致用户信息泄露，影响下一个请求。
    记录请求日志
    记录请求耗时、异常信息、用户操作日志等。
    资源释放
    关闭数据库连接、释放锁等。
    * */
    /*
    * UserContext = 微服务内部的「当前用户全局存储器」
让整个微服务内的所有代码，随时随地获取当前登录用户 ID，不用反复传递参数！
    * */
    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
        // 清理ThreadLocal，防止内存泄漏和用户信息串号
        UserContext.removeUser();
    }

}
