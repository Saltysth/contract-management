package com.contract.management.domain.service;

import com.contract.management.domain.model.OperationLog;
import com.contract.management.domain.model.valueobject.OperationLogId;
import com.contract.management.domain.repository.OperationLogRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * 操作日志领域服务
 *
 * @author SaltyFish
 * @since 1.0.0
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class OperationLogDomainService {

    private final OperationLogRepository operationLogRepository;

    /**
     * 记录操作日志
     */
    public OperationLog recordLog(OperationLog operationLog) {
        try {
            OperationLog savedLog = operationLogRepository.save(operationLog);
            log.debug("操作日志记录成功: {}", savedLog);
            return savedLog;
        } catch (Exception e) {
            log.error("记录操作日志失败", e);
            // 记录日志失败不应该影响主业务流程，所以返回null而不是抛出异常
            return null;
        }
    }

    /**
     * 根据ID查询操作日志
     */
    public Optional<OperationLog> getLogById(OperationLogId id) {
        try {
            OperationLog operationLog = operationLogRepository.findById(id);
            return Optional.ofNullable(operationLog);
        } catch (Exception e) {
            log.error("查询操作日志失败: id={}", id, e);
            return Optional.empty();
        }
    }

    /**
     * 根据资源ID查询操作日志
     */
    public List<OperationLog> getLogsByResourceId(Long resourceId, String resourceType) {
        try {
            return operationLogRepository.findByResourceId(resourceId, resourceType);
        } catch (Exception e) {
            log.error("根据资源ID查询操作日志失败: resourceId={}, resourceType={}", resourceId, resourceType, e);
            return List.of();
        }
    }

    /**
     * 根据合同ID查询条款抽取相关的操作日志
     */
    public List<OperationLog> getClauseExtractionLogsByContractId(Long contractId) {
        try {
            List<OperationLog> logs = operationLogRepository.findClauseExtractionLogsByContractId(contractId);
            log.debug("查询到合同的条款抽取日志数量: contractId={}, count={}", contractId, logs.size());
            return logs;
        } catch (Exception e) {
            log.error("根据合同ID查询条款抽取日志失败: contractId={}", contractId, e);
            return List.of();
        }
    }

    /**
     * 根据用户ID查询操作日志
     */
    public List<OperationLog> getLogsByUserId(Long userId, int limit) {
        try {
            return operationLogRepository.findByUserId(userId, limit);
        } catch (Exception e) {
            log.error("根据用户ID查询操作日志失败: userId={}", userId, e);
            return List.of();
        }
    }

    /**
     * 根据时间范围查询操作日志
     */
    public List<OperationLog> getLogsByTimeRange(LocalDateTime startTime, LocalDateTime endTime, int limit) {
        try {
            return operationLogRepository.findByTimeRange(startTime, endTime, limit);
        } catch (Exception e) {
            log.error("根据时间范围查询操作日志失败: startTime={}, endTime={}", startTime, endTime, e);
            return List.of();
        }
    }

    /**
     * 清理过期日志
     */
    public int cleanupExpiredLogs(int retentionDays) {
        try {
            LocalDateTime cutoffTime = LocalDateTime.now().minusDays(retentionDays);
            int deletedCount = operationLogRepository.deleteLogsBefore(cutoffTime);
            log.info("清理过期日志完成: 删除{}天前的日志，删除数量: {}", retentionDays, deletedCount);
            return deletedCount;
        } catch (Exception e) {
            log.error("清理过期日志失败: retentionDays={}", retentionDays, e);
            return 0;
        }
    }

    /**
     * 统计指定时间范围内的操作日志数量
     */
    public long countLogsByTimeRange(LocalDateTime startTime, LocalDateTime endTime) {
        try {
            return operationLogRepository.countByTimeRange(startTime, endTime);
        } catch (Exception e) {
            log.error("统计操作日志数量失败: startTime={}, endTime={}", startTime, endTime, e);
            return 0;
        }
    }

    /**
     * 记录条款抽取开始日志
     */
    public void recordClauseExtractionStart(Long extractionId, Long contractId, Long userId, String ipAddress) {
        OperationLog log = OperationLog.createClauseExtractionStart(extractionId, contractId, userId, ipAddress);
        recordLog(log);
    }

    /**
     * 记录条款抽取成功日志
     */
    public void recordClauseExtractionSuccess(Long extractionId, Long contractId, Long userId,
                                             String ipAddress, Long executionTimeMs, String resultSummary) {
        OperationLog log = OperationLog.createClauseExtractionSuccess(
            extractionId, contractId, userId, ipAddress, executionTimeMs, resultSummary
        );
        recordLog(log);
    }

    /**
     * 记录条款抽取失败日志
     */
    public void recordClauseExtractionFailure(Long extractionId, Long contractId, Long userId,
                                             String ipAddress, Long executionTimeMs, String errorMessage) {
        OperationLog log = OperationLog.createClauseExtractionFailure(
            extractionId, contractId, userId, ipAddress, executionTimeMs, errorMessage
        );
        recordLog(log);
    }

    /**
     * 记录条款抽取取消日志
     */
    public void recordClauseExtractionCancel(Long extractionId, Long contractId, Long userId,
                                            String ipAddress, Long executionTimeMs) {
        // 可以扩展OperationLog来支持取消操作
        log.info("条款抽取任务被取消: extractionId={}, contractId={}, userId={}",
                extractionId, contractId, userId);
    }

    /**
     * 根据抽取ID查询指定资源类型的操作日志
     */
    public List<OperationLog> getOperationLogsByExtractionId(Long extractionId, String resourceType) {
        try {
            List<OperationLog> logs = operationLogRepository.findOperationLogsByExtractionId(extractionId, resourceType);
            log.debug("查询到抽取ID的操作日志数量: extractionId={}, resourceType={}, count={}",
                     extractionId, resourceType, logs.size());
            return logs;
        } catch (Exception e) {
            log.error("根据抽取ID查询操作日志失败: extractionId={}, resourceType={}", extractionId, resourceType, e);
            return List.of();
        }
    }

    /**
     * 根据抽取ID查询所有操作日志，按创建时间正序排列
     */
    public List<OperationLog> getAllOperationLogsByExtractionId(Long extractionId) {
        try {
            List<OperationLog> logs = operationLogRepository.findAllOperationLogsByExtractionId(extractionId);
            log.debug("查询到抽取ID的所有操作日志数量: extractionId={}, count={}",
                     extractionId, logs.size());
            return logs;
        } catch (Exception e) {
            log.error("根据抽取ID查询所有操作日志失败: extractionId={}", extractionId, e);
            return List.of();
        }
    }

    /**
     * 根据合同ID查找最新的条款抽取ID
     */
    public Long getLatestExtractionIdByContractId(Long contractId) {
        try {
            Long extractionId = operationLogRepository.findLatestExtractionIdByContractId(contractId);
            log.debug("查询到合同最新的条款抽取ID: contractId={}, extractionId={}", contractId, extractionId);
            return extractionId;
        } catch (Exception e) {
            log.error("根据合同ID查找最新的条款抽取ID失败: contractId={}", contractId, e);
            return null;
        }
    }
}