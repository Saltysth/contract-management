package com.contract.management.interfaces.rest.api.v1.convertor;

import com.contract.management.domain.model.valueobject.ComprehensiveClauseExtractionResult;
import com.contract.management.interfaces.rest.api.v1.dto.response.ComprehensiveClauseExtractionResponse;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.stream.Collectors;

/**
 * 综合条款抽取转换器
 * 负责领域模型与DTO之间的转换
 *
 * @author SaltyFish
 * @since 1.0.0
 */
@Component
public class ComprehensiveClauseExtractionConvertor {

    /**
     * 将领域模型转换为响应DTO
     */
    public ComprehensiveClauseExtractionResponse toResponse(ComprehensiveClauseExtractionResult result) {
        if (result == null) {
            return null;
        }

        return ComprehensiveClauseExtractionResponse.builder()
            .taskInfo(convertTaskInfo(result.getTaskInfo()))
            .documentInfo(convertDocumentInfo(result.getDocumentInfo()))
            .structureInfo(convertStructureInfo(result.getStructureInfo()))
            .clauses(convertClauses(result.getClauses()))
            .qualityMetrics(convertQualityMetrics(result.getQualityMetrics()))
            .build();
    }

    /**
     * 转换任务信息
     */
    private ComprehensiveClauseExtractionResponse.TaskInfo convertTaskInfo(
            ComprehensiveClauseExtractionResult.TaskInfo taskInfo) {
        if (taskInfo == null) {
            return null;
        }

        return ComprehensiveClauseExtractionResponse.TaskInfo.builder()
            .extractionTaskId(taskInfo.getExtractionTaskId())
            .processingTime(taskInfo.getProcessingTime())
            .documentQuality(taskInfo.getDocumentQuality())
            .build();
    }

    /**
     * 转换文档信息
     */
    private ComprehensiveClauseExtractionResponse.DocumentInfo convertDocumentInfo(
            ComprehensiveClauseExtractionResult.DocumentInfo documentInfo) {
        if (documentInfo == null) {
            return null;
        }

        return ComprehensiveClauseExtractionResponse.DocumentInfo.builder()
            .totalPages(documentInfo.getTotalPages())
            .documentType(documentInfo.getDocumentType())
            .parties(convertContractParties(documentInfo.getParties()))
            .contractDate(documentInfo.getContractDate())
            .contractAmount(documentInfo.getContractAmount())
            .build();
    }

    /**
     * 转换合同双方信息
     */
    private ComprehensiveClauseExtractionResponse.ContractParties convertContractParties(
            ComprehensiveClauseExtractionResult.ContractParties parties) {
        if (parties == null) {
            return null;
        }

        return ComprehensiveClauseExtractionResponse.ContractParties.builder()
            .partyA(parties.getPartyA())
            .partyB(parties.getPartyB())
            .build();
    }

    /**
     * 转换结构信息
     */
    private ComprehensiveClauseExtractionResponse.StructureInfo convertStructureInfo(
            ComprehensiveClauseExtractionResult.StructureInfo structureInfo) {
        if (structureInfo == null) {
            return null;
        }

        return ComprehensiveClauseExtractionResponse.StructureInfo.builder()
            .chapters(convertChapters(structureInfo.getChapters()))
            .specialAreas(convertSpecialAreas(structureInfo.getSpecialAreas()))
            .build();
    }

    /**
     * 转换章节列表
     */
    private java.util.List<ComprehensiveClauseExtractionResponse.Chapter> convertChapters(
            java.util.List<ComprehensiveClauseExtractionResult.Chapter> chapters) {
        if (chapters == null) {
            return new ArrayList<>();
        }

        return chapters.stream()
            .map(this::convertChapter)
            .collect(Collectors.toList());
    }

    /**
     * 转换单个章节
     */
    private ComprehensiveClauseExtractionResponse.Chapter convertChapter(
            ComprehensiveClauseExtractionResult.Chapter chapter) {
        if (chapter == null) {
            return null;
        }

        return ComprehensiveClauseExtractionResponse.Chapter.builder()
            .title(chapter.getTitle())
            .pageStart(chapter.getPageStart())
            .pageEnd(chapter.getPageEnd())
            .level(chapter.getLevel())
            .subsections(convertSubsections(chapter.getSubsections()))
            .build();
    }

    /**
     * 转换子章节列表
     */
    private java.util.List<ComprehensiveClauseExtractionResponse.Subsection> convertSubsections(
            java.util.List<ComprehensiveClauseExtractionResult.Subsection> subsections) {
        if (subsections == null) {
            return new ArrayList<>();
        }

        return subsections.stream()
            .map(this::convertSubsection)
            .collect(Collectors.toList());
    }

    /**
     * 转换单个子章节
     */
    private ComprehensiveClauseExtractionResponse.Subsection convertSubsection(
            ComprehensiveClauseExtractionResult.Subsection subsection) {
        if (subsection == null) {
            return null;
        }

        return ComprehensiveClauseExtractionResponse.Subsection.builder()
            .title(subsection.getTitle())
            .page(subsection.getPage())
            .build();
    }

    /**
     * 转换特殊区域列表
     */
    private java.util.List<ComprehensiveClauseExtractionResponse.SpecialArea> convertSpecialAreas(
            java.util.List<ComprehensiveClauseExtractionResult.SpecialArea> specialAreas) {
        if (specialAreas == null) {
            return new ArrayList<>();
        }

        return specialAreas.stream()
            .map(this::convertSpecialArea)
            .collect(Collectors.toList());
    }

