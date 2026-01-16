package com.contract.management.domain.repository;

import com.contract.management.domain.model.OperationLog;
import com.contract.management.domain.model.valueobject.OperationLogId;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 操作日志仓储接口
 *
 * @author SaltyFish
 * @since 1.0.0
 */
public interface OperationLogRepository {

    /**
     * 保存操作日志
     */
    OperationLog save(OperationLog operationLog);

    /**
     * 根据ID查找操作日志
     */
    OperationLog findById(OperationLogId id);

    /**
     * 根据资源ID查找操作日志
     */
    List<OperationLog> findByResourceId(Long resourceId, String resourceType);

    /**
     * 根据合同ID查找条款抽取相关的操作日志
     */
    List<OperationLog> findClauseExtractionLogsByContractId(Long contractId);

    /**
     * 根据用户ID查找操作日志
     */
    List<OperationLog> findByUserId(Long userId, int limit);

    /**
     * 根据时间范围查找操作日志
     */
    List<OperationLog> findByTimeRange(LocalDateTime startTime, LocalDateTime endTime, int limit);

    /**
     * 删除指定时间之前的日志
     */
    int deleteLogsBefore(LocalDateTime beforeTime);

    /**
     * 统计指定时间范围内的操作日志数量
     */
    long countByTimeRange(LocalDateTime startTime, LocalDateTime endTime);

    /**
     * 根据抽取ID查找指定资源类型的操作日志
     */
    List<OperationLog> findOperationLogsByExtractionId(Long extractionId, String resourceType);

    /**
     * 根据抽取ID查找所有操作日志，按创建时间正序排列
     */
    List<OperationLog> findAllOperationLogsByExtractionId(Long extractionId);

    /**
     * 根据合同ID查找最新的条款抽取ID
     */
    Long findLatestExtractionIdByContractId(Long contractId);
}