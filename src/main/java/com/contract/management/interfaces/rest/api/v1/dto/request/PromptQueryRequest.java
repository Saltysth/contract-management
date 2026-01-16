package com.contract.management.interfaces.rest.api.v1.dto.request;

import com.contract.management.domain.model.valueobject.PromptRole;
import com.contract.management.domain.model.valueobject.PromptType;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 提示词模板查询请求DTO
 *
 * @author SaltyFish
 * @since 1.0.0
 */
@Data
@Schema(description = "提示词模板查询请求")
public class PromptQueryRequest {

    /**
     * 提示词模板名称（模糊查询）
     */
    @Schema(description = "提示词模板名称", example = "合同审查")
    private String promptName;

    /**
     * 提示词模板类型列表
     */
    @Schema(description = "提示词模板类型列表", example = "[CUSTOM]")
    private List<PromptType> promptTypes;

    /**
     * 提示词角色列表
     */
    @Schema(description = "提示词角色列表", example = "[SYSTEM]")
    private List<PromptRole> promptRoles;

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
     * 创建人ID
     */
    @Schema(description = "创建人ID", example = "1")
    private Long createdBy;

    /**
     * 创建时间范围 - 开始时间
     */
    @Schema(description = "创建时间开始", example = "2024-01-01T00:00:00")
    private LocalDateTime createdTimeStart;

    /**
     * 创建时间范围 - 结束时间
     */
    @Schema(description = "创建时间结束", example = "2024-12-31T23:59:59")
    private LocalDateTime createdTimeEnd;

    /**
     * 更新时间范围 - 开始时间
     */
    @Schema(description = "更新时间开始", example = "2024-01-01T00:00:00")
    private LocalDateTime updatedTimeStart;

    /**
     * 更新时间范围 - 结束时间
     */
    @Schema(description = "更新时间结束", example = "2024-12-31T23:59:59")
    private LocalDateTime updatedTimeEnd;

    /**
     * 备注（模糊查询）
     */
    @Schema(description = "备注", example = "标准模板")
    private String remark;

    /**
     * 关键词（搜索名称和备注）
     */
    @Schema(description = "关键词", example = "合同")
    private String keyword;
}