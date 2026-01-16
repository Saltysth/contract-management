package com.contract.management.domain.model;

import com.contract.management.domain.model.valueobject.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.Objects;

/**
 * 审查规则聚合根
 * 用于管理合同审查的业务规则
 *
 * @author SaltyFish
 * @since 1.0.0
 */
@Getter
public class ReviewRule {

    /**
     * 规则ID
     */
    private final ReviewRuleId id;

    /**
     * 规则名称
     */
    @Setter
    private RuleName ruleName;

    /**
     * 规则类型
     */
    private RuleType ruleType;

    /**
     * 适用合同类型
     */
    @Setter
    private ApplicableContractTypes applicableContractTypes;

    /**
     * 适用条款类型
     */
    @Setter
    private ApplicableClauseTypes applicableClauseTypes;

    /**
     * 规则内容
     */
    @Setter
    private RuleContent ruleContent;

    /**
     * 关联提示词模式
     */
    @Setter
    private PromptMode promptMode;

    /**
     * 启用状态
     */
    @Setter
    private Boolean enabled;

    /**
     * 规则创建时间
     */
    private final LocalDateTime createTime;

    /**
     * 规则更新时间
     */
    @Setter
    private LocalDateTime updateTime;

    /**
     * 备注
     */
    @Setter
    private String remark;

    /**
     * 创建新规则（数据库构造）
     */
    public ReviewRule(ReviewRuleId id, RuleName ruleName, RuleType ruleType,
                      ApplicableContractTypes applicableContractTypes,
                      ApplicableClauseTypes applicableClauseTypes,
                      RuleContent ruleContent, PromptMode promptMode, Boolean enabled,
                      LocalDateTime createTime, LocalDateTime updateTime, String remark) {
        this.id = Objects.requireNonNull(id, "Review rule ID cannot be null");
        this.ruleName = Objects.requireNonNull(ruleName, "Rule name cannot be null");
        this.ruleType = Objects.requireNonNull(ruleType, "Rule type cannot be null");
        this.applicableContractTypes = Objects.requireNonNull(applicableContractTypes, "Applicable contract types cannot be null");
        this.applicableClauseTypes = Objects.requireNonNull(applicableClauseTypes, "Applicable clause types cannot be null");
        this.ruleContent = Objects.requireNonNull(ruleContent, "Rule content cannot be null");
        this.promptMode = Objects.requireNonNull(promptMode, "Prompt mode cannot be null");
        this.enabled = enabled != null ? enabled : true;
        this.createTime = Objects.requireNonNull(createTime, "Create time cannot be null");
        this.updateTime = updateTime != null ? updateTime : createTime;
        this.remark = remark;

        validate();
    }

    /**
     * 创建新规则（业务构造）
     */
    public ReviewRule(RuleName ruleName, RuleType ruleType,
                      ApplicableContractTypes applicableContractTypes,
                      ApplicableClauseTypes applicableClauseTypes,
                      RuleContent ruleContent, PromptMode promptMode, String remark) {
        this.id = null; // 新创建的规则ID将由数据库生成
        this.ruleName = Objects.requireNonNull(ruleName, "Rule name cannot be null");
        this.ruleType = Objects.requireNonNull(ruleType, "Rule type cannot be null");
        this.applicableContractTypes = Objects.requireNonNull(applicableContractTypes, "Applicable contract types cannot be null");
        this.applicableClauseTypes = Objects.requireNonNull(applicableClauseTypes, "Applicable clause types cannot be null");
        this.ruleContent = Objects.requireNonNull(ruleContent, "Rule content cannot be null");
        this.promptMode = Objects.requireNonNull(promptMode, "Prompt mode cannot be null");
        this.enabled = true; // 默认启用
        this.createTime = LocalDateTime.now();
        this.updateTime = LocalDateTime.now();
        this.remark = remark;

        validate();
    }

    /**
     * 验证业务规则
     */
    private void validate() {
        // 扩展规则必须是严格模式
        if (ruleType == RuleType.EXTENDED && promptMode.getCode() < 2) {
            throw new IllegalArgumentException("Extended rules must be in strict mode");
        }

        // 兜底规则应该是 fallback 类型
        if (applicableContractTypes.isFallback() && ruleType != RuleType.FALLBACK) {
            throw new IllegalArgumentException("Fallback rules must have rule type FALLBACK");
        }
    }

    /**
     * 检查规则是否适用于指定的合同类型和条款类型
     */
    public boolean isApplicableTo(String contractType, String clauseType) {
        return enabled && applicableContractTypes.appliesTo(contractType)
               && applicableClauseTypes.appliesTo(clauseType);
    }

    /**
     * 检查规则是否在指定提示词模式下生效
     */
    public boolean isEffectiveInMode(PromptMode targetMode) {
        return enabled && targetMode.isRuleApplicable(promptMode.getCode());
    }

    /**
     * 启用规则
     */
    public void enable() {
        this.enabled = true;
        this.updateTime = LocalDateTime.now();
    }

    /**
     * 禁用规则
     */
    public void disable() {
        this.enabled = false;
        this.updateTime = LocalDateTime.now();
    }

    /**
     * 更新规则信息
     */
    public void updateRule(RuleName ruleName, RuleType ruleType, ApplicableContractTypes applicableContractTypes,
                          ApplicableClauseTypes applicableClauseTypes, RuleContent ruleContent,
                          PromptMode promptMode, String remark) {
        this.ruleName = Objects.requireNonNull(ruleName, "Rule name cannot be null");
        this.ruleType = Objects.requireNonNull(ruleType, "Applicable contract types cannot be null");
        this.applicableContractTypes = Objects.requireNonNull(applicableContractTypes, "Applicable contract types cannot be null");
        this.applicableClauseTypes = Objects.requireNonNull(applicableClauseTypes, "Applicable clause types cannot be null");
        this.ruleContent = Objects.requireNonNull(ruleContent, "Rule content cannot be null");
        this.promptMode = Objects.requireNonNull(promptMode, "Prompt mode cannot be null");
        this.remark = remark;
        this.updateTime = LocalDateTime.now();

        validate();
    }

    /**
     * 更新时间戳
     */
    public void markAsModified() {
        this.updateTime = LocalDateTime.now();
    }

    /**
     * 检查是否为兜底规则
     */
    public boolean isFallbackRule() {
        return RuleType.FALLBACK.equals(ruleType);
    }

    /**
     * 检查是否为扩展规则
     */
    public boolean isExtendedRule() {
        return RuleType.EXTENDED.equals(ruleType);
    }

    /**
     * 检查是否为专属规则
     */
    public boolean isSpecificRule() {
        return RuleType.SPECIFIC.equals(ruleType);
    }

    /**
     * 获取模式编码
     */
    public short getPromptModeCode() {
        return promptMode.getCode();
    }

    /**
     * 获取规则类型代码
     */
    public String getRuleTypeCode() {
        return ruleType.getCode();
    }

    /**
     * 检查是否适用于所有合同类型
     */
    public boolean appliesToAllContracts() {
        return applicableContractTypes.isFallback();
    }

    /**
     * 获取适用的合同类型列表
     */
    public java.util.List<String> getApplicableContractTypeList() {
        return applicableContractTypes.getValues();
    }

    /**
     * 获取适用的条款类型列表
     */
    public java.util.List<String> getApplicableClauseTypeList() {
        return applicableClauseTypes.getValues();
    }
}