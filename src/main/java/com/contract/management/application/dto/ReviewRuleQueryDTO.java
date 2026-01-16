package com.contract.management.application.dto;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 审查规则查询数据传输对象
 * 用于应用层的查询条件封装
 */
@Data
public class ReviewRuleQueryDTO {

    /**
     * 规则名称（模糊查询）
     */
    private String ruleName;

    /**
     * 规则类型列表
     */
    private List<String> ruleTypeList;

    /**
     * 适用合同类型列表
     */
    private List<String> applicableContractTypes;

    /**
     * 适用条款类型列表
     */
    private List<String> applicableClauseTypes;

    /**
     * 提示词模式代码列表
     */
    private List<String> promptModeCodeList;

    /**
     * 是否启用
     */
    private Boolean enabled;

    /**
     * 关键词搜索（搜索名称、内容、备注）
     */
    private String keyword;

    /**
     * 创建时间范围 - 开始时间
     */
    private LocalDateTime createTimeStart;

    /**
     * 创建时间范围 - 结束时间
     */
    private LocalDateTime createTimeEnd;

    /**
     * 更新时间范围 - 开始时间
     */
    private LocalDateTime updateTimeStart;

    /**
     * 更新时间范围 - 结束时间
     */
    private LocalDateTime updateTimeEnd;

    /**
     * 是否只查询适用的规则
     */
    private Boolean onlyApplicable;

    /**
     * 指定合同类型（用于查询适用规则）
     */
    private String contractType;

    /**
     * 指定条款类型（用于查询适用规则）
     */
    private String clauseType;
}