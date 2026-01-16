package com.contract.management.infrastructure.repository;

import com.contract.management.domain.model.OperationLog;
import com.contract.management.domain.model.valueobject.OperationDetails;
import com.contract.management.domain.model.valueobject.OperationLogId;
import com.contract.management.domain.model.valueobject.OperationType;
import com.contract.management.domain.model.valueobject.ResourceType;
import com.contract.management.domain.repository.OperationLogRepository;
import com.contract.management.infrastructure.entity.OperationLogEntity;
import com.contract.management.infrastructure.mapper.OperationLogMapper;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 操作日志仓储实现
 *
 * @author SaltyFish
 * @since 1.0.0
 */
@Slf4j
@Repository
@RequiredArgsConstructor
public class OperationLogRepositoryImpl implements OperationLogRepository {

    private final OperationLogMapper operationLogMapper;
    private final ObjectMapper objectMapper;

    @Override
    public OperationLog save(OperationLog operationLog) {
        OperationLogEntity entity = toEntity(operationLog);

        if (entity.getId() == null) {
            operationLogMapper.insert(entity);
        } else {
            // 使用自定义的更新方法，正确处理JSONB类型
            operationLogMapper.updateOperationLogWithJsonb(entity);
        }

        return toDomain(entity);
    }

    @Override
    public OperationLog findById(OperationLogId id) {
        OperationLogEntity entity = operationLogMapper.selectById(id.getValue());
        return entity != null ? toDomain(entity) : null;
    }

    @Override
    public List<OperationLog> findByResourceId(Long resourceId, String resourceType) {
        List<OperationLogEntity> entities = operationLogMapper.findByResourceId(resourceId, resourceType);
        return entities.stream()
                .map(this::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public List<OperationLog> findClauseExtractionLogsByContractId(Long contractId) {
        List<OperationLogEntity> entities = operationLogMapper.findClauseExtractionLogsByContractId(contractId);
        return entities.stream()
                .map(this::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public List<OperationLog> findByUserId(Long userId, int limit) {
        List<OperationLogEntity> entities = operationLogMapper.findByUserId(userId, limit);
        return entities.stream()
                .map(this::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public List<OperationLog> findByTimeRange(LocalDateTime startTime, LocalDateTime endTime, int limit) {
        List<OperationLogEntity> entities = operationLogMapper.findByTimeRange(startTime, endTime, limit);
        return entities.stream()
                .map(this::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public int deleteLogsBefore(LocalDateTime beforeTime) {
        return operationLogMapper.deleteLogsBefore(beforeTime);
    }

    @Override
    public long countByTimeRange(LocalDateTime startTime, LocalDateTime endTime) {
        return operationLogMapper.countByTimeRange(startTime, endTime);
    }

    @Override
    public List<OperationLog> findOperationLogsByExtractionId(Long extractionId, String resourceType) {
        List<OperationLogEntity> entities = operationLogMapper.findOperationLogsByExtractionId(extractionId, resourceType);
        return entities.stream()
                .map(this::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public List<OperationLog> findAllOperationLogsByExtractionId(Long extractionId) {
        List<OperationLogEntity> entities = operationLogMapper.findAllOperationLogsByExtractionId(extractionId);
        return entities.stream()
                .map(this::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public Long findLatestExtractionIdByContractId(Long contractId) {
        return operationLogMapper.findLatestExtractionIdByContractId(contractId);
    }

    /**
     * 领域模型转实体
     */
    private OperationLogEntity toEntity(OperationLog domain) {
        if (domain == null) {
            return null;
        }

        OperationLogEntity entity = new OperationLogEntity();
        // 只有当ID不为null时才设置，否则让数据库自动生成
        if (domain.getId() != null) {
            entity.setId(domain.getId().getValue());
        }
        entity.setOperationType(domain.getOperationType().name());
        entity.setResourceType(domain.getResourceType().getDescription());
        entity.setResourceId(domain.getResourceId());
        entity.setUserId(domain.getUserId());
        entity.setIpAddress(domain.getIpAddress());
        entity.setUserAgent(domain.getUserAgent());
        entity.setExecutionTimeMs(domain.getExecutionTimeMs());
        entity.setCreatedTime(domain.getCreatedTime());

        // 设置操作详情（MyBatis会通过JsonbTypeHandler自动处理JSON序列化）
        entity.setOperationDetails(domain.getOperationDetails());

        return entity;
    }

    /**
     * 实体转领域模型
     */
    private OperationLog toDomain(OperationLogEntity entity) {
        if (entity == null) {
            return null;
        }

        // 获取操作详情（MyBatis会通过JsonbTypeHandler自动处理JSON反序列化）
        OperationDetails operationDetails = entity.getOperationDetails();

        OperationType operationType = OperationType.valueOf(entity.getOperationType());
        ResourceType resourceType = ResourceType.fromDescription(entity.getResourceType());

        return new OperationLog(
            OperationLogId.of(entity.getId()),
            operationType,
            resourceType,
            entity.getResourceId(),
            operationDetails,
            entity.getUserId(),
            entity.getIpAddress(),
            entity.getUserAgent(),
            entity.getExecutionTimeMs(),
            entity.getCreatedTime()
        );
    }
}