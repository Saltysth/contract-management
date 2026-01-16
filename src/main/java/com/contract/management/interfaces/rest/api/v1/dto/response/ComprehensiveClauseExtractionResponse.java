package com.contract.management.interfaces.rest.api.v1.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

/**
 * 综合条款抽取响应DTO
 * 基于设计方案的完整数据结构
 *
 * @author SaltyFish
 * @since 1.0.0
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "综合条款抽取响应")
public class ComprehensiveClauseExtractionResponse {

    /**
     * 任务信息
     */
    @Schema(description = "任务信息")
    private TaskInfo taskInfo;

    /**
     * 文档信息
     */
    @Schema(description = "文档信息")
    private DocumentInfo documentInfo;

    /**
     * 结构信息
     */
    @Schema(description = "文档结构信息")
    private StructureInfo structureInfo;

    /**
     * 条款列表
     */
    @Schema(description = "抽取的条款列表")
    private List<ExtractedClause> clauses;

    /**
     * 质量指标
     */
    @Schema(description = "抽取质量指标")
    private QualityMetrics qualityMetrics;

    /**
     * 任务信息
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @Schema(description = "任务信息")
    public static class TaskInfo {
        @Schema(description = "抽取任务ID")
        private String extractionTaskId;

        @Schema(description = "处理时间（秒）")
        private Integer processingTime;

        @Schema(description = "文档质量等级")
        private String documentQuality;
    }

    /**
     * 文档信息
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @Schema(description = "文档基本信息")
    public static class DocumentInfo {
        @Schema(description = "总页数")
        private Integer totalPages;

        @Schema(description = "文档类型")
        private String documentType;

        @Schema(description = "合同双方信息")
        private ContractParties parties;

        @Schema(description = "合同日期")
        private String contractDate;

        @Schema(description = "合同金额")
        private String contractAmount;
    }

    /**
     * 合同双方信息
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @Schema(description = "合同双方信息")
    public static class ContractParties {
        @Schema(description = "甲方")
        private String partyA;

        @Schema(description = "乙方")
        private String partyB;
    }

    /**
     * 结构信息
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @Schema(description = "文档结构信息")
    public static class StructureInfo {
        @Schema(description = "章节列表")
        private List<Chapter> chapters;

        @Schema(description = "特殊区域")
        private List<SpecialArea> specialAreas;
    }

    /**
     * 章节信息
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @Schema(description = "章节信息")
    public static class Chapter {
        @Schema(description = "章节标题")
        private String title;

        @Schema(description = "起始页")
        private Integer pageStart;

        @Schema(description = "结束页")
        private Integer pageEnd;

        @Schema(description = "层级")
        private Integer level;

        @Schema(description = "子章节")
        private List<Subsection> subsections;
    }

    /**
     * 子章节信息
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @Schema(description = "子章节信息")
    public static class Subsection {
        @Schema(description = "子章节标题")
        private String title;

        @Schema(description = "页码")
        private Integer page;
    }

    /**
     * 特殊区域
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @Schema(description = "特殊区域信息")
    public static class SpecialArea {
        @Schema(description = "区域类型")
        private String type;

        @Schema(description = "所在页码")
        private List<Integer> pages;

        @Schema(description = "描述")
        private String description;
    }

    /**
     * 抽取的条款
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @Schema(description = "抽取的条款")
    public static class ExtractedClause {
        @Schema(description = "条款ID")
        private String clauseId;

        @Schema(description = "条款类型")
        private String clauseType;

        @Schema(description = "条款标题")
        private String clauseTitle;

        @Schema(description = "条款内容")
        private String content;

        @Schema(description = "置信度分数")
        private Double confidenceScore;

        @Schema(description = "风险等级")
        private String riskLevel;

        @Schema(description = "提取的实体")
        private ExtractedEntities extractedEntities;

        @Schema(description = "风险因子")
        private List<RiskFactor> riskFactors;

        @Schema(description = "位置信息")
        private List<ClausePosition> positions;

        @Schema(description = "关联章节")
        private List<String> relatedChapters;

        @Schema(description = "抽取时间")
        private LocalDateTime extractedAt;
    }

    /**
     * 提取的实体
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @Schema(description = "提取的实体信息")
    public static class ExtractedEntities {
        @Schema(description = "金额百分比")
        private String amountPercentage;

        @Schema(description = "付款期限")
        private String paymentPeriod;

        @Schema(description = "付款条件")
        private String paymentCondition;

        @Schema(description = "付款金额")
        private String paymentAmount;
    }

    /**
     * 风险因子
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @Schema(description = "风险因子")
    public static class RiskFactor {
        @Schema(description = "风险因子描述")
        private String factor;

        @Schema(description = "严重程度")
        private String severity;
    }

    /**
     * 条款位置信息
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @Schema(description = "条款位置信息")
    public static class ClausePosition {
        @Schema(description = "页码")
        private Integer page;

        @Schema(description = "边界框坐标")
        private List<Integer> bbox;

        @Schema(description = "上下文描述")
        private String context;

        @Schema(description = "文本片段")
        private String textSnippet;
    }

    /**
     * 质量指标
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @Schema(description = "抽取质量指标")
    public static class QualityMetrics {
        @Schema(description = "找到的条款总数")
        private Integer totalClausesFound;

        @Schema(description = "高置信度条款数")
        private Integer highConfidenceClauses;

        @Schema(description = "中置信度条款数")
        private Integer mediumConfidenceClauses;

        @Schema(description = "低置信度条款数")
        private Integer lowConfidenceClauses;

        @Schema(description = "有问题的页面")
        private List<Integer> pagesWithIssues;

        @Schema(description = "OCR平均置信度")
        private Double ocrConfidenceAvg;

        @Schema(description = "结构识别准确度")
        private Double structureAccuracy;

        @Schema(description = "建议人工审核的条款")
        private List<String> recommendedReviewClauses;
    }
}