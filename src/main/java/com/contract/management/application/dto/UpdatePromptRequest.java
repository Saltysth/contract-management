package com.contract.management.application.dto;

import com.contract.management.domain.model.valueobject.PromptRole;
import com.contract.management.domain.model.valueobject.PromptType;
import lombok.Data;

import jakarta.validation.constraints.Size;

/**
 * 更新提示词模板请求DTO
 *
 * @author SaltyFish
 * @since 1.0.0
 */
@Data
public class UpdatePromptRequest {

    /**
     * 提示词模板名称
     */
    @Size(max = 120, message = "提示词模板名称不能超过120个字符")
    private String promptName;

    /**
     * 提示词内容
     */
    private String promptContent;

    /**
     * 是否启用
     */
    private Boolean enabled;

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