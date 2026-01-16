package com.contract.management.interfaces.rest.api.v1.handler;

import com.contract.management.domain.exception.ClauseExtractionException;
import com.contract.management.domain.exception.ContractNotFoundException;
import com.contract.management.domain.exception.FileProcessingException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 全局异常处理器
 * 统一处理各种异常情况，返回标准化的错误响应
 *
 * @author SaltyFish
 * @since 1.0.0
 */
@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    /**
     * 处理参数验证异常
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidationExceptions(MethodArgumentNotValidException ex) {
        Map<String, Object> errors = new HashMap<>();
        ex.getBindingResult().getAllErrors().forEach((error) -> {
            String fieldName = ((FieldError) error).getField();
            String errorMessage = error.getDefaultMessage();
            errors.put(fieldName, errorMessage);
        });

        log.warn("参数验证失败: {}", errors);

        ErrorResponse response = ErrorResponse.builder()
            .status(HttpStatus.BAD_REQUEST.value())
            .error("参数验证失败")
            .message("请求参数不符合要求")
            .details(errors)
            .timestamp(LocalDateTime.now())
            .path(getCurrentPath())
            .build();

        return ResponseEntity.badRequest().body(response);
    }

    /**
     * 处理绑定异常
     */
    @ExceptionHandler(BindException.class)
    public ResponseEntity<ErrorResponse> handleBindException(BindException ex) {
        Map<String, Object> errors = new HashMap<>();
        ex.getBindingResult().getAllErrors().forEach((error) -> {
            String fieldName = ((FieldError) error).getField();
            String errorMessage = error.getDefaultMessage();
            errors.put(fieldName, errorMessage);
        });

        log.warn("参数绑定失败: {}", errors);

        ErrorResponse response = ErrorResponse.builder()
            .status(HttpStatus.BAD_REQUEST.value())
            .error("参数绑定失败")
            .message("请求参数格式错误")
            .details(errors)
            .timestamp(LocalDateTime.now())
            .path(getCurrentPath())
            .build();

        return ResponseEntity.badRequest().body(response);
    }

    /**
     * 处理约束违反异常
     */
    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<ErrorResponse> handleConstraintViolationException(ConstraintViolationException ex) {
        Map<String, Object> errors = ex.getConstraintViolations().stream()
            .collect(Collectors.toMap(
                violation -> violation.getPropertyPath().toString(),
                ConstraintViolation::getMessage
            ));

        log.warn("约束验证失败: {}", errors);

        ErrorResponse response = ErrorResponse.builder()
            .status(HttpStatus.BAD_REQUEST.value())
            .error("约束验证失败")
            .message("请求参数违反约束条件")
            .details(errors)
            .timestamp(LocalDateTime.now())
            .path(getCurrentPath())
            .build();

        return ResponseEntity.badRequest().body(response);
    }

    /**
     * 处理非法参数异常
     */
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ErrorResponse> handleIllegalArgumentException(IllegalArgumentException ex) {
        log.warn("非法参数异常: {}", ex.getMessage());

        ErrorResponse response = ErrorResponse.builder()
            .status(HttpStatus.BAD_REQUEST.value())
            .error("非法参数")
            .message(ex.getMessage())
            .timestamp(LocalDateTime.now())
            .path(getCurrentPath())
            .build();

        return ResponseEntity.badRequest().body(response);
    }

    /**
     * 处理合同未找到异常
     */
    @ExceptionHandler(ContractNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleContractNotFoundException(ContractNotFoundException ex) {
        log.warn("合同未找到: {}", ex.getMessage());

        ErrorResponse response = ErrorResponse.builder()
            .status(HttpStatus.NOT_FOUND.value())
            .error("合同不存在")
            .message(ex.getMessage())
            .timestamp(LocalDateTime.now())
            .path(getCurrentPath())
            .build();

        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
    }

    /**
     * 处理文件处理异常
     */
    @ExceptionHandler(FileProcessingException.class)
    public ResponseEntity<ErrorResponse> handleFileProcessingException(FileProcessingException ex) {
        log.error("文件处理异常: {}", ex.getMessage(), ex);

        ErrorResponse response = ErrorResponse.builder()
            .status(HttpStatus.INTERNAL_SERVER_ERROR.value())
            .error("文件处理失败")
            .message(ex.getMessage())
            .timestamp(LocalDateTime.now())
            .path(getCurrentPath())
            .build();

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
    }

    /**
     * 处理条款抽取异常
     */
    @ExceptionHandler(ClauseExtractionException.class)
    public ResponseEntity<ErrorResponse> handleClauseExtractionException(ClauseExtractionException ex) {
        log.error("条款抽取异常: {}", ex.getMessage(), ex);

        ErrorResponse response = ErrorResponse.builder()
            .status(HttpStatus.BAD_REQUEST.value())
            .error("条款抽取失败")
            .message(ex.getMessage())
            .timestamp(LocalDateTime.now())
            .path(getCurrentPath())
            .build();

        return ResponseEntity.badRequest().body(response);
    }

    /**
     * 处理JSON反序列化异常
     */
    @ExceptionHandler(org.springframework.http.converter.HttpMessageNotReadableException.class)
    public ResponseEntity<ErrorResponse> handleJsonDeserializationException(
            org.springframework.http.converter.HttpMessageNotReadableException ex) {
        String message = "请求体格式错误";
        if (ex.getCause() instanceof com.fasterxml.jackson.core.JsonProcessingException) {
            message = "JSON格式错误: " + ex.getCause().getMessage();
        }

        log.warn("JSON反序列化失败: {}", message);

        ErrorResponse response = ErrorResponse.builder()
            .status(HttpStatus.BAD_REQUEST.value())
            .error("JSON解析错误")
            .message(message)
            .timestamp(LocalDateTime.now())
            .path(getCurrentPath())
            .build();

        return ResponseEntity.badRequest().body(response);
    }

    /**
     * 处理其他未捕获的异常
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGenericException(Exception ex) {
        log.error("未处理的异常: {}", ex.getMessage(), ex);

        ErrorResponse response = ErrorResponse.builder()
            .status(HttpStatus.INTERNAL_SERVER_ERROR.value())
            .error("系统内部错误")
            .message("系统处理过程中发生错误，请稍后重试")
            .timestamp(LocalDateTime.now())
            .path(getCurrentPath())
            .build();

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
    }

    /**
     * 获取当前请求路径（简化实现）
     */
    private String getCurrentPath() {
        // 在实际项目中，可以通过HttpServletRequest获取请求路径
        return "/unknown";
    }

    /**
     * 统一错误响应DTO
     */
    @lombok.Data
    @lombok.Builder
    @lombok.NoArgsConstructor
    @lombok.AllArgsConstructor
    public static class ErrorResponse {
        /**
         * HTTP状态码
         */
        private Integer status;

        /**
         * 错误类型
         */
        private String error;

        /**
         * 错误消息
         */
        private String message;

        /**
         * 详细错误信息
         */
        private Map<String, Object> details;

        /**
         * 错误发生时间
         */
        private LocalDateTime timestamp;

        /**
         * 请求路径
         */
        private String path;
    }
}