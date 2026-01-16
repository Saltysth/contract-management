package com.contract.management.application.dto;

import com.contract.management.domain.model.valueobject.PromptRole;
import com.contract.management.domain.model.valueobject.PromptType;
import lombok.Data;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

/**
 * 创建提示词模板请求DTO
 *
 * @author SaltyFish
 * @since 1.0.0
 */
@Data
public class CreatePromptRequest {

    /**
     * 提示词模板名称
     */
    @NotBlank(message = "提示词模板名称不能为空")
    @Size(max = 120, message = "提示词模板名称不能超过120个字符")
    private String promptName;

    /**
     * 提示词模板类型
     */
    @NotNull(message = "提示词模板类型不能为空")
    private PromptType promptType;

    /**
     * 提示词角色
     */
    @NotNull(message = "提示词角色不能为空")
    private PromptRole promptRole;

    /**
     * 提示词内容
     */
    private String promptContent;

    /**
     * 是否启用
     */
    private Boolean enabled = true;

    /**
     * 响应格式
     */
    @Size(max = 30, message = "响应格式不能超过30个字符")
    private String responseFormat;

    /**
     * 备注
     */
    @Size(max = 600, message = "备注不能超过600个字符")
    private String remark;
}