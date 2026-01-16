package com.contract.management.application.dto;

import com.contract.management.domain.model.valueobject.PromptRole;
import com.contract.management.domain.model.valueobject.PromptType;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 提示词模板查询应用DTO
 *
 * @author SaltyFish
 * @since 1.0.0
 */
@Data
public class PromptQueryDTO {

    /**
     * 提示词模板名称（模糊查询）
     */
    private String promptName;

    /**
     * 提示词模板类型列表
     */
    private List<PromptType> promptTypes;

    /**
     * 提示词角色列表
     */
    private List<PromptRole> promptRoles;

    /**
     * 是否启用
     */
    private Boolean enabled;

    /**
     * 响应格式
     */
    private String responseFormat;

    /**
     * 创建人ID
     */
    private Long createdBy;

    /**
     * 创建时间范围 - 开始时间
     */
    private LocalDateTime createdTimeStart;

    /**
     * 创建时间范围 - 结束时间
     */
    private LocalDateTime createdTimeEnd;

    /**
     * 更新时间范围 - 开始时间
     */
    private LocalDateTime updatedTimeStart;

    /**
     * 更新时间范围 - 结束时间
     */
    private LocalDateTime updatedTimeEnd;

    /**
     * 备注（模糊查询）
     */
    private String remark;

    /**
     * 关键词（搜索名称和备注）
     */
    private String keyword;
}