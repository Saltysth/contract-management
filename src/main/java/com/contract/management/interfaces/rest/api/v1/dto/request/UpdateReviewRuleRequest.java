package com.contract.management.interfaces.rest.api.v1.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.util.List;

/**
 * 更新审查规则请求
 *
 * @author SaltyFish
 * @since 1.0.0
 */
@Data
@Schema(description = "更新审查规则请求")
public class UpdateReviewRuleRequest {

    /**
     * 规则名称
     */
    @NotBlank(message = "规则名称不能为空")
    @Size(max = 255, message = "规则名称长度不能超过255个字符")
    @Schema(description = "规则名称", example = "采购合同-质量异议期校验", required = true)
    private String ruleName;

    /**
     * 规则类型
     */
    @NotBlank(message = "规则类型不能为空")
    @Schema(description = "规则类型", example = "specific", required = true, allowableValues = {"specific", "fallback", "extended"})
    private String ruleType;

    /**
     * 适用合同类型
     */
    @NotEmpty(message = "适用合同类型不能为空")
    @Schema(description = "适用合同类型", example = "[\"采购合同\", \"销售合同\"]", required = true)
    private List<String> applicableContractType;

    /**
     * 适用条款类型
     */
    @NotEmpty(message = "适用条款类型不能为空")
    @Schema(description = "适用条款类型", example = "[\"付款条款\", \"违约责任\"]", required = true)
    private List<String> applicableClauseType;

    /**
     * 规则内容
     */
    @NotBlank(message = "规则内容不能为空")
    @Schema(description = "规则内容", example = "检查合同中是否明确规定了质量异议期的具体时限...", required = true)
    private String ruleContent;

    /**
     * 关联提示词模式编码
     */
    @NotNull(message = "提示词模式不能为空")
    @Schema(description = "关联提示词模式编码", example = "1", required = true, allowableValues = {"0", "1", "2"})
    private Short promptModeCode;

    /**
     * 备注
     */
    @Schema(description = "备注", example = "根据《民法典》相关规定设置")
    private String remark;
}