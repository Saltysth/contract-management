package com.contract.management.interfaces.rest.api.v1.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 审查规则响应
 *
 * @author SaltyFish
 * @since 1.0.0
 */
@Data
@Schema(description = "审查规则响应")
public class ReviewRuleResponse {

    /**
     * 规则ID
     */
    @Schema(description = "规则ID", example = "1")
    private Long id;

    /**
     * 规则名称
     */
    @Schema(description = "规则名称", example = "采购合同-质量异议期校验")
    private String ruleName;

    /**
     * 规则类型代码
     */
    @Schema(description = "规则类型代码", example = "specific")
    private String ruleType;

    /**
     * 规则类型描述
     */
    @Schema(description = "规则类型描述", example = "专属规则")
    private String ruleTypeDescription;

    /**
     * 适用合同类型
     */
    @Schema(description = "适用合同类型", example = "[\"采购合同\", \"销售合同\"]")
    private List<String> applicableContractType;

    /**
     * 适用条款类型
     */
    @Schema(description = "适用条款类型", example = "[\"付款条款\", \"违约责任\"]")
    private List<String> applicableClauseType;

    /**
     * 规则内容
     */
    @Schema(description = "规则内容", example = "检查合同中是否明确规定了质量异议期的具体时限...")
    private String ruleContent;

    /**
     * 关联提示词模式编码
     */
    @Schema(description = "关联提示词模式编码", example = "1")
    private Short promptModeCode;

    /**
     * 提示词模式描述
     */
    @Schema(description = "提示词模式描述", example = "标准模式")
    private String promptModeDescription;

    /**
     * 规则启用状态
     */
    @Schema(description = "规则启用状态", example = "true")
    private Boolean enabled;

    /**
     * 规则创建时间
     */
    @Schema(description = "规则创建时间", example = "2024-01-01T10:00:00")
    private LocalDateTime createTime;

    /**
     * 规则更新时间
     */
    @Schema(description = "规则更新时间", example = "2024-01-01T10:00:00")
    private LocalDateTime updateTime;

    /**
     * 备注
     */
    @Schema(description = "备注", example = "根据《民法典》相关规定设置")
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