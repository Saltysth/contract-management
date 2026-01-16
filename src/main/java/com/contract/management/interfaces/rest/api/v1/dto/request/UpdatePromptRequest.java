package com.contract.management.interfaces.rest.api.v1.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import jakarta.validation.constraints.Size;

/**
 * 更新提示词模板请求DTO
 *
 * @author SaltyFish
 * @since 1.0.0
 */
@Data
@Schema(description = "更新提示词模板请求")
public class UpdatePromptRequest {

    /**
     * 提示词模板名称
     */
    @Size(max = 120, message = "提示词模板名称不能超过120个字符")
    @Schema(description = "提示词模板名称", example = "合同审查提示词")
    private String promptName;

    /**
     * 提示词内容
     */
    @Schema(description = "提示词内容", example = "请帮我审查以下合同...")
    private String promptContent;

    /**
     * 是否启用
     */
    @Schema(description = "是否启用", example = "true")
    private Boolean enabled;

    /**
     * 响应格式
     */
    @Size(max = 30, message = "响应格式不能超过30个字符")
    @Schema(description = "响应格式", example = "markdown")
    private String responseFormat;

    /**
     * 备注
     */
    @Size(max = 600, message = "备注不能超过600个字符")
    @Schema(description = "备注", example = "用于合同审查的标准提示词")
    private String remark;
}