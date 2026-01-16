package com.contract.management.interfaces.dto;

import com.contract.management.domain.model.valueobject.ResourceType;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * 操作日志查询请求测试
 *
 * @author SaltyFish
 * @since 1.0.0
 */
class OperationLogQueryRequestTest {

    @Test
    void testValidRequestWithContractId() {
        OperationLogQueryRequest request = OperationLogQueryRequest.builder()
                .contractId(123L)
                .build();

        assertTrue(request.isValid());
        assertEquals("CONTRACT_ID", request.getQueryType());
        assertEquals(ResourceType.CLAUSE_EXTRACTION, request.getResourceTypeEnum());
    }

    @Test
    void testValidRequestWithExtractionId() {
        OperationLogQueryRequest request = OperationLogQueryRequest.builder()
                .extractionId(456L)
                .resourceType("合同")
                .build();

        assertTrue(request.isValid());
        assertEquals("EXTRACTION_ID", request.getQueryType());
        assertEquals(ResourceType.CONTRACT, request.getResourceTypeEnum());
    }

    @Test
    void testValidRequestWithAllParameters() {
        OperationLogQueryRequest request = OperationLogQueryRequest.builder()
                .contractId(123L)
                .resourceType("文件")
                .build();

        assertTrue(request.isValid());
        assertEquals("CONTRACT_ID", request.getQueryType());
        assertEquals(ResourceType.FILE, request.getResourceTypeEnum());
    }

    @Test
    void testInvalidRequestWithBothIds() {
        OperationLogQueryRequest request = OperationLogQueryRequest.builder()
                .contractId(123L)
                .extractionId(456L)
                .build();

        assertFalse(request.isValid());
    }

    @Test
    void testInvalidRequestWithNoIds() {
        OperationLogQueryRequest request = OperationLogQueryRequest.builder()
                .build();

        assertFalse(request.isValid());
    }

    @Test
    void testInvalidResourceType() {
        OperationLogQueryRequest request = OperationLogQueryRequest.builder()
                .extractionId(456L)
                .resourceType("无效类型")
                .build();

        assertFalse(request.isValid());
    }

    @Test
    void testNullResourceTypeUsesDefault() {
        OperationLogQueryRequest request = OperationLogQueryRequest.builder()
                .extractionId(456L)
                .resourceType(null)
                .build();

        assertTrue(request.isValid());
        assertEquals(ResourceType.CLAUSE_EXTRACTION, request.getResourceTypeEnum());
        assertEquals("条款抽取", request.getResourceTypeEnum().getDescription());
    }

    @Test
    void testEmptyResourceTypeUsesDefault() {
        OperationLogQueryRequest request = OperationLogQueryRequest.builder()
                .extractionId(456L)
                .resourceType("   ")
                .build();

        assertTrue(request.isValid());
        assertEquals(ResourceType.CLAUSE_EXTRACTION, request.getResourceTypeEnum());
        assertEquals("条款抽取", request.getResourceTypeEnum().getDescription());
    }

    @Test
    void testAllValidResourceTypes() {
        ResourceType[] validTypes = ResourceType.values();

        for (ResourceType type : validTypes) {
            OperationLogQueryRequest request = OperationLogQueryRequest.builder()
                    .extractionId(456L)
                    .resourceType(type.getDescription())
                    .build();

            assertTrue(request.isValid(), "Resource type " + type.getDescription() + " should be valid");
            assertEquals(type, request.getResourceTypeEnum());
        }
    }
}