    /**
     * 转换单个特殊区域
     */
    private ComprehensiveClauseExtractionResponse.SpecialArea convertSpecialArea(
            ComprehensiveClauseExtractionResult.SpecialArea specialArea) {
        if (specialArea == null) {
            return null;
        }

        return ComprehensiveClauseExtractionResponse.SpecialArea.builder()
            .type(specialArea.getType())
            .pages(specialArea.getPages())
            .description(specialArea.getDescription())
            .build();
    }

    /**
     * 转换条款列表
     */
    private java.util.List<ComprehensiveClauseExtractionResponse.ExtractedClause> convertClauses(
            java.util.List<ComprehensiveClauseExtractionResult.ExtractedClause> clauses) {
        if (clauses == null) {
            return new ArrayList<>();
        }

        return clauses.stream()
            .map(this::convertClause)
            .collect(Collectors.toList());
    }

    /**
     * 转换单个条款
     */
    private ComprehensiveClauseExtractionResponse.ExtractedClause convertClause(
            ComprehensiveClauseExtractionResult.ExtractedClause clause) {
        if (clause == null) {
            return null;
        }

        return ComprehensiveClauseExtractionResponse.ExtractedClause.builder()
            .clauseId(clause.getClauseId())
            .clauseType(clause.getClauseType())
            .clauseTitle(clause.getClauseTitle())
            .content(clause.getContent())
            .confidenceScore(clause.getConfidenceScore())
            .riskLevel(clause.getRiskLevel())
            .extractedEntities(convertExtractedEntities(clause.getExtractedEntities()))
            .riskFactors(convertRiskFactors(clause.getRiskFactors()))
            .positions(convertPositions(clause.getPositions()))
            .relatedChapters(clause.getRelatedChapters())
            .extractedAt(clause.getExtractedAt())
            .build();
    }

    /**
     * 转换提取的实体
     */
    private ComprehensiveClauseExtractionResponse.ExtractedEntities convertExtractedEntities(
            ComprehensiveClauseExtractionResult.ExtractedEntities entities) {
        if (entities == null) {
            return null;
        }

        return ComprehensiveClauseExtractionResponse.ExtractedEntities.builder()
            .amountPercentage(entities.getAmountPercentage())
            .paymentPeriod(entities.getPaymentPeriod())
            .paymentCondition(entities.getPaymentCondition())
            .paymentAmount(entities.getPaymentAmount())
            .build();
    }

    /**
     * 转换风险因子列表
     */
    private java.util.List<ComprehensiveClauseExtractionResponse.RiskFactor> convertRiskFactors(
            java.util.List<ComprehensiveClauseExtractionResult.RiskFactor> riskFactors) {
        if (riskFactors == null) {
            return new ArrayList<>();
        }

        return riskFactors.stream()
            .map(this::convertRiskFactor)
            .collect(Collectors.toList());
    }

    /**
     * 转换单个风险因子
     */
    private ComprehensiveClauseExtractionResponse.RiskFactor convertRiskFactor(
            ComprehensiveClauseExtractionResult.RiskFactor riskFactor) {
        if (riskFactor == null) {
            return null;
        }

        return ComprehensiveClauseExtractionResponse.RiskFactor.builder()
            .factor(riskFactor.getFactor())
            .severity(riskFactor.getSeverity())
            .build();
    }

    /**
     * 转换位置信息列表
     */
    private java.util.List<ComprehensiveClauseExtractionResponse.ClausePosition> convertPositions(
            java.util.List<ComprehensiveClauseExtractionResult.ClausePosition> positions) {
        if (positions == null) {
            return new ArrayList<>();
        }

        return positions.stream()
            .map(this::convertPosition)
            .collect(Collectors.toList());
    }

    /**
     * 转换单个位置信息
     */
    private ComprehensiveClauseExtractionResponse.ClausePosition convertPosition(
            ComprehensiveClauseExtractionResult.ClausePosition position) {
        if (position == null) {
            return null;
        }

        return ComprehensiveClauseExtractionResponse.ClausePosition.builder()
            .page(position.getPage())
            .bbox(position.getBbox())
            .context(position.getContext())
            .textSnippet(position.getTextSnippet())
            .build();
    }

    /**
     * 转换质量指标
     */
    private ComprehensiveClauseExtractionResponse.QualityMetrics convertQualityMetrics(
            ComprehensiveClauseExtractionResult.QualityMetrics qualityMetrics) {
        if (qualityMetrics == null) {
            return null;
        }

        return ComprehensiveClauseExtractionResponse.QualityMetrics.builder()
            .totalClausesFound(qualityMetrics.getTotalClausesFound())
            .highConfidenceClauses(qualityMetrics.getHighConfidenceClauses())
            .mediumConfidenceClauses(qualityMetrics.getMediumConfidenceClauses())
            .lowConfidenceClauses(qualityMetrics.getLowConfidenceClauses())
            .pagesWithIssues(qualityMetrics.getPagesWithIssues())
            .ocrConfidenceAvg(qualityMetrics.getOcrConfidenceAvg())
            .structureAccuracy(qualityMetrics.getStructureAccuracy())
            .recommendedReviewClauses(qualityMetrics.getRecommendedReviewClauses())
            .build();
    }
}