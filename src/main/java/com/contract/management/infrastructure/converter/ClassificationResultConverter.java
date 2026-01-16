package com.contract.management.infrastructure.converter;

import com.contract.common.constant.ContractType;
import com.contract.management.domain.model.*;
import com.contract.management.domain.model.valueobject.AuditInfo;
import com.contract.management.infrastructure.entity.ClassificationResultEntity;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 合同分类结果转换器
 *
 * @author SaltyFish
 * @since 1.0.0
 */
public class ClassificationResultConverter {

    /**
     * 实体转领域对象
     *
     * @param entity 实体对象
     * @return 领域对象
     */
    public static ClassificationResult toDomain(ClassificationResultEntity entity) {
        if (entity == null) {
            return null;
        }

        ClassificationResultId id = entity.getId() != null ? ClassificationResultId.of(entity.getId()) : null;
        ContractId contractId = ContractId.of(entity.getContractId());
        ContractType contractType = ContractType.valueOf(entity.getContractType());
        ClassificationMethod method = ClassificationMethod.valueOf(entity.getClassificationMethod());

        AuditInfo auditInfo = new AuditInfo(
                entity.getCreatedBy(),
                entity.getCreatedTime(),
                entity.getUpdatedTime(),
                entity.getUpdatedBy(),
                entity.getObjectVersionNumber()
        );

        return new ClassificationResult(
                id, contractId, contractType, method,
                entity.getConfidenceScore(),
                entity.getModelVersion(),
                entity.getClassifiedBy(),
                entity.getClassificationReason(),
                entity.getMetadata(),
                entity.getIsActive(),
                auditInfo
        );
    }

    /**
     * 领域对象转实体
     *
     * @param classificationResult 领域对象
     * @return 实体对象
     */
    public static ClassificationResultEntity toEntity(ClassificationResult classificationResult) {
        if (classificationResult == null) {
            return null;
        }

        ClassificationResultEntity entity = new ClassificationResultEntity();

        if (classificationResult.getId() != null) {
            entity.setId(classificationResult.getId().getValue());
        }

        entity.setContractId(classificationResult.getContractId().getValue());
        entity.setContractType(classificationResult.getContractType().name());
        entity.setClassificationMethod(classificationResult.getClassificationMethod().name());
        entity.setConfidenceScore(classificationResult.getConfidenceScore());
        entity.setModelVersion(classificationResult.getModelVersion());
        entity.setClassifiedBy(classificationResult.getClassifiedBy());
        entity.setClassificationReason(classificationResult.getClassificationReason());
        entity.setMetadata(classificationResult.getMetadata());
        entity.setIsActive(classificationResult.getIsActive());

        if (classificationResult.getAuditInfo() != null) {
            entity.setCreatedBy(classificationResult.getAuditInfo().getCreatedBy());
            entity.setUpdatedBy(classificationResult.getAuditInfo().getUpdatedBy());
            entity.setCreatedTime(classificationResult.getAuditInfo().getCreatedTime());
            entity.setUpdatedTime(classificationResult.getAuditInfo().getUpdatedTime());
            entity.setObjectVersionNumber(classificationResult.getAuditInfo().getObjectVersionNumber());
        }

        return entity;
    }

    /**
     * 实体列表转领域对象列表
     *
     * @param entities 实体列表
     * @return 领域对象列表
     */
    public static List<ClassificationResult> toDomainList(List<ClassificationResultEntity> entities) {
        if (entities == null || entities.isEmpty()) {
            return List.of();
        }
        return entities.stream()
                .map(ClassificationResultConverter::toDomain)
                .collect(Collectors.toList());
    }
}