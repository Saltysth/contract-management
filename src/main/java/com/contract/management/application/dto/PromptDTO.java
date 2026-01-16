package com.contract.management.application.dto;

import com.contract.management.domain.model.valueobject.PromptRole;
import com.contract.management.domain.model.valueobject.PromptType;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 提示词模板应用层DTO
 * 用于应用服务层的数据传输
 *
 * @author SaltyFish
 * @since 1.0.0
 */
@Data
public class PromptDTO {

    /**
     * 提示词模板ID
     */
    private Long id;

    /**
     * 提示词模板名称
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
    private String responseFormat;

    /**
     * 备注
     */
    private String remark;

    /**
     * 创建人
     */
    private Long createdBy;

    /**
     * 更新人
     */
    private Long updatedBy;

    /**
     * 创建时间
     */
    private LocalDateTime createdTime;

    /**
     * 更新时间
     */
    private LocalDateTime updatedTime;

    /**
     * 版本号
     */
    private Long objectVersionNumber;
}