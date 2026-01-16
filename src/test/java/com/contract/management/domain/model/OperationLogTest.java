package com.contract.management.domain.model;

import com.contract.management.domain.model.valueobject.OperationType;
import com.contract.management.domain.model.valueobject.ResourceType;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * 操作日志领域模型测试
 *
 * @author SaltyFish
 * @since 1.0.0
 */
class OperationLogTest {

    @Test
    void testCreateClauseExtractionStart() {
        // Given
        Long extractionId = 12345L;
        Long contractId = 1001L;
        Long userId = 1L;
        String ipAddress = "127.0.0.1";

        // When
        OperationLog log = OperationLog.createClauseExtractionStart(
            extractionId, contractId, userId, ipAddress
        );

        // Then
        assertNotNull(log);
        assertEquals(OperationType.CLAUSE_EXTRACTION_START, log.getOperationType());
        assertEquals(ResourceType.CLAUSE_EXTRACTION, log.getResourceType());
        assertEquals(extractionId, log.getResourceId());
        assertEquals(userId, log.getUserId());
        assertEquals(ipAddress, log.getIpAddress());
        assertTrue(log.isInProgressOperation());
        assertTrue(log.isClauseExtractionOperation());
    }

    @Test
    void testCreateClauseExtractionSuccess() {
        // Given
        Long extractionId = 12345L;
        Long contractId = 1001L;
        Long userId = 1L;
        String ipAddress = "127.0.0.1";
        Long executionTimeMs = 5000L;
        String resultSummary = "成功抽取5个条款";

        // When
        OperationLog log = OperationLog.createClauseExtractionSuccess(
            extractionId, contractId, userId, ipAddress, executionTimeMs, resultSummary
        );

        // Then
        assertNotNull(log);
        assertEquals(OperationType.CLAUSE_EXTRACTION_SUCCESS, log.getOperationType());
        assertEquals(ResourceType.CLAUSE_EXTRACTION, log.getResourceType());
        assertEquals(extractionId, log.getResourceId());
        assertEquals(userId, log.getUserId());
        assertEquals(executionTimeMs, log.getExecutionTimeMs());
        assertTrue(log.isSuccessOperation());
        assertTrue(log.isClauseExtractionOperation());
    }

    @Test
    void testCreateClauseExtractionFailure() {
        // Given
        Long extractionId = 12345L;
        Long contractId = 1001L;
        Long userId = 1L;
        String ipAddress = "127.0.0.1";
        Long executionTimeMs = 3000L;
        String errorMessage = "文件解析失败";

        // When
        OperationLog log = OperationLog.createClauseExtractionFailure(
            extractionId, contractId, userId, ipAddress, executionTimeMs, errorMessage
        );

        // Then
        assertNotNull(log);
        assertEquals(OperationType.CLAUSE_EXTRACTION_FAILED, log.getOperationType());
        assertEquals(ResourceType.CLAUSE_EXTRACTION, log.getResourceType());
        assertEquals(extractionId, log.getResourceId());
        assertEquals(userId, log.getUserId());
        assertEquals(executionTimeMs, log.getExecutionTimeMs());
        assertTrue(log.isFailureOperation());
        assertTrue(log.isClauseExtractionOperation());
    }

    @Test
    void testOperationLogValidation() {
        // Given & When & Then
        assertThrows(IllegalArgumentException.class, () -> {
            new OperationLog(
                com.contract.management.domain.model.valueobject.OperationLogId.of(1L),
                null, // 操作类型为空
                ResourceType.CLAUSE_EXTRACTION,
                12345L,
                com.contract.management.domain.model.valueobject.OperationDetails.success("测试"),
                1L,
                "127.0.0.1",
                null,
                null
            );
        });

        assertThrows(IllegalArgumentException.class, () -> {
            new OperationLog(
                com.contract.management.domain.model.valueobject.OperationLogId.of(1L),
                OperationType.CLAUSE_EXTRACTION_START,
                null, // 资源类型为空
                12345L,
                com.contract.management.domain.model.valueobject.OperationDetails.success("测试"),
                1L,
                "127.0.0.1",
                null,
                null
            );
        });

        assertThrows(IllegalArgumentException.class, () -> {
            new OperationLog(
                com.contract.management.domain.model.valueobject.OperationLogId.of(1L),
                OperationType.CLAUSE_EXTRACTION_START,
                ResourceType.CLAUSE_EXTRACTION,
                12345L,
                null, // 操作详情为空
                1L,
                "127.0.0.1",
                null,
                null
            );
        });

        assertThrows(IllegalArgumentException.class, () -> {
            new OperationLog(
                com.contract.management.domain.model.valueobject.OperationLogId.of(1L),
                OperationType.CLAUSE_EXTRACTION_START,
                ResourceType.CLAUSE_EXTRACTION,
                12345L,
                com.contract.management.domain.model.valueobject.OperationDetails.success("测试"),
                0L, // 用户ID无效
                "127.0.0.1",
                null,
                null
            );
        });
    }
}