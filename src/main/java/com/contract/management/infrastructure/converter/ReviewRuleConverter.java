package com.contract.management.infrastructure.converter;

import com.contract.management.domain.model.ReviewRule;
import com.contract.management.domain.model.valueobject.*;
import com.contract.management.infrastructure.entity.ReviewRuleEntity;

/**
 * 审查规则转换器
 * 用于领域模型和基础设施实体之间的转换
 *
 * @author SaltyFish
 * @since 1.0.0
 */
public class ReviewRuleConverter {

    /**
     * 领域模型转实体
     */
    public static ReviewRuleEntity toEntity(ReviewRule domain) {
        if (domain == null) {
            return null;
        }

        ReviewRuleEntity entity = new ReviewRuleEntity();
        if (domain.getId() != null) {
            entity.setId(domain.getId().getValue());
        }
        entity.setRuleName(domain.getRuleName().getValue());
        entity.setRuleType(domain.getRuleType());
        entity.setApplicableContractType(domain.getApplicableContractTypes());
        entity.setApplicableClauseType(domain.getApplicableClauseTypes());
        entity.setRuleContent(domain.getRuleContent().getValue());
        entity.setPromptModeCode(domain.getPromptModeCode());
        entity.setEnabled(domain.getEnabled());
        entity.setCreateTime(domain.getCreateTime());
        entity.setUpdateTime(domain.getUpdateTime());
        entity.setRemark(domain.getRemark());

        return entity;
    }

    /**
     * 实体转领域模型
     */
    public static ReviewRule toDomain(ReviewRuleEntity entity) {
        if (entity == null) {
            return null;
        }

        ReviewRuleId id = ReviewRuleId.of(entity.getId());
        RuleName ruleName = RuleName.of(entity.getRuleName());
        RuleType ruleType = entity.getRuleType();
        ApplicableContractTypes applicableContractTypes = entity.getApplicableContractType();
        ApplicableClauseTypes applicableClauseTypes = entity.getApplicableClauseType();
        RuleContent ruleContent = RuleContent.of(entity.getRuleContent());
        PromptMode promptMode = PromptMode.fromCode(entity.getPromptModeCode());

        return new ReviewRule(id, ruleName, ruleType, applicableContractTypes, applicableClauseTypes,
                ruleContent, promptMode, entity.getEnabled(), entity.getCreateTime(),
                entity.getUpdateTime(), entity.getRemark());
    }

    /**
     * 领域模型列表转实体列表
     */
    public static java.util.List<ReviewRuleEntity> toEntityList(java.util.List<ReviewRule> domains) {
        return domains.stream()
                .map(ReviewRuleConverter::toEntity)
                .toList();
    }

    /**
     * 实体列表转领域模型列表
     */
    public static java.util.List<ReviewRule> toDomainList(java.util.List<ReviewRuleEntity> entities) {
        return entities.stream()
                .map(ReviewRuleConverter::toDomain)
                .toList();
    }
}