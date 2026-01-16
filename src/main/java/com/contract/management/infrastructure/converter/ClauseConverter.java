package com.contract.management.infrastructure.converter;

import com.contract.management.domain.model.Clause;
import com.contract.management.domain.model.ClauseId;
import com.contract.management.domain.model.ClauseType;
import com.contract.management.domain.model.RiskLevel;
import com.contract.management.domain.model.valueobject.ClauseContent;
import com.contract.management.domain.model.valueobject.ClauseTitle;
import com.contract.management.domain.model.valueobject.ClausePosition;
import com.contract.management.domain.model.valueobject.RiskFactors;
import com.contract.management.domain.model.valueobject.ExtractedEntities;
import com.contract.management.infrastructure.entity.ClauseEntity;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 条款领域模型与实体转换器
 *
 * @author SaltyFish
 * @since 1.0.0
 */
public class ClauseConverter {

    /**
     * 实体转领域模型
     *
     * @param entity 实体
     * @return 领域模型
     */
    public static Clause toDomain(ClauseEntity entity) {
        if (entity == null) {
            return null;
        }

        // 构建值对象
        ClauseId clauseId = new ClauseId(entity.getId());
        ClauseType clauseType = ClauseType.fromString(entity.getClauseType());
        ClauseTitle clauseTitle = entity.getClauseTitle() != null ?
            ClauseTitle.of(entity.getClauseTitle()) : null;
        ClauseContent clauseContent = ClauseContent.of(entity.getClauseContent());
        RiskLevel riskLevel = RiskLevel.fromString(entity.getRiskLevel());

        // 使用重建构造函数
        return new Clause(
            clauseId,
            entity.getExtractionTaskId(),
            entity.getContractId(),
            clauseType,
            clauseTitle,
            clauseContent,
            entity.getClausePosition() != null ? entity.getClausePosition().toString() : null,
            entity.getConfidenceScore(),
            convertExtractedEntitiesToString(entity.getExtractedEntities()),
            riskLevel,
            entity.getRiskFactors() != null ? entity.getRiskFactors().toString() : null,
            entity.getCreatedTime()
        );
    }

    /**
     * 领域模型转实体
     *
     * @param domain 领域模型
     * @return 实体
     */
    public static ClauseEntity toEntity(Clause domain) {
        if (domain == null) {
            return null;
        }

        ClauseEntity entity = new ClauseEntity();
        entity.setId(domain.getId() != null ? domain.getId().getValue() : null);
        entity.setExtractionTaskId(domain.getExtractionTaskId());
        entity.setContractId(domain.getContractId());
        entity.setClauseType(domain.getClauseType().name());
        entity.setClauseTitle(domain.getClauseTitle() != null ?
            domain.getClauseTitle().getValue() : null);
        entity.setClauseContent(domain.getClauseContent().getValue());
        entity.setClausePosition(parseClausePosition(domain.getClausePosition()));
        entity.setConfidenceScore(domain.getConfidenceScore());
        // 设置提取实体 - 从String转换为ExtractedEntities
        if (domain.getExtractedEntities() != null && !domain.getExtractedEntities().trim().isEmpty()) {
            entity.setExtractedEntities(parseStringToExtractedEntities(domain.getExtractedEntities()));
        } else {
            entity.setExtractedEntities(ExtractedEntities.empty());
        }
        entity.setRiskLevel(domain.getRiskLevel() != null ?
            domain.getRiskLevel().name() : null);
        entity.setRiskFactors(parseRiskFactors(domain.getRiskFactors()));
        entity.setCreatedTime(domain.getCreatedTime());
        entity.setIsDeleted(false);
        // 移除硬编码版本号，让Repository层处理

        return entity;
    }

    /**
     * 实体列表转领域模型列表
     *
     * @param entities 实体列表
     * @return 领域模型列表
     */
    public static List<Clause> toDomainList(List<ClauseEntity> entities) {
        if (entities == null) {
            return List.of();
        }
        return entities.stream()
            .map(ClauseConverter::toDomain)
            .collect(Collectors.toList());
    }

    /**
     * 领域模型列表转实体列表
     *
     * @param domains 领域模型列表
     * @return 实体列表
     */
    public static List<ClauseEntity> toEntityList(List<Clause> domains) {
        if (domains == null) {
            return List.of();
        }
        return domains.stream()
            .map(ClauseConverter::toEntity)
            .collect(Collectors.toList());
    }

    /**
     * 解析条款位置字符串为ClausePosition对象
     */
    private static ClausePosition parseClausePosition(String positionStr) {
        if (positionStr == null || positionStr.trim().isEmpty()) {
            return null;
        }
        try {
            // 尝试从JSON字符串解析
            com.fasterxml.jackson.databind.ObjectMapper mapper = new com.fasterxml.jackson.databind.ObjectMapper();
            return mapper.readValue(positionStr, ClausePosition.class);
        } catch (Exception e) {
            // 如果解析失败，返回默认位置
            return ClausePosition.of(1, 0.0, 0.0, 100.0, 50.0);
        }
    }

    /**
     * 转换ExtractedEntities为字符串
     */
    private static String convertExtractedEntitiesToString(ExtractedEntities extractedEntities) {
        if (extractedEntities == null || extractedEntities.isEmpty()) {
            return null;
        }
        // 将ExtractedEntities转换为JSON字符串
        try {
            com.fasterxml.jackson.databind.ObjectMapper mapper = new com.fasterxml.jackson.databind.ObjectMapper();
            return mapper.writeValueAsString(extractedEntities.toMap());
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * 解析提取实体字符串为ExtractedEntities
     */
    private static ExtractedEntities parseStringToExtractedEntities(String entitiesStr) {
        if (entitiesStr == null || entitiesStr.trim().isEmpty()) {
            return ExtractedEntities.empty();
        }
        try {
            com.fasterxml.jackson.databind.ObjectMapper mapper = new com.fasterxml.jackson.databind.ObjectMapper();
            Map<String, String> map = mapper.readValue(entitiesStr, new com.fasterxml.jackson.core.type.TypeReference<Map<String, String>>() {});
            return ExtractedEntities.of(map);
        } catch (Exception e) {
            // 如果解析失败，返回空ExtractedEntities
            return ExtractedEntities.empty();
        }
    }

    /**
     * 解析提取实体字符串为Map（向后兼容）
     */
    private static Map<String, String> parseExtractedEntities(String entitiesStr) {
        if (entitiesStr == null || entitiesStr.trim().isEmpty()) {
            return new HashMap<>();
        }
        try {
            com.fasterxml.jackson.databind.ObjectMapper mapper = new com.fasterxml.jackson.databind.ObjectMapper();
            return mapper.readValue(entitiesStr, new com.fasterxml.jackson.core.type.TypeReference<Map<String, String>>() {});
        } catch (Exception e) {
            // 如果解析失败，返回空Map
            return new HashMap<>();
        }
    }

    /**
     * 解析风险因子字符串为RiskFactors对象
     */
    private static RiskFactors parseRiskFactors(String riskFactorsStr) {
        if (riskFactorsStr == null || riskFactorsStr.trim().isEmpty()) {
            return null;
        }
        try {
            // 尝试从JSON字符串解析
            com.fasterxml.jackson.databind.ObjectMapper mapper = new com.fasterxml.jackson.databind.ObjectMapper();
            return mapper.readValue(riskFactorsStr, RiskFactors.class);
        } catch (Exception e) {
            // 如果解析失败，返回默认风险因子
            return RiskFactors.simple(
                List.of("未知风险"),
                "无法解析的风险因子信息",
                3
            );
        }
    }
}