package com.contract.management.domain.service;

import com.contract.management.domain.model.OperationLog;
import com.contract.management.domain.model.valueobject.OperationLogId;
import com.contract.management.domain.repository.OperationLogRepository;
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
 * 操作日志领域服务测试
 *
 * @author SaltyFish
 * @since 1.0.0
 */
@ExtendWith(MockitoExtension.class)
class OperationLogDomainServiceTest {

    @Mock
    private OperationLogRepository operationLogRepository;

    @InjectMocks
    private OperationLogDomainService operationLogDomainService;

    private OperationLog testOperationLog;
    private Long testContractId = 1001L;
    private Long testExtractionId = 12345L;
    private Long testUserId = 1L;

    @BeforeEach
    void setUp() {
        // 创建测试用的操作日志
        testOperationLog = OperationLog.createClauseExtractionStart(
            testExtractionId, testContractId, testUserId, "127.0.0.1"
        );
    }

    @Test
    void testRecordLog_Success() {
        // Given - 创建一个模拟已保存的操作日志（有ID）
        OperationLog savedLog = OperationLog.createClauseExtractionStart(
            testExtractionId, testContractId, testUserId, "127.0.0.1"
        );
        // 使用反射设置ID，模拟数据库生成的ID
        try {
            java.lang.reflect.Field idField = OperationLog.class.getDeclaredField("id");
            idField.setAccessible(true);
            idField.set(savedLog, OperationLogId.of(123L));
        } catch (Exception e) {
            throw new RuntimeException("Failed to set test ID", e);
        }

        when(operationLogRepository.save(any(OperationLog.class))).thenReturn(savedLog);

        // When
        OperationLog result = operationLogDomainService.recordLog(testOperationLog);

        // Then
        assertNotNull(result);
        assertEquals(savedLog.getId(), result.getId());
        verify(operationLogRepository, times(1)).save(testOperationLog);
    }

    @Test
    void testRecordLog_Failure() {
        // Given
        when(operationLogRepository.save(any(OperationLog.class)))
            .thenThrow(new RuntimeException("Database error"));

        // When
        OperationLog result = operationLogDomainService.recordLog(testOperationLog);

        // Then
        assertNull(result);
        verify(operationLogRepository, times(1)).save(testOperationLog);
    }

    @Test
    void testGetLogById_Success() {
        // Given - 使用一个具体的ID来测试
        OperationLogId testId = OperationLogId.of(123L);
        when(operationLogRepository.findById(testId))
            .thenReturn(testOperationLog);

        // When
        Optional<OperationLog> result = operationLogDomainService.getLogById(testId);

        // Then
        assertTrue(result.isPresent());
        assertEquals(testOperationLog, result.get());
        verify(operationLogRepository, times(1)).findById(testId);
    }

    @Test
    void testGetLogById_NotFound() {
        // Given
        when(operationLogRepository.findById(any(OperationLogId.class)))
            .thenReturn(null);

        // When
        Optional<OperationLog> result = operationLogDomainService.getLogById(
            OperationLogId.of(999L)
        );

        // Then
        assertFalse(result.isPresent());
        verify(operationLogRepository, times(1)).findById(any(OperationLogId.class));
    }

    @Test
    void testGetClauseExtractionLogsByContractId() {
        // Given
        List<OperationLog> expectedLogs = Arrays.asList(testOperationLog);
        when(operationLogRepository.findClauseExtractionLogsByContractId(testContractId))
            .thenReturn(expectedLogs);

        // When
        List<OperationLog> result = operationLogDomainService.getClauseExtractionLogsByContractId(testContractId);

        // Then
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(testOperationLog, result.get(0));
        verify(operationLogRepository, times(1))
            .findClauseExtractionLogsByContractId(testContractId);
    }

    @Test
    void testGetLogsByUserId() {
        // Given
        List<OperationLog> expectedLogs = Arrays.asList(testOperationLog);
        when(operationLogRepository.findByUserId(testUserId, 10))
            .thenReturn(expectedLogs);

        // When
        List<OperationLog> result = operationLogDomainService.getLogsByUserId(testUserId, 10);

        // Then
        assertNotNull(result);
        assertEquals(1, result.size());
        verify(operationLogRepository, times(1)).findByUserId(testUserId, 10);
    }

    @Test
    void testGetLogsByTimeRange() {
        // Given
        LocalDateTime startTime = LocalDateTime.now().minusHours(1);
        LocalDateTime endTime = LocalDateTime.now();
        List<OperationLog> expectedLogs = Arrays.asList(testOperationLog);
        when(operationLogRepository.findByTimeRange(startTime, endTime, 100))
            .thenReturn(expectedLogs);

        // When
        List<OperationLog> result = operationLogDomainService.getLogsByTimeRange(startTime, endTime, 100);

        // Then
        assertNotNull(result);
        assertEquals(1, result.size());
        verify(operationLogRepository, times(1)).findByTimeRange(startTime, endTime, 100);
    }

    @Test
    void testCountLogsByTimeRange() {
        // Given
        LocalDateTime startTime = LocalDateTime.now().minusHours(1);
        LocalDateTime endTime = LocalDateTime.now();
        long expectedCount = 5L;
        when(operationLogRepository.countByTimeRange(startTime, endTime))
            .thenReturn(expectedCount);

        // When
        long result = operationLogDomainService.countLogsByTimeRange(startTime, endTime);

        // Then
        assertEquals(expectedCount, result);
        verify(operationLogRepository, times(1)).countByTimeRange(startTime, endTime);
    }

    @Test
    void testCleanupExpiredLogs() {
        // Given
        int retentionDays = 30;
        int expectedDeletedCount = 100;
        when(operationLogRepository.deleteLogsBefore(any(LocalDateTime.class)))
            .thenReturn(expectedDeletedCount);

        // When
        int result = operationLogDomainService.cleanupExpiredLogs(retentionDays);

        // Then
        assertEquals(expectedDeletedCount, result);
        verify(operationLogRepository, times(1)).deleteLogsBefore(any(LocalDateTime.class));
    }

    @Test
    void testRecordClauseExtractionStart() {
        // Given
        when(operationLogRepository.save(any(OperationLog.class))).thenReturn(testOperationLog);

        // When
        operationLogDomainService.recordClauseExtractionStart(
            testExtractionId, testContractId, testUserId, "127.0.0.1"
        );

        // Then
        verify(operationLogRepository, times(1)).save(any(OperationLog.class));
    }

    @Test
    void testRecordClauseExtractionSuccess() {
        // Given
        when(operationLogRepository.save(any(OperationLog.class))).thenReturn(testOperationLog);

        // When
        operationLogDomainService.recordClauseExtractionSuccess(
            testExtractionId, testContractId, testUserId, "127.0.0.1", 5000L, "成功抽取5个条款"
        );

        // Then
        verify(operationLogRepository, times(1)).save(any(OperationLog.class));
    }

    @Test
    void testRecordClauseExtractionFailure() {
        // Given
        when(operationLogRepository.save(any(OperationLog.class))).thenReturn(testOperationLog);

        // When
        operationLogDomainService.recordClauseExtractionFailure(
            testExtractionId, testContractId, testUserId, "127.0.0.1", 3000L, "文件解析失败"
        );

        // Then
        verify(operationLogRepository, times(1)).save(any(OperationLog.class));
    }
}