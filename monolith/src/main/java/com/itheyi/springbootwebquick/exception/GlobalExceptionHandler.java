package com.itheyi.springbootwebquick.exception;

import com.itheyi.springbootwebquick.dto.ErrorResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import java.util.stream.Collectors;

@RestControllerAdvice
public class GlobalExceptionHandler {

    /**
     * 处理请求参数验证失败异常
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidationException(MethodArgumentNotValidException ex) {
        String errorMessage = ex.getBindingResult()
                .getFieldErrors()
                .stream()
                .map(FieldError::getDefaultMessage)
                .collect(Collectors.joining(", "));
        
        ErrorResponse error = new ErrorResponse(errorMessage, "VALIDATION_ERROR", 400);
        return ResponseEntity.badRequest().body(error);
    }

    /**
     * 处理约束违反异常
     */
    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<ErrorResponse> handleConstraintViolationException(ConstraintViolationException ex) {
        String errorMessage = ex.getConstraintViolations()
                .stream()
                .map(ConstraintViolation::getMessage)
                .collect(Collectors.joining(", "));
        
        ErrorResponse error = new ErrorResponse(errorMessage, "VALIDATION_ERROR", 400);
        return ResponseEntity.badRequest().body(error);
    }

    /**
     * 处理运行时异常
     */
    @ExceptionHandler(RuntimeException.class)
    public Object handleRuntimeException(RuntimeException ex) {
        // 如果是返回 text/event-stream 或 text/plain，无法直接序列化 ErrorResponse
        // 因此这里直接返回带状态码的 ResponseEntity，并保证返回 String 格式的错误信息。
        String escapedMessage = ex.getMessage() != null ? ex.getMessage().replace("\"", "\\\"").replace("\n", "\\n").replace("\r", "") : "";
        return ResponseEntity.badRequest().body("{\"error\": \"RUNTIME_ERROR\", \"message\": \"" + escapedMessage + "\"}");
    }

    /**
     * 处理其他所有异常
     */
    @ExceptionHandler(Exception.class)
    public Object handleException(Exception ex) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body("{\"error\": \"INTERNAL_ERROR\", \"message\": \"服务器内部错误: " + ex.getMessage() + "\"}");
    }
}


