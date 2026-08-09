package com.itheyi.springbootwebquick.dto;
//统一错误格式，所有异常都返回这个结构，前端可统一处理
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
/*
* 当系统发生异常（如参数校验失败、业务逻辑错误、服务器内部错误）时，Controller 层或全局异常处理器捕获异常，
* 将其转换为 ErrorResponse 对象返回给前端，保证所有错误响应都遵循相同的结构，
* 便于前端统一处理（如统一弹窗提示、日志记录等）
* */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ErrorResponse {
    private String message;
    private String error;
    private int status;
}


