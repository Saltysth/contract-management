package com.contract.management.infrastructure.handler;

import com.contract.management.domain.model.valueobject.ClassificationMetadata;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.postgresql.util.PGobject;

import java.sql.SQLException;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * 分类元数据类型处理器测试
 *
 * @author SaltyFish
 * @since 1.0.0
 */
@DisplayName("分类元数据类型处理器测试")
class ClassificationMetadataTypeHandlerTest {

    private final ClassificationMetadataTypeHandler typeHandler = new ClassificationMetadataTypeHandler();
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Test
    @DisplayName("测试序列化和反序列化")
    void testSerializationAndDeserialization() throws Exception {
        // Given
        ClassificationMetadata.AIRawResponse aiResponse = new ClassificationMetadata.AIRawResponse();
        aiResponse.setRole("assistant");
        aiResponse.setContent("这是一个销售合同");

        ClassificationMetadata metadata = ClassificationMetadata.builder()
                .reason("AI自动分类")
                .aiRawResponse(aiResponse)
                .domainFileUuid("test-uuid")
                .domainFileType("pdf")
                .classificationTimestamp(System.currentTimeMillis())
                .build();

        // When - 序列化为PGobject
        PGobject pgObject = new PGobject();
        pgObject.setType("jsonb");
        pgObject.setValue(objectMapper.writeValueAsString(metadata));

        // Then - 反序列化回ClassificationMetadata
        ClassificationMetadata deserialized = ClassificationMetadata.fromPGobject(pgObject);

        assertThat(deserialized).isNotNull();
        assertThat(deserialized.getReason()).isEqualTo("AI自动分类");
        assertThat(deserialized.getDomainFileUuid()).isEqualTo("test-uuid");
        assertThat(deserialized.getDomainFileType()).isEqualTo("pdf");
        assertThat(deserialized.getAiRawResponse()).isNotNull();
        assertThat(deserialized.getAiRawResponse().getRole()).isEqualTo("assistant");
        assertThat(deserialized.getAiRawResponse().getContent()).isEqualTo("这是一个销售合同");
    }

    @Test
    @DisplayName("测试空对象处理")
    void testNullObject() {
        // Given
        ClassificationMetadata metadata = new ClassificationMetadata();

        // When & Then - 应该能够处理空对象
        assertThat(metadata.getReason()).isNull();
        assertThat(metadata.getAiRawResponse()).isNull();
        assertThat(metadata.getDomainFileUuid()).isNull();
        assertThat(metadata.getDomainFileType()).isNull();
        assertThat(metadata.getClassificationTimestamp()).isNull();
    }

    @Test
    @DisplayName("测试JSON解析失败时的处理")
    void testInvalidJsonHandling() {
        // Given
        PGobject pgObject = new PGobject();
        try {
            pgObject.setType("jsonb");
            pgObject.setValue("invalid json");
        } catch (SQLException e) {
            // 如果创建失败，跳过测试
            return;
        }

        // When
        ClassificationMetadata metadata = ClassificationMetadata.fromPGobject(pgObject);

        // Then - 解析失败时应该返回空对象
        assertThat(metadata).isNotNull();
        assertThat(metadata.getReason()).isNull();
    }
}