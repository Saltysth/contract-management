package com.contract.management.domain.model.valueobject;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

/**
 * 综合条款抽取结果值对象
 * 基于设计方案的完整数据结构
 *
 * @author SaltyFish
 * @since 1.0.0
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ComprehensiveClauseExtractionResult {

    /**
     * 任务信息
     */
    private TaskInfo taskInfo;

    /**
     * 文档信息
     */
    private DocumentInfo documentInfo;

    /**
     * 结构信息
     */
    private StructureInfo structureInfo;

    /**
     * 条款列表
     */
    private List<ExtractedClause> clauses;

    /**
     * 质量指标
     */
    private QualityMetrics qualityMetrics;

    /**
     * 任务信息
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class TaskInfo {
        private String extractionTaskId; // 抽取任务ID
        private Integer processingTime; // 处理时间（秒）
        private String documentQuality; // 文档质量：high/medium/low
    }

    /**
     * 文档信息
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class DocumentInfo {
        private Integer totalPages; // 总页数
        private String documentType; // 文档类型
        private ContractParties parties; // 合同双方信息
        private String contractDate; // 合同日期
        private String contractAmount; // 合同金额
    }

    /**
     * 合同双方信息
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ContractParties {
        private String partyA; // 甲方
        private String partyB; // 乙方
    }

    /**
     * 结构信息
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class StructureInfo {
        private List<Chapter> chapters; // 章节列表
        private List<SpecialArea> specialAreas; // 特殊区域
    }

    /**
     * 章节信息
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Chapter {
        private String title; // 标题
        private Integer pageStart; // 起始页
        private Integer pageEnd; // 结束页
        private Integer level; // 层级
        private List<Subsection> subsections; // 子章节
    }

    /**
     * 子章节信息
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Subsection {
        private String title; // 标题
        private Integer page; // 页码
    }

    /**
     * 特殊区域
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class SpecialArea {
        private String type; // 类型：table, signature, attachment
        private List<Integer> pages; // 所在页码
        private String description; // 描述
    }

    /**
     * 抽取的条款
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ExtractedClause {
        private String clauseId; // 条款ID
        private String clauseType; // 条款类型
        private String clauseTitle; // 条款标题
        private String content; // 条款内容
        private Double confidenceScore; // 置信度分数
        private String riskLevel; // 风险等级：high/medium/low
        private ExtractedEntities extractedEntities; // 提取的实体
        private List<RiskFactor> riskFactors; // 风险因子
        private List<ClausePosition> positions; // 位置信息
        private List<String> relatedChapters; // 关联章节
        private LocalDateTime extractedAt; // 抽取时间
    }

    /**
     * 提取的实体
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ExtractedEntities {
        private String amountPercentage; // 金额百分比
        private String paymentPeriod; // 付款期限
        private String paymentCondition; // 付款条件
        private String paymentAmount; // 付款金额
        private Map<String, Object> additionalEntities; // 其他实体
    }

    /**
     * 风险因子
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class RiskFactor {
        private String factor; // 风险因子描述
        private String severity; // 严重程度：high/medium/low
    }

    /**
     * 条款位置信息
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ClausePosition {
        private Integer page; // 页码
        private List<Integer> bbox; // 边界框坐标 [x1, y1, x2, y2]
        private String context; // 上下文描述
        private String textSnippet; // 文本片段
    }

    /**
     * 质量指标
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class QualityMetrics {
        private Integer totalClausesFound; // 找到的条款总数
        private Integer highConfidenceClauses; // 高置信度条款数
        private Integer mediumConfidenceClauses; // 中置信度条款数
        private Integer lowConfidenceClauses; // 低置信度条款数
        private List<Integer> pagesWithIssues; // 有问题的页面
        private Double ocrConfidenceAvg; // OCR平均置信度
        private Double structureAccuracy; // 结构识别准确度
        private List<String> recommendedReviewClauses; // 建议人工审核的条款
    }
}