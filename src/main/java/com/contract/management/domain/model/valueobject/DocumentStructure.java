package com.contract.management.domain.model.valueobject;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

/**
 * 文档结构值对象
 * 表示合同文档的结构信息和元数据
 *
 * @author SaltyFish
 * @since 1.0.0
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DocumentStructure {

    /**
     * 文档基本信息
     */
    private DocumentInfo documentInfo;

    /**
     * 章节结构信息
     */
    private StructureInfo structureInfo;

    /**
     * 特殊区域标记
     */
    private List<SpecialArea> specialAreas;

    /**
     * 质量指标
     */
    private QualityMetrics qualityMetrics;

    /**
     * 文档基本信息
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class DocumentInfo {
        private Integer totalPages; // 总页数
        private String documentType; // 文档类型
        private ContractParties parties; // 合同双方信息
        private LocalDate contractDate; // 合同签订日期
        private LocalDate effectiveDate; // 合同生效日期
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
        private Map<String, Object> additionalInfo; // 额外信息
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
        private Map<String, Object> additionalInfo; // 额外结构信息
    }

    /**
     * 章节信息
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Chapter {
        private String title; // 章节标题
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
        private String title; // 子章节标题
        private Integer page; // 页码
        private Map<String, Object> additionalInfo; // 额外信息
    }

    /**
     * 特殊区域标记
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class SpecialArea {
        private String type; // 类型：table, signature, attachment等
        private List<Integer> pages; // 所在页码
        private String description; // 描述
        private Map<String, Object> bbox; // 边界框坐标
    }

    /**
     * 质量指标
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class QualityMetrics {
        private Double ocrConfidenceAvg; // OCR平均置信度
        private Double structureAccuracy; // 结构识别准确度
        private List<Integer> pagesWithIssues; // 有问题的页面
        private Map<String, Object> additionalMetrics; // 其他质量指标
    }
}