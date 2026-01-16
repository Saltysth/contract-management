package com.contract.management.application.dto;

import com.contract.management.domain.model.ClauseType;
import com.contract.management.domain.model.RiskLevel;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 条款应用层DTO
 * 用于应用服务层的数据传输
 *
 * @author SaltyFish
 * @since 1.0.0
 */
@Data
public class ClauseDTO {

    /**
     * 条款ID
     */
    private Long id;

    /**
     * 抽取任务ID
     */
    private Long extractionTaskId;

    /**
     * 合同ID
     */
    private Long contractId;

    /**
     * 条款类型
     */
    private ClauseType clauseType;

    /**
     * 条款标题
     */
    private String clauseTitle;

    /**
     * 条款内容
     */
    private String clauseContent;

    /**
     * 条款位置（JSON格式）
     */
    private String clausePosition;

    /**
     * 置信度分数
     */
    private BigDecimal confidenceScore;

    /**
     * 提取的实体（JSON格式）
     */
    private String extractedEntities;

    /**
     * 风险等级
     */
    private RiskLevel riskLevel;

    /**
     * 风险因子（JSON格式）
     */
    private String riskFactors;

    /**
     * 创建时间
     */
    private LocalDateTime createdTime;

    /**
     * 是否有风险
     */
    private Boolean hasRisk;

    /**
     * 是否为高风险
     */
    private Boolean isHighRisk;

    /**
     * 是否有置信度分数
     */
    private Boolean hasConfidenceScore;

    /**
     * 是否有提取的实体
     */
    private Boolean hasExtractedEntities;

    /**
     * 是否有风险因子
     */
    private Boolean hasRiskFactors;
}