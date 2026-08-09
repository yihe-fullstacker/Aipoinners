package com.itheyi.springbootwebquick.exception;

import com.itheyi.springbootwebquick.dto.ErrorResponse;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.stream.Collectors;

@RestControllerAdvice
public class GlobalExceptionHandler {

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

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<ErrorResponse> handleConstraintViolationException(ConstraintViolationException ex) {
        String errorMessage = ex.getConstraintViolations()
                .stream()
                .map(ConstraintViolation::getMessage)
                .collect(Collectors.joining(", "));

        ErrorResponse error = new ErrorResponse(errorMessage, "VALIDATION_ERROR", 400);
        return ResponseEntity.badRequest().body(error);
    }

    @ExceptionHandler(RuntimeException.class)
    public Object handleRuntimeException(RuntimeException ex) {
        String escapedMessage = ex.getMessage() != null ? ex.getMessage().replace("\"", "\\\"").replace("\n", "\\n").replace("\r", "") : "";
        return ResponseEntity.badRequest().body("{\"error\": \"RUNTIME_ERROR\", \"message\": \"" + escapedMessage + "\"}");
    }

    @ExceptionHandler(Exception.class)
    public Object handleException(Exception ex) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body("{\"error\": \"INTERNAL_ERROR\", \"message\": \"服务器内部错误: " + ex.getMessage() + "\"}");
    }
}
