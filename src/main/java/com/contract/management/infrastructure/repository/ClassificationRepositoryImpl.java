package com.contract.management.infrastructure.repository;

import com.contract.management.domain.model.ClassificationResult;
import com.contract.management.domain.model.ClassificationResultId;
import com.contract.management.domain.model.ContractId;
import com.contract.management.domain.repository.ClassificationRepository;
import com.contract.management.infrastructure.converter.ClassificationResultConverter;
import com.contract.management.infrastructure.entity.ClassificationResultEntity;
import com.contract.management.infrastructure.mapper.ClassificationResultMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * 合同分类结果仓储实现
 *
 * @author SaltyFish
 * @since 1.0.0
 */
@Repository
@RequiredArgsConstructor
public class ClassificationRepositoryImpl implements ClassificationRepository {

    private final ClassificationResultMapper classificationResultMapper;

    @Override
    @Transactional
    public ClassificationResult save(ClassificationResult classificationResult) {
        ClassificationResultEntity entity = ClassificationResultConverter.toEntity(classificationResult);
        if (entity.getId() == null) {
            classificationResultMapper.insert(entity);
        } else {
            // 先查询现有实体，保留版本号
            ClassificationResultEntity existingEntity = classificationResultMapper.selectById(entity.getId());
            if (existingEntity == null) {
                throw new RuntimeException("分类结果不存在");
            }
            // 保留版本号
            entity.setObjectVersionNumber(existingEntity.getObjectVersionNumber());
            classificationResultMapper.updateById(entity);
        }
        return ClassificationResultConverter.toDomain(entity);
    }

    @Override
    public Optional<ClassificationResult> findById(ClassificationResultId id) {
        ClassificationResultEntity entity = classificationResultMapper.selectById(id.getValue());
        return Optional.ofNullable(ClassificationResultConverter.toDomain(entity));
    }

    @Override
    public Optional<ClassificationResult> findActiveByContractId(ContractId contractId) {
        ClassificationResultEntity entity = classificationResultMapper.findActiveByContractId(contractId.getValue());
        return Optional.ofNullable(ClassificationResultConverter.toDomain(entity));
    }

    @Override
    public List<ClassificationResult> findAllByContractId(ContractId contractId) {
        List<ClassificationResultEntity> entities = classificationResultMapper.findAllByContractId(contractId.getValue());
        return ClassificationResultConverter.toDomainList(entities);
    }

    @Override
    @Transactional
    public void deactivateByContractId(ContractId contractId, Long operatorId) {
        classificationResultMapper.deactivateByContractId(
                contractId.getValue(),
                operatorId,
                LocalDateTime.now()
        );
    }

    @Override
    public boolean existsById(ClassificationResultId id) {
        return classificationResultMapper.selectById(id.getValue()) != null;
    }
}