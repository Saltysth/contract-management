package com.contract.management.domain.repository;

import com.contract.management.domain.model.valueobject.PromptRole;
import com.contract.management.domain.model.valueobject.PromptType;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

/**
 * 提示词模板查询过滤条件
 *
 * @author SaltyFish
 * @since 1.0.0
 */
@Getter
@Builder
public class PromptFilters {

    /**
     * 提示词模板名称（模糊查询）
     */
    private String promptName;

    /**
     * 提示词模板类型
     */
    private PromptType promptType;

    /**
     * 提示词角色
     */
    private PromptRole promptRole;

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
     * 创建时间开始范围
     */
    private LocalDateTime createdTimeStart;

    /**
     * 创建时间结束范围
     */
    private LocalDateTime createdTimeEnd;

    /**
     * 更新时间开始范围
     */
    private LocalDateTime updatedTimeStart;

    /**
     * 更新时间结束范围
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