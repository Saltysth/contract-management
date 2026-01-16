package com.contract.management.domain.model.valueobject;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

/**
 * 操作详情值对象
 *
 * @author SaltyFish
 * @since 1.0.0
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class OperationDetails {

    /**
     * 操作描述
     */
    private String description;

    /**
     * 操作前的状态（可选）
     */
    private Map<String, Object> beforeState;

    /**
     * 操作后的状态（可选）
     */
    private Map<String, Object> afterState;

    /**
     * 额外的操作参数
     */
    private Map<String, Object> parameters;

    /**
     * 操作结果
     */
    private String result;

    /**
     * 错误信息（如果有）
     */
    private String errorMessage;

    /**
     * 操作耗时（毫秒）
     */
    private Long executionTimeMs;

    /**
     * 转换为JSON字符串
     */
    public String toJson(ObjectMapper objectMapper) {
        try {
            return objectMapper.writeValueAsString(this);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("操作详情序列化失败", e);
        }
    }

    /**
     * 从JSON字符串创建
     */
    public static OperationDetails fromJson(String json, ObjectMapper objectMapper) {
        try {
            return objectMapper.readValue(json, OperationDetails.class);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("操作详情反序列化失败", e);
        }
    }

    /**
     * 创建成功的操作详情
     */
    public static OperationDetails success(String description) {
        return OperationDetails.builder()
                .description(description)
                .result("SUCCESS")
                .build();
    }

    /**
     * 创建失败的操作详情
     */
    public static OperationDetails failure(String description, String errorMessage) {
        return OperationDetails.builder()
                .description(description)
                .result("FAILURE")
                .errorMessage(errorMessage)
                .build();
    }

    /**
     * 创建进行中的操作详情
     */
    public static OperationDetails inProgress(String description) {
        return OperationDetails.builder()
                .description(description)
                .result("IN_PROGRESS")
                .build();
    }
}