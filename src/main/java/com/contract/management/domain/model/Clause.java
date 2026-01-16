package com.contract.management.domain.model;

import com.contract.management.domain.model.valueobject.AuditInfo;
import com.contract.management.domain.model.valueobject.ClauseContent;
import com.contract.management.domain.model.valueobject.ClauseTitle;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 条款聚合根
 *
 * @author SaltyFish
 * @since 1.0.0
 */
@Getter
@Setter
public class Clause {

    private final ClauseId id;
    private final Long extractionTaskId;
    private final Long contractId;
    private final ClauseType clauseType;
    private ClauseTitle clauseTitle;
    private ClauseContent clauseContent;
    private String clausePosition;
    private BigDecimal confidenceScore;
    private String extractedEntities;
    private RiskLevel riskLevel;
    private String riskFactors;
    private final LocalDateTime createdTime;

    /**
     * 构造函数 - 创建新条款
     */
    public Clause(ClauseId id, Long contractId, ClauseType clauseType,
                  ClauseTitle clauseTitle, ClauseContent clauseContent, Long createdBy) {
        if (contractId == null || contractId <= 0) {
            throw new IllegalArgumentException("合同ID必须为正数");
        }
        if (clauseType == null) {
            throw new IllegalArgumentException("条款类型不能为空");
        }

        this.id = id;
        this.extractionTaskId = null;
        this.contractId = contractId;
        this.clauseType = clauseType;
        this.clauseTitle = clauseTitle != null ? clauseTitle : ClauseTitle.of(null);
        this.clauseContent = clauseContent;
        this.clausePosition = null;
        this.confidenceScore = null;
        this.extractedEntities = null;
        this.riskLevel = RiskLevel.UNKNOWN;
        this.riskFactors = null;
        this.createdTime = LocalDateTime.now();

        // 创建时验证
        validateForCreation();
    }

    /**
     * 构造函数 - 创建新条款（带抽取任务ID）
     */
    public Clause(ClauseId id, Long extractionTaskId, Long contractId, ClauseType clauseType,
                  ClauseTitle clauseTitle, ClauseContent clauseContent, Long createdBy) {
        if (contractId == null || contractId <= 0) {
            throw new IllegalArgumentException("合同ID必须为正数");
        }
        if (clauseType == null) {
            throw new IllegalArgumentException("条款类型不能为空");
        }

        this.id = id;
        this.extractionTaskId = extractionTaskId;
        this.contractId = contractId;
        this.clauseType = clauseType;
        this.clauseTitle = clauseTitle != null ? clauseTitle : ClauseTitle.of(null);
        this.clauseContent = clauseContent;
        this.clausePosition = null;
        this.confidenceScore = null;
        this.extractedEntities = null;
        this.riskLevel = RiskLevel.UNKNOWN;
        this.riskFactors = null;
        this.createdTime = LocalDateTime.now();

        // 创建时验证
        validateForCreation();
    }

    /**
     * 构造函数 - 从数据库重建
     */
    public Clause(ClauseId id, Long extractionTaskId, Long contractId, ClauseType clauseType,
                  ClauseTitle clauseTitle, ClauseContent clauseContent, String clausePosition,
                  BigDecimal confidenceScore, String extractedEntities, RiskLevel riskLevel,
                  String riskFactors, LocalDateTime createdTime) {
        this.id = id;
        this.extractionTaskId = extractionTaskId;
        this.contractId = contractId;
        this.clauseType = clauseType;
        this.clauseTitle = clauseTitle != null ? clauseTitle : ClauseTitle.of(null);
        this.clauseContent = clauseContent;
        this.clausePosition = clausePosition;
        this.confidenceScore = confidenceScore;
        this.extractedEntities = extractedEntities;
        this.riskLevel = riskLevel != null ? riskLevel : RiskLevel.UNKNOWN;
        this.riskFactors = riskFactors;
        this.createdTime = createdTime;
    }

    // ==================== 领域行为方法 ====================

    /**
     * 更新条款标题
     */
    public void updateTitle(ClauseTitle clauseTitle) {
        if (clauseTitle != null) {
            this.clauseTitle = clauseTitle;
        }
    }

    /**
     * 更新条款内容
     */
    public void updateContent(ClauseContent clauseContent) {
        if (clauseContent != null) {
            this.clauseContent = clauseContent;
        }
    }

    /**
     * 更新条款位置信息
     */
    public void updatePosition(String clausePosition) {
        this.clausePosition = clausePosition;
    }

    /**
     * 更新置信度分数
     */
    public void updateConfidenceScore(BigDecimal confidenceScore) {
        if (confidenceScore != null && (confidenceScore.compareTo(BigDecimal.ZERO) < 0 ||
                                       confidenceScore.compareTo(new BigDecimal("100.00")) > 0)) {
            throw new IllegalArgumentException("置信度分数必须在0-100之间");
        }
        this.confidenceScore = confidenceScore;
    }

    /**
     * 更新提取的实体信息
     */
    public void updateExtractedEntities(String extractedEntities) {
        this.extractedEntities = extractedEntities;
    }

    /**
     * 更新风险等级
     */
    public void updateRiskLevel(RiskLevel riskLevel) {
        this.riskLevel = riskLevel != null ? riskLevel : RiskLevel.UNKNOWN;
    }

    /**
     * 更新风险因子
     */
    public void updateRiskFactors(String riskFactors) {
        this.riskFactors = riskFactors;
    }

    /**
     * 批量更新分析结果
     */
    public void updateAnalysisResult(BigDecimal confidenceScore, String extractedEntities,
                                    RiskLevel riskLevel, String riskFactors) {
        updateConfidenceScore(confidenceScore);
        updateExtractedEntities(extractedEntities);
        updateRiskLevel(riskLevel);
        updateRiskFactors(riskFactors);
    }

    // ==================== 业务规则验证方法 ====================

    /**
     * 验证创建时的业务规则
     */
    public void validateForCreation() {
        validateBasicInfo();
    }

    /**
     * 验证基本信息
     */
    private void validateBasicInfo() {
        if (clauseContent == null) {
            throw new IllegalArgumentException("条款内容不能为空");
        }
    }

    // ==================== 查询方法 ====================

    /**
     * 检查是否有风险
     */
    public boolean hasRisk() {
        return riskLevel != null && riskLevel != RiskLevel.UNKNOWN && riskLevel != RiskLevel.LOW;
    }

    /**
     * 检查是否为高风险
     */
    public boolean isHighRisk() {
        return riskLevel == RiskLevel.HIGH || riskLevel == RiskLevel.CRITICAL;
    }

    /**
     * 检查是否有置信度分数
     */
    public boolean hasConfidenceScore() {
        return confidenceScore != null;
    }

    /**
     * 检查是否有提取的实体
     */
    public boolean hasExtractedEntities() {
        return extractedEntities != null && !extractedEntities.trim().isEmpty();
    }

    /**
     * 检查是否有风险因子
     */
    public boolean hasRiskFactors() {
        return riskFactors != null && !riskFactors.trim().isEmpty();
    }

    // ==================== 辅助方法 ====================

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Clause clause = (Clause) o;
        return id != null && id.equals(clause.id);
    }

    @Override
    public int hashCode() {
        return id != null ? id.hashCode() : 0;
    }

    @Override
    public String toString() {
        return "Clause{" +
                "id=" + id +
                ", extractionTaskId=" + extractionTaskId +
                ", contractId=" + contractId +
                ", clauseType=" + clauseType +
                ", clauseTitle=" + clauseTitle +
                ", riskLevel=" + riskLevel +
                '}';
    }
}