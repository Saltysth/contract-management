package com.contract.management.application.service;

import com.contract.management.domain.model.OperationLog;
import com.contract.management.domain.service.OperationLogDomainService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

/**
 * 操作日志应用服务测试
 *
 * @author SaltyFish
 * @since 1.0.0
 */
@ExtendWith(MockitoExtension.class)
class OperationLogApplicationServiceTest {

    @Mock
    private OperationLogDomainService operationLogDomainService;

    @InjectMocks
    private OperationLogApplicationService operationLogApplicationService;

    private OperationLog testOperationLog;
    private Long testContractId = 1001L;
    private Long testUserId = 1L;

    @BeforeEach
    void setUp() {
        testOperationLog = OperationLog.createClauseExtractionStart(
            12345L, testContractId, testUserId, "127.0.0.1"
        );
    }

    @Test
    void testGetClauseExtractionLogsByContractId() {
        // Given
        List<OperationLog> expectedLogs = Arrays.asList(testOperationLog);
        when(operationLogDomainService.getClauseExtractionLogsByContractId(testContractId))
            .thenReturn(expectedLogs);

        // When
        List<OperationLog> result = operationLogApplicationService
            .getClauseExtractionLogsByContractId(testContractId);

        // Then
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(testOperationLog, result.get(0));
        verify(operationLogDomainService, times(1))
            .getClauseExtractionLogsByContractId(testContractId);
    }

    @Test
    void testGetLogsByResourceId() {
        // Given
        Long resourceId = 12345L;
        String resourceType = "条款抽取";
        List<OperationLog> expectedLogs = Arrays.asList(testOperationLog);
        when(operationLogDomainService.getLogsByResourceId(resourceId, resourceType))
            .thenReturn(expectedLogs);

        // When
        List<OperationLog> result = operationLogApplicationService
            .getLogsByResourceId(resourceId, resourceType);

        // Then
        assertNotNull(result);
        assertEquals(1, result.size());
        verify(operationLogDomainService, times(1))
            .getLogsByResourceId(resourceId, resourceType);
    }

    @Test
    void testGetRecentLogsByUserId() {
        // Given
        List<OperationLog> expectedLogs = Arrays.asList(testOperationLog);
        when(operationLogDomainService.getLogsByUserId(testUserId, 50))
            .thenReturn(expectedLogs);

        // When
        List<OperationLog> result = operationLogApplicationService
            .getRecentLogsByUserId(testUserId, 50);

        // Then
        assertNotNull(result);
        assertEquals(1, result.size());
        verify(operationLogDomainService, times(1)).getLogsByUserId(testUserId, 50);
    }

    @Test
    void testGetRecentLogsByUserId_ExceedsLimit() {
        // Given
        when(operationLogDomainService.getLogsByUserId(testUserId, 100))
            .thenReturn(Arrays.asList(testOperationLog));

        // When
        List<OperationLog> result = operationLogApplicationService
            .getRecentLogsByUserId(testUserId, 150);

        // Then
        assertNotNull(result);
        // 验证调用时限制了最大数量为100
        verify(operationLogDomainService, times(1)).getLogsByUserId(testUserId, 100);
    }

    @Test
    void testGetLogsByTimeRange() {
        // Given
        LocalDateTime startTime = LocalDateTime.now().minusHours(1);
        LocalDateTime endTime = LocalDateTime.now();
        List<OperationLog> expectedLogs = Arrays.asList(testOperationLog);
        when(operationLogDomainService.getLogsByTimeRange(startTime, endTime, 500))
            .thenReturn(expectedLogs);

        // When
        List<OperationLog> result = operationLogApplicationService
            .getLogsByTimeRange(startTime, endTime, 500);

        // Then
        assertNotNull(result);
        assertEquals(1, result.size());
        verify(operationLogDomainService, times(1))
            .getLogsByTimeRange(startTime, endTime, 500);
    }

    @Test
    void testGetLogsByTimeRange_ExceedsLimit() {
        // Given
        LocalDateTime startTime = LocalDateTime.now().minusHours(1);
        LocalDateTime endTime = LocalDateTime.now();
        when(operationLogDomainService.getLogsByTimeRange(startTime, endTime, 1000))
            .thenReturn(Arrays.asList(testOperationLog));

        // When
        List<OperationLog> result = operationLogApplicationService
            .getLogsByTimeRange(startTime, endTime, 1500);

        // Then
        assertNotNull(result);
        // 验证调用时限制了最大数量为1000
        verify(operationLogDomainService, times(1))
            .getLogsByTimeRange(startTime, endTime, 1000);
    }

    @Test
    void testCountLogsByTimeRange() {
        // Given
        LocalDateTime startTime = LocalDateTime.now().minusHours(1);
        LocalDateTime endTime = LocalDateTime.now();
        long expectedCount = 10L;
        when(operationLogDomainService.countLogsByTimeRange(startTime, endTime))
            .thenReturn(expectedCount);

        // When
        long result = operationLogApplicationService.countLogsByTimeRange(startTime, endTime);

        // Then
        assertEquals(expectedCount, result);
        verify(operationLogDomainService, times(1))
            .countLogsByTimeRange(startTime, endTime);
    }

    @Test
    void testCleanupExpiredLogs() {
        // Given
        int retentionDays = 30;
        int expectedDeletedCount = 50;
        when(operationLogDomainService.cleanupExpiredLogs(retentionDays))
            .thenReturn(expectedDeletedCount);

        // When
        int result = operationLogApplicationService.cleanupExpiredLogs(retentionDays);

        // Then
        assertEquals(expectedDeletedCount, result);
        verify(operationLogDomainService, times(1)).cleanupExpiredLogs(retentionDays);
    }

    @Test
    void testGetLogById() {
        // Given
        Long logId = 12345L;
        when(operationLogDomainService.getLogById(any()))
            .thenReturn(Optional.of(testOperationLog));

        // When
        Optional<OperationLog> result = operationLogApplicationService.getLogById(logId);

        // Then
        assertTrue(result.isPresent());
        assertEquals(testOperationLog, result.get());
        verify(operationLogDomainService, times(1)).getLogById(any());
    }

    @Test
    void testGetLogById_NotFound() {
        // Given
        Long logId = 99999L;
        when(operationLogDomainService.getLogById(any()))
            .thenReturn(Optional.empty());

        // When
        Optional<OperationLog> result = operationLogApplicationService.getLogById(logId);

        // Then
        assertFalse(result.isPresent());
        verify(operationLogDomainService, times(1)).getLogById(any());
    }
}