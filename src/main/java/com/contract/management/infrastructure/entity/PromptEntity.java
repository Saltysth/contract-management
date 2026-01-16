package com.contract.management.infrastructure.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;

/**
 * 提示词模板实体类
 *
 * @author SaltyFish
 * @since 1.0.0
 */
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("prompt")
public class PromptEntity {

    /**
     * 提示词模板ID
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 提示词模板名称
     */
    @TableField("prompt_name")
    private String promptName;

    /**
     * 提示词模板类型
     */
    @TableField("prompt_type")
    private String promptType;

    /**
     * 提示词角色
     */
    @TableField("prompt_role")
    private String promptRole;

    /**
     * 提示词内容
     */
    @TableField("prompt_content")
    private String promptContent;

    /**
     * 是否启用
     */
    @TableField("enabled")
    private Boolean enabled;

    /**
     * 响应格式
     */
    @TableField("response_format")
    private String responseFormat;

    /**
     * 备注
     */
    @TableField("remark")
    private String remark;

    /**
     * 创建人ID
     */
    @TableField(value = "created_by", fill = FieldFill.INSERT)
    private Long createdBy;

    /**
     * 更新人ID
     */
    @TableField(value = "updated_by", fill = FieldFill.INSERT_UPDATE)
    private Long updatedBy;

    /**
     * 创建时间
     */
    @TableField(value = "created_time", fill = FieldFill.INSERT)
    private LocalDateTime createdTime;

    /**
     * 更新时间
     */
    @TableField(value = "updated_time", fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updatedTime;

    /**
     * 版本号（乐观锁）
     */
    @Version
    @TableField("object_version_number")
    private Long objectVersionNumber;
}