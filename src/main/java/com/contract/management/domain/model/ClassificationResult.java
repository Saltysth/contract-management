package com.contract.management.domain.model;

import com.contract.common.constant.ContractType;
import com.contract.management.domain.model.valueobject.AuditInfo;
import com.contract.management.domain.model.valueobject.ClassificationMetadata;
import lombok.Getter;

import java.math.BigDecimal;

/**
 * 合同分类结果聚合根
 *
 * @author SaltyFish
 * @since 1.0.0
 */
@Getter
public class ClassificationResult {

    private final ClassificationResultId id;
    private final ContractId contractId;
    private final ContractType contractType;
    private final ClassificationMethod classificationMethod;
    private final BigDecimal confidenceScore;
    private final String modelVersion;
    private final Long classifiedBy;
    private final String classificationReason;
    private final ClassificationMetadata metadata;
    private Boolean isActive;
    private AuditInfo auditInfo;

    /**
     * 构造函数 - 创建新分类结果
     */
    public ClassificationResult(ContractId contractId, ContractType contractType,
                               ClassificationMethod classificationMethod, BigDecimal confidenceScore,
                               String modelVersion, Long classifiedBy, String classificationReason,
                               ClassificationMetadata metadata, Long createdBy) {
        this.id = null;
        this.contractId = contractId;
        this.contractType = contractType;
        this.classificationMethod = classificationMethod;
        this.confidenceScore = validateConfidenceScore(confidenceScore);
        this.modelVersion = classificationMethod == ClassificationMethod.AI ? modelVersion : null;
        this.classifiedBy = classificationMethod == ClassificationMethod.MANUAL ? classifiedBy : createdBy;
        this.classificationReason = classificationReason;
        this.metadata = metadata != null ? metadata : new ClassificationMetadata();
        this.isActive = true;
        this.auditInfo = AuditInfo.create(createdBy);

        validateForCreation();
    }

    /**
     * 构造函数 - 从数据库重建
     */
    public ClassificationResult(ClassificationResultId id, ContractId contractId, ContractType contractType,
                               ClassificationMethod classificationMethod, BigDecimal confidenceScore,
                               String modelVersion, Long classifiedBy, String classificationReason,
                               ClassificationMetadata metadata, Boolean isActive, AuditInfo auditInfo) {
        this.id = id;
        this.contractId = contractId;
        this.contractType = contractType;
        this.classificationMethod = classificationMethod;
        this.confidenceScore = confidenceScore;
        this.modelVersion = modelVersion;
        this.classifiedBy = classifiedBy;
        this.classificationReason = classificationReason;
        this.metadata = metadata != null ? metadata : new ClassificationMetadata();
        this.isActive = isActive;
        this.auditInfo = auditInfo;
    }

    /**
     * 停用分类结果
     */
    public void deactivate() {
        this.isActive = false;
        this.auditInfo = this.auditInfo.update();
    }

    /**
     * 重新激活分类结果
     */
    public void reactivate() {
        this.isActive = true;
        this.auditInfo = this.auditInfo.update();
    }

    /**
     * 业务验证 - 创建时的验证
     */
    private void validateForCreation() {
        if (contractId == null) {
            throw new IllegalArgumentException("合同ID不能为空");
        }
        if (contractType == null) {
            throw new IllegalArgumentException("合同类型不能为空");
        }
        if (classificationMethod == null) {
            throw new IllegalArgumentException("分类方法不能为空");
        }
        if (classificationMethod == ClassificationMethod.AI && modelVersion == null) {
            throw new IllegalArgumentException("AI分类必须提供模型版本");
        }
    }

    /**
     * 验证置信度分数
     */
    private BigDecimal validateConfidenceScore(BigDecimal score) {
        if (score != null && (score.compareTo(BigDecimal.ZERO) < 0 || score.compareTo(BigDecimal.ONE) > 0)) {
            throw new IllegalArgumentException("置信度必须在0-1之间");
        }
        return score;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ClassificationResult that = (ClassificationResult) o;
        return id != null && id.equals(that.id);
    }

    @Override
    public int hashCode() {
        return id != null ? id.hashCode() : 0;
    }
}