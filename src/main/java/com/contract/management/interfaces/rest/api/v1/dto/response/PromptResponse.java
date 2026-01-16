package com.contract.management.interfaces.rest.api.v1.dto.response;

import com.contract.management.domain.model.valueobject.PromptRole;
import com.contract.management.domain.model.valueobject.PromptType;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 提示词模板响应DTO
 *
 * @author SaltyFish
 * @since 1.0.0
 */
@Data
@Schema(description = "提示词模板响应")
public class PromptResponse {

    /**
     * 提示词模板ID
     */
    @Schema(description = "提示词模板ID", example = "1")
    private Long id;

    /**
     * 提示词模板名称
     */
    @Schema(description = "提示词模板名称", example = "合同审查提示词")
    private String promptName;

    /**
     * 提示词模板类型
     */
    @Schema(description = "提示词模板类型", example = "CUSTOM")
    private PromptType promptType;

    /**
     * 提示词角色
     */
    @Schema(description = "提示词角色", example = "SYSTEM")
    private PromptRole promptRole;

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
    @Schema(description = "响应格式", example = "markdown")
    private String responseFormat;

    /**
     * 备注
     */
    @Schema(description = "备注", example = "用于合同审查的标准提示词")
    private String remark;

    /**
     * 创建人
     */
    @Schema(description = "创建人", example = "1")
    private Long createdBy;

    /**
     * 更新人
     */
    @Schema(description = "更新人", example = "1")
    private Long updatedBy;

    /**
     * 创建时间
     */
    @Schema(description = "创建时间", example = "2024-01-15T10:30:00")
    private LocalDateTime createdTime;

    /**
     * 更新时间
     */
    @Schema(description = "更新时间", example = "2024-01-16T09:20:00")
    private LocalDateTime updatedTime;

    /**
     * 版本号
     */
    @Schema(description = "版本号", example = "1")
    private Long objectVersionNumber;
}