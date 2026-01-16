package com.contract.management.domain.repository;

import com.contract.management.domain.model.ClauseType;
import com.contract.management.domain.model.RiskLevel;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 条款查询过滤器
 *
 * @author SaltyFish
 * @since 1.0.0
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ClauseFilters {

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
     * 是否包含逻辑删除的数据
     */
    private Boolean includeDeleted;

    /**
     * 创建空过滤器
     *
     * @return 空过滤器
     */
    public static ClauseFilters empty() {
        return new ClauseFilters();
    }

    /**
     * 检查过滤器是否为空（所有条件都为null）
     *
     * @return 是否为空
     */
    public boolean isEmpty() {
        return contractId == null &&
               clauseType == null &&
               riskLevel == null &&
               (titleKeyword == null || titleKeyword.trim().isEmpty()) &&
               (contentKeyword == null || contentKeyword.trim().isEmpty()) &&
               minConfidenceScore == null &&
               maxConfidenceScore == null &&
               highRiskOnly == null &&
               hasRiskFactorsOnly == null &&
               createdTimeStart == null &&
               createdTimeEnd == null &&
               createdBy == null &&
               includeDeleted == null;
    }
}