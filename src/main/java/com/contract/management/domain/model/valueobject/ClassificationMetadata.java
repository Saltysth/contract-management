package com.contract.management.domain.model.valueobject;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.postgresql.util.PGobject;

import java.util.Map;

/**
 * 分类元数据值对象
 * 用于存储分类相关的详细信息
 *
 * @author SaltyFish
 * @since 1.0.0
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ClassificationMetadata {

    /**
     * 分类原因或依据
     */
    @JsonProperty("reason")
    private String reason;

    /**
     * AI原始响应
     */
    @JsonProperty("aiRawResponse")
    private AIRawResponse aiRawResponse;

    /**
     * 文件UUID
     */
    @JsonProperty("domainFileUuid")
    private String domainFileUuid;

    /**
     * 文件类型
     */
    @JsonProperty("domainFileType")
    private String domainFileType;

    /**
     * 分类时间戳
     */
    @JsonProperty("classificationTimestamp")
    private Long classificationTimestamp;

    /**
     * AI原始响应内部类
     */
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @JsonInclude(JsonInclude.Include.NON_NULL)
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class AIRawResponse {
        @JsonProperty("role")
        private String role;

        @JsonProperty("content")
        private String content;

        @JsonProperty("extensions")
        private Map<String, Object> extensions;

        @JsonProperty("contractType")
        private String contractType;
    }

    /**
     * 创建PGobject用于存储JSONB
     */
    public PGobject toPGobject() throws JsonProcessingException {
        ObjectMapper objectMapper = new ObjectMapper();
        String jsonString = objectMapper.writeValueAsString(this);

        try {
            PGobject pgObject = new PGobject();
            pgObject.setType("jsonb");
            pgObject.setValue(jsonString);
            return pgObject;
        } catch (java.sql.SQLException e) {
            // 将SQLException包装为RuntimeException，因为这是值对象内部问题
            throw new RuntimeException("Failed to create PGobject", e);
        }
    }

    /**
     * 从JSON字符串创建ClassificationMetadata
     */
    @JsonCreator
    public static ClassificationMetadata fromJson(String json) throws JsonProcessingException {
        if (json == null || json.trim().isEmpty()) {
            return new ClassificationMetadata();
        }

        ObjectMapper objectMapper = new ObjectMapper();
        return objectMapper.readValue(json, ClassificationMetadata.class);
    }

    /**
     * 从PGobject创建ClassificationMetadata
     */
    public static ClassificationMetadata fromPGobject(PGobject pgObject) {
        try {
            return fromJson(pgObject.getValue());
        } catch (JsonProcessingException e) {
            // 解析失败时返回空对象
            return new ClassificationMetadata();
        } catch (Exception e) {
            // 处理其他可能的异常
            return new ClassificationMetadata();
        }
    }
}