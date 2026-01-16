package com.contract.management.infrastructure.repository;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.contract.management.domain.model.ClauseExtraction;
import com.contract.management.domain.model.valueobject.AuditInfo;
import com.contract.management.domain.model.valueobject.ClauseExtractionResult;
import com.contract.management.domain.model.valueobject.ExtractionId;
import com.contract.management.domain.model.valueobject.ExtractionStatus;
import com.contract.management.domain.repository.ClauseExtractionRepository;
import com.contract.management.infrastructure.entity.ClauseExtractionEntity;
import com.contract.management.infrastructure.mapper.ClauseExtractionMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
/**
 * 条款抽取仓储实现
 *
 * @author SaltyFish
 * @since 1.0.0
 */
@Slf4j
@Repository
public class ClauseExtractionRepositoryImpl implements ClauseExtractionRepository {

    private final ClauseExtractionMapper clauseExtractionMapper;

    public ClauseExtractionRepositoryImpl(ClauseExtractionMapper clauseExtractionMapper) {
        this.clauseExtractionMapper = clauseExtractionMapper;
    }

    @Override
    public ClauseExtraction save(ClauseExtraction clauseExtraction) {
        ClauseExtractionEntity entity = toEntity(clauseExtraction);

        if (entity.getId() == null) {
            clauseExtractionMapper.insert(entity);
        } else {
            clauseExtractionMapper.updateById(entity);
        }

        return toDomain(entity);
    }

    @Override
    public ClauseExtraction findById(ExtractionId id) {
        ClauseExtractionEntity entity = clauseExtractionMapper.selectById(id.getValue());
        return entity != null ? toDomain(entity) : null;
    }

    @Override
    public ClauseExtraction findByContractId(Long contractId) {
        LambdaQueryWrapper<ClauseExtractionEntity> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(ClauseExtractionEntity::getContractId, contractId)
               .eq(ClauseExtractionEntity::getIsDeleted, false)
               .orderByDesc(ClauseExtractionEntity::getCreatedTime)
               .last("LIMIT 1");

        ClauseExtractionEntity entity = clauseExtractionMapper.selectOne(wrapper);
        return entity != null ? toDomain(entity) : null;
    }

    @Override
    @Transactional
    public ClauseExtraction update(ClauseExtraction clauseExtraction) {
        // 先查询现有实体，获取更新后的实体（包含版本号）
        ClauseExtractionEntity existingEntity = clauseExtractionMapper.selectById(clauseExtraction.getId().getValue());
        if (existingEntity == null || existingEntity.getIsDeleted()) {
            throw new RuntimeException("条款抽取任务不存在");
        }

        // 转换为实体并保留原始版本号（乐观锁检查会自动进行）
        ClauseExtractionEntity entity = toEntity(clauseExtraction);
        entity.setObjectVersionNumber(existingEntity.getObjectVersionNumber());

        // 使用updateById以支持乐观锁
        clauseExtractionMapper.updateById(entity);

        // 重新查询获取更新后的实体（包含新的版本号）
        ClauseExtractionEntity updatedEntity = clauseExtractionMapper.selectById(entity.getId());
        return toDomain(updatedEntity);
    }

    @Override
    public void delete(ExtractionId id) {
        ClauseExtractionEntity entity = new ClauseExtractionEntity();
        entity.setId(id.getValue());
        entity.setIsDeleted(true);
        entity.setUpdatedTime(LocalDateTime.now());
        clauseExtractionMapper.updateById(entity);
    }

    @Override
    public boolean existsActiveExtraction(Long contractId) {
        LambdaQueryWrapper<ClauseExtractionEntity> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(ClauseExtractionEntity::getContractId, contractId)
               .eq(ClauseExtractionEntity::getIsDeleted, false)
               .in(ClauseExtractionEntity::getStatus,
                   ExtractionStatus.PENDING.name(),
                   ExtractionStatus.PROCESSING.name());

        return clauseExtractionMapper.selectCount(wrapper) > 0;
    }

    @Override
    public void deleteByContractId(Long contractId) {
        clauseExtractionMapper.deleteByContractId(contractId);
    }

    /**
     * 领域模型转实体
     */
    private ClauseExtractionEntity toEntity(ClauseExtraction domain) {
        if (domain == null) {
            return null;
        }

        ClauseExtractionEntity entity = new ClauseExtractionEntity();
        // ID可能为null（新增时由数据库生成），也可能不为null（更新时）
        if (domain.getId() != null) {
            entity.setId(domain.getId().getValue());
        }
        entity.setContractId(domain.getContractId());
        if (domain.getStatus() != null) {
            entity.setStatus(domain.getStatus().name());
        } else {
            entity.setStatus(ExtractionStatus.PENDING.name());
        }
        entity.setErrorMessage(domain.getErrorMessage());
        entity.setStartedAt(domain.getStartedAt());
        entity.setCompletedAt(domain.getCompletedAt());
        entity.setCreatedTime(domain.getAuditInfo().getCreatedTime());
        entity.setUpdatedTime(domain.getAuditInfo().getUpdatedTime());
        entity.setCreatedBy(domain.getAuditInfo().getCreatedBy());
        entity.setUpdatedBy(domain.getAuditInfo().getUpdatedBy());
        entity.setObjectVersionNumber(domain.getAuditInfo().getObjectVersionNumber());

        // 设置抽取结果
        entity.setExtractionResult(domain.getResult());

        return entity;
    }

    /**
     * 实体转领域模型
     */
    private ClauseExtraction toDomain(ClauseExtractionEntity entity) {
        if (entity == null) {
            return null;
        }

        // 获取抽取结果
        ClauseExtractionResult result = entity.getExtractionResult();

        ExtractionStatus status;
        if (entity.getStatus() != null) {
            try {
                status = ExtractionStatus.valueOf(entity.getStatus());
            } catch (IllegalArgumentException e) {
                log.warn("无效的抽取状态，使用默认状态: {}", entity.getStatus());
                status = ExtractionStatus.PENDING;
            }
        } else {
            log.warn("抽取状态为空，使用默认状态");
            status = ExtractionStatus.PENDING;
        }

        return new ClauseExtraction(
            ExtractionId.of(entity.getId()),
            entity.getContractId(),
            status,
            entity.getErrorMessage(),
            result,
            new AuditInfo(
                entity.getCreatedBy(),
                entity.getCreatedTime(),
                entity.getUpdatedTime(),
                entity.getUpdatedBy(),
                entity.getObjectVersionNumber()
            ),
            entity.getStartedAt(),
            entity.getCompletedAt()
        );
    }
}