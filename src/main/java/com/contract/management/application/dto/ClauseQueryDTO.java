package com.contract.management.application.dto;

import com.contract.management.domain.model.ClauseType;
import com.contract.management.domain.model.RiskLevel;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 条款查询DTO
 *
 * @author SaltyFish
 * @since 1.0.0
 */
@Data
public class ClauseQueryDTO {

    /**
     * 任务ID
     */
    private Long extractTaskId;

    /**
     * 合同ID
     */
    private Long contractId;

    /**
     * 条款类型
     */
    private ClauseType clauseType;

    /**
     * 风险等级
     */
    private RiskLevel riskLevel;

    /**
     * 条款标题关键词
     */
    private String titleKeyword;

    /**
     * 条款内容关键词
     */
    private String contentKeyword;

    /**
     * 最小置信度分数
     */
    private BigDecimal minConfidenceScore;

    /**
     * 最大置信度分数
     */
    private BigDecimal maxConfidenceScore;

    /**
     * 是否仅高风险条款
     */
    private Boolean highRiskOnly;

    /**
     * 是否仅包含风险因子的条款
     */
    private Boolean hasRiskFactorsOnly;

    /**
     * 创建时间开始
     */
    private LocalDateTime createdTimeStart;

    /**
     * 创建时间结束
     */
    private LocalDateTime createdTimeEnd;

    /**
     * 创建人ID
     */
    private Long createdBy;

    /**
     * 页码（从0开始）
     */
    private Integer page = 0;

    /**
     * 页大小
     */
    private Integer size = 20;

    /**
     * 排序字段
     */
    private String sort = "createdTime";

    /**
     * 排序方向（ASC/DESC）
     */
    private String direction = "DESC";
}