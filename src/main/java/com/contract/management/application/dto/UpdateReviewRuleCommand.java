package com.contract.management.application.dto;

import lombok.Data;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.util.List;

/**
 * 更新审查规则命令
 *
 * @author SaltyFish
 * @since 1.0.0
 */
@Data
public class UpdateReviewRuleCommand {

    /**
     * 规则名称
     */
    @NotBlank(message = "规则名称不能为空")
    @Size(max = 255, message = "规则名称长度不能超过255个字符")
    private String ruleName;

    /**
     * 规则类型
     */
    @NotBlank(message = "规则类型不能为空")
    private String ruleType;

    /**
     * 适用合同类型
     */
    @NotEmpty(message = "适用合同类型不能为空")
    private List<String> applicableContractType;

    /**
     * 适用条款类型
     */
    @NotEmpty(message = "适用条款类型不能为空")
    private List<String> applicableClauseType;

    /**
     * 规则内容
     */
    @NotBlank(message = "规则内容不能为空")
    private String ruleContent;

    /**
     * 关联提示词模式编码
     */
    @NotNull(message = "提示词模式不能为空")
    private Short promptModeCode;

    /**
     * 备注
     */
    private String remark;
}