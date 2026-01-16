package com.contract.management.integration;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doAnswer;

import com.contract.management.application.service.OperationLogApplicationService;
import com.contract.management.domain.model.OperationLog;
import com.contract.management.domain.model.valueobject.ExtractionId;
import com.contract.management.domain.service.ClauseExtractionDomainService;
import com.contract.management.infrastructure.service.ClauseExtractionService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;
import java.util.concurrent.CompletableFuture;

/**
 * 条款抽取和操作日志集成测试
 *
 * @author SaltyFish
 * @since 1.0.0
 */
@SpringBootTest
@ActiveProfiles("test")
class ClauseExtractionLogIntegrationTest {

    @Autowired
    private OperationLogApplicationService operationLogApplicationService;

    @Autowired
    private ClauseExtractionDomainService clauseExtractionDomainService;

    @MockBean
    private ClauseExtractionService clauseExtractionService;

    @Test
    void testClauseExtractionLogIntegration() {
        // Given
        Long contractId = 1001L;
        Long userId = 1L;
        String fileUuid = "test-file-uuid";

        // 模拟异步执行成功
        doAnswer(invocation -> {
            // 模拟异步执行
            CompletableFuture.runAsync(() -> {
                try {
                    Thread.sleep(100); // 模拟处理时间
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            });
            return null;
        }).when(clauseExtractionService).extractClausesAsync(any(ExtractionId.class), eq(contractId), eq(fileUuid));

        // When - 执行条款抽取
        clauseExtractionDomainService.performExtractionAsync(contractId, userId, fileUuid);

        // Then - 验证日志记录
        // 注意：由于异步执行，可能需要等待一段时间
        try {
            Thread.sleep(200);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        // 验证能够查询到条款抽取日志
        List<OperationLog> logs = operationLogApplicationService
            .getClauseExtractionLogsByContractId(contractId);

        // 至少应该有开始日志
        assertFalse(logs.isEmpty());

        // 验证日志内容
        OperationLog startLog = logs.stream()
            .filter(log -> log.isClauseExtractionOperation())
            .findFirst()
            .orElse(null);

        assertNotNull(startLog);
        assertTrue(startLog.isInProgressOperation() || startLog.isSuccessOperation() || startLog.isFailureOperation());
    }

    @Test
    void testGetClauseExtractionLogsByContractId() {
        // Given
        Long contractId = 1002L;

        // When - 查询合同相关的条款抽取日志
        List<OperationLog> logs = operationLogApplicationService
            .getClauseExtractionLogsByContractId(contractId);

        // Then - 验证返回结果
        assertNotNull(logs);
        // 可能是空列表，但不应该是null
        assertTrue(logs.stream().allMatch(OperationLog::isClauseExtractionOperation));
    }

    @Test
    void testOperationLogApplicationService() {
        // Given
        Long contractId = 1003L;

        // When - 测试各种查询方法
        List<OperationLog> clauseLogs = operationLogApplicationService
            .getClauseExtractionLogsByContractId(contractId);

        List<OperationLog> recentLogs = operationLogApplicationService
            .getRecentLogsByUserId(1L, 10);

        // Then - 验证结果不为null
        assertNotNull(clauseLogs);
        assertNotNull(recentLogs);
    }
}