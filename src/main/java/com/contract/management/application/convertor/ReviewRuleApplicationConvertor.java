package com.contract.management.application.convertor;

import com.contract.management.application.dto.*;
import com.contract.management.domain.model.ReviewRule;
import com.contract.management.domain.model.valueobject.*;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

import java.util.List;

/**
 * 审查规则应用层转换器
 * 使用MapStruct自动生成转换代码
 * 命名以Mapping结尾避免与MyBatis的Mapper冲突
 *
 * @author SaltyFish
 * @since 1.0.0
 */
@Mapper(componentModel = "spring")
public interface ReviewRuleApplicationConvertor {

    /**
     * 领域模型转DTO
     */
    @Mapping(source = "id.value", target = "id")
    @Mapping(source = "ruleName.value", target = "ruleName")
    @Mapping(source = "ruleType.code", target = "ruleType")
    @Mapping(source = "ruleType.description", target = "ruleTypeDescription")
    @Mapping(source = "applicableContractTypes", target = "applicableContractType", qualifiedByName = "contractTypesToList")
    @Mapping(source = "applicableClauseTypes", target = "applicableClauseType", qualifiedByName = "clauseTypesToList")
    @Mapping(source = "ruleContent.value", target = "ruleContent")
    @Mapping(source = "promptMode.code", target = "promptModeCode")
    @Mapping(source = "promptMode.description", target = "promptModeDescription")
    ReviewRuleDTO toDTO(ReviewRule domain);

    /**
     * 领域模型列表转DTO列表
     */
    List<ReviewRuleDTO> toDTOList(List<ReviewRule> domains);

    // 自定义转换方法

    @Named("contractTypesToList")
    default List<String> contractTypesToList(ApplicableContractTypes applicableContractTypes) {
        return applicableContractTypes != null ? applicableContractTypes.getValues() : null;
    }

    @Named("clauseTypesToList")
    default List<String> clauseTypesToList(ApplicableClauseTypes applicableClauseTypes) {
        return applicableClauseTypes != null ? applicableClauseTypes.getValues() : null;
    }

    // 手动实现转换方法，避免MapStruct的复杂映射问题
    default ReviewRule toDomainForCreate(CreateReviewRuleCommand command) {
        return new ReviewRule(
            RuleName.of(command.getRuleName()),
            RuleType.fromCode(command.getRuleType()),
            ApplicableContractTypes.of(command.getApplicableContractType()),
            ApplicableClauseTypes.of(command.getApplicableClauseType()),
            RuleContent.of(command.getRuleContent()),
            PromptMode.fromCode(command.getPromptModeCode()),
            command.getRemark()
        );
    }

    default ReviewRule toDomainForUpdate(Long id, UpdateReviewRuleCommand command) {
        // 这里简化处理，直接创建新的领域对象，实际的更新逻辑在应用服务中处理
        return new ReviewRule(
            RuleName.of(command.getRuleName()),
            RuleType.fromCode(command.getRuleType()),
            ApplicableContractTypes.of(command.getApplicableContractType()),
            ApplicableClauseTypes.of(command.getApplicableClauseType()),
            RuleContent.of(command.getRuleContent()),
            PromptMode.fromCode(command.getPromptModeCode()),
            command.getRemark()
        );
    }
}