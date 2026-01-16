package com.contract.management.application.service;

import com.contract.management.domain.model.OperationLog;
import com.contract.management.domain.service.OperationLogDomainService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * 操作日志应用服务
 * 提供给内部模块使用的操作日志功能
 *
 * @author SaltyFish
 * @since 1.0.0
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class OperationLogApplicationService {

    private final OperationLogDomainService operationLogDomainService;

    /**
     * 根据合同ID查询条款抽取日志
     * 这个是专门用于查询条款抽取日志的接口
     */
    public List<OperationLog> getClauseExtractionLogsByContractId(Long contractId) {
        log.info("查询合同的条款抽取日志: contractId={}", contractId);
        List<OperationLog> logs = operationLogDomainService.getClauseExtractionLogsByContractId(contractId);
        log.info("查询到合同的条款抽取日志数量: contractId={}, count={}", contractId, logs.size());
        return logs;
    }

    /**
     * 根据资源ID查询操作日志
     */
    public List<OperationLog> getLogsByResourceId(Long resourceId, String resourceType) {
        log.debug("根据资源ID查询操作日志: resourceId={}, resourceType={}", resourceId, resourceType);
        return operationLogDomainService.getLogsByResourceId(resourceId, resourceType);
    }

    /**
     * 根据用户ID查询最近的操作日志
     */
    public List<OperationLog> getRecentLogsByUserId(Long userId, int limit) {
        log.debug("查询用户最近的操作日志: userId={}, limit={}", userId, limit);
        return operationLogDomainService.getLogsByUserId(userId, Math.min(limit, 100)); // 限制最大100条
    }

    /**
     * 根据时间范围查询操作日志
     */
    public List<OperationLog> getLogsByTimeRange(LocalDateTime startTime, LocalDateTime endTime, int limit) {
        log.debug("根据时间范围查询操作日志: startTime={}, endTime={}, limit={}", startTime, endTime, limit);
        return operationLogDomainService.getLogsByTimeRange(startTime, endTime, Math.min(limit, 1000)); // 限制最大1000条
    }

    /**
     * 统计指定时间范围内的操作日志数量
     */
    public long countLogsByTimeRange(LocalDateTime startTime, LocalDateTime endTime) {
        log.debug("统计操作日志数量: startTime={}, endTime={}", startTime, endTime);
        return operationLogDomainService.countLogsByTimeRange(startTime, endTime);
    }

    /**
     * 清理过期日志
     */
    public int cleanupExpiredLogs(int retentionDays) {
        log.info("开始清理过期日志: retentionDays={}", retentionDays);
        return operationLogDomainService.cleanupExpiredLogs(retentionDays);
    }

    /**
     * 获取操作日志详情
     */
    public Optional<OperationLog> getLogById(Long logId) {
        log.debug("查询操作日志详情: logId={}", logId);
        return operationLogDomainService.getLogById(
            com.contract.management.domain.model.valueobject.OperationLogId.of(logId)
        );
    }

    /**
     * 根据查询条件查询操作日志
     * 支持通过contractId或extractionId查询，可指定资源类型
     * 如果resourceType为null，则查询所有日志，按创建时间正序排列
     */
    public List<OperationLog> getOperationLogs(Long contractId, Long extractionId, String resourceType) {
        log.info("查询操作日志: contractId={}, extractionId={}, resourceType={}", contractId, extractionId, resourceType);

        // 如果提供了contractId，先查找最新的extractionId
        if (contractId != null && extractionId == null) {
            extractionId = operationLogDomainService.getLatestExtractionIdByContractId(contractId);
            if (extractionId == null) {
                log.info("未找到合同的操作记录: contractId={}, resourceType={}", contractId, resourceType);
                return List.of();
            }
            log.info("找到合同最新的操作ID: contractId={}, extractionId={}, resourceType={}",
                    contractId, extractionId, resourceType);
        }

        // 使用extractionId查询日志
        if (extractionId != null) {
            List<OperationLog> logs;
            if (resourceType != null) {
                // 查询指定资源类型的日志
                logs = operationLogDomainService.getOperationLogsByExtractionId(extractionId, resourceType);
            } else {
                // 查询所有日志，按创建时间正序排列
                logs = operationLogDomainService.getAllOperationLogsByExtractionId(extractionId);
            }
            log.info("查询到操作日志数量: extractionId={}, resourceType={}, count={}",
                    extractionId, resourceType, logs.size());
            return logs;
        }

        log.warn("无效的查询参数: contractId={}, extractionId={}, resourceType={}",
                contractId, extractionId, resourceType);
        return List.of();
    }
}