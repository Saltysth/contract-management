package com.contract.management.infrastructure.entity;

import com.baomidou.mybatisplus.annotation.*;
import com.contract.management.infrastructure.handler.ApplicableClauseTypesHandler;
import com.contract.management.infrastructure.handler.ApplicableContractTypesHandler;
import com.contract.management.infrastructure.handler.ReviewTypeDetailHandler;
import com.contract.management.infrastructure.handler.RuleTypeHandler;
import com.contract.management.domain.model.valueobject.ApplicableContractTypes;
import com.contract.management.domain.model.valueobject.ApplicableClauseTypes;
import com.contract.management.domain.model.valueobject.RuleType;
import com.contract.common.enums.ReviewTypeDetail;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 审查规则实体
 * 使用安全的TypeHandler避免类型冲突
 *
 * @author SaltyFish
 * @since 1.0.0
 */
@Data
@TableName("contract_review_rule")
public class ReviewRuleEntity {

    /**
     * 规则主键ID
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 规则名称
     */
    private String ruleName;

    /**
     * 规则类型
     */
    @TableField(typeHandler = RuleTypeHandler.class)
    private RuleType ruleType;

    /**
     * 适用合同类型（数组格式）
     */
    @TableField(typeHandler = ApplicableContractTypesHandler.class)
    private ApplicableContractTypes applicableContractType;

    /**
     * 适用条款类型（数组格式）
     */
    @TableField(typeHandler = ApplicableClauseTypesHandler.class)
    private ApplicableClauseTypes applicableClauseType;

    /**
     * 规则分类（非必填）
     */
    @TableField(typeHandler = ReviewTypeDetailHandler.class)
    private ReviewTypeDetail category;

    /**
     * 规则内容
     */
    private String ruleContent;

    /**
     * 关联提示词模式编码
     */
    private Short promptModeCode;

    /**
     * 规则启用状态
     */
    private Boolean enabled;

    /**
     * 规则创建时间
     */
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    /**
     * 规则更新时间
     */
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;

    /**
     * 备注
     */
    private String remark;
}