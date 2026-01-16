package com.contract.management.application.dto;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 审查规则应用层DTO
 *
 * @author SaltyFish
 * @since 1.0.0
 */
@Data
public class ReviewRuleDTO {

    /**
     * 规则ID
     */
    private Long id;

    /**
     * 规则名称
     */
    private String ruleName;

    /**
     * 规则类型代码
     */
    private String ruleType;

    /**
     * 规则类型描述
     */
    private String ruleTypeDescription;

    /**
     * 适用合同类型
     */
    private List<String> applicableContractType;

    /**
     * 适用条款类型
     */
    private List<String> applicableClauseType;

    /**
     * 规则内容
     */
    private String ruleContent;

    /**
     * 关联提示词模式编码
     */
    private Short promptModeCode;

    /**
     * 提示词模式描述
     */
    private String promptModeDescription;

    /**
     * 规则启用状态
     */
    private Boolean enabled;

    /**
     * 规则创建时间
     */
    private LocalDateTime createTime;

    /**
     * 规则更新时间
     */
    private LocalDateTime updateTime;

    /**
     * 备注
     */
    private String remark;

    /**
     * 检查是否为启用状态
     */
    public boolean isEnabled() {
        return Boolean.TRUE.equals(enabled);
    }

    /**
     * 检查是否为兜底规则
     */
    public boolean isFallbackRule() {
        return "fallback".equals(ruleType);
    }

    /**
     * 检查是否为扩展规则
     */
    public boolean isExtendedRule() {
        return "extended".equals(ruleType);
    }

    /**
     * 检查是否适用于所有合同类型
     */
    public boolean appliesToAllContracts() {
        return applicableContractType != null &&
               applicableContractType.size() == 1 &&
               "all".equalsIgnoreCase(applicableContractType.get(0));
    }
}