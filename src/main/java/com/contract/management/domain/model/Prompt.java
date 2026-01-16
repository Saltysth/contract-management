package com.contract.management.domain.model;

import com.contract.management.domain.model.valueobject.AuditInfo;
import com.contract.management.domain.model.valueobject.PromptContent;
import com.contract.management.domain.model.valueobject.PromptName;
import com.contract.management.domain.model.valueobject.PromptRole;
import com.contract.management.domain.model.valueobject.PromptType;
import com.contract.management.domain.model.valueobject.ResponseFormat;
import lombok.Getter;

/**
 * 提示词模板聚合根
 *
 * @author SaltyFish
 * @since 1.0.0
 */
@Getter
public class Prompt {

    private final PromptId id;
    private PromptName promptName;
    private final PromptType promptType;
    private final PromptRole promptRole;
    private PromptContent promptContent;
    private Boolean enabled;
    private ResponseFormat responseFormat;
    private String remark;
    private AuditInfo auditInfo;

    /**
     * 构造函数 - 创建新提示词模板
     */
    public Prompt(PromptId id, PromptName promptName, PromptType promptType, PromptRole promptRole,
                  PromptContent promptContent, Boolean enabled, ResponseFormat responseFormat,
                  String remark, Long createdBy) {
        this.id = id;
        this.promptName = promptName;
        this.promptType = promptType;
        this.promptRole = promptRole;
        this.promptContent = promptContent;
        this.enabled = enabled != null ? enabled : Boolean.TRUE;
        this.responseFormat = responseFormat;
        this.remark = remark;
        this.auditInfo = AuditInfo.create(createdBy);

        // 创建时验证
        validateForCreation();
    }

    /**
     * 构造函数 - 从数据库重建
     */
    public Prompt(PromptId id, PromptName promptName, PromptType promptType, PromptRole promptRole,
                  PromptContent promptContent, Boolean enabled, ResponseFormat responseFormat,
                  String remark, AuditInfo auditInfo) {
        this.id = id;
        this.promptName = promptName;
        this.promptType = promptType;
        this.promptRole = promptRole;
        this.promptContent = promptContent;
        this.enabled = enabled;
        this.responseFormat = responseFormat;
        this.remark = remark;
        this.auditInfo = auditInfo;
    }

    // ==================== 领域行为方法 ====================

    /**
     * 更新基本信息
     */
    public void updateBasicInfo(PromptName promptName, PromptContent promptContent,
                               ResponseFormat responseFormat, String remark) {
        if (promptName != null) {
            this.promptName = promptName;
        }
        if (promptContent != null) {
            this.promptContent = promptContent;
        }
        this.responseFormat = responseFormat;
        this.remark = remark;
        this.auditInfo = this.auditInfo.update();

        validateForUpdate();
    }

    /**
     * 启用提示词模板
     */
    public void enable() {
        this.enabled = Boolean.TRUE;
        this.auditInfo = this.auditInfo.update();
    }

    /**
     * 禁用提示词模板
     */
    public void disable() {
        this.enabled = Boolean.FALSE;
        this.auditInfo = this.auditInfo.update();
    }

    /**
     * 检查是否可以删除
     * 内置模板不允许删除，只能禁用
     */
    public boolean canDelete() {
        return !PromptType.INNER.equals(this.promptType);
    }

    /**
     * 检查是否为系统内置模板
     */
    public boolean isInner() {
        return PromptType.INNER.equals(this.promptType);
    }

    /**
     * 检查是否为用户自定义模板
     */
    public boolean isCustom() {
        return PromptType.CUSTOM.equals(this.promptType);
    }

    /**
     * 检查是否启用
     */
    public boolean isEnabled() {
        return Boolean.TRUE.equals(this.enabled);
    }

    // ==================== 业务规则验证方法 ====================

    /**
     * 验证创建时的业务规则
     */
    public void validateForCreation() {
        validateBasicInfo();
        validatePromptTypeAndRole();
    }

    /**
     * 验证更新时的业务规则
     */
    public void validateForUpdate() {
        validateBasicInfo();
        validatePromptTypeAndRole();
    }

    /**
     * 验证基本信息
     */
    private void validateBasicInfo() {
        if (promptName == null) {
            throw new IllegalArgumentException("提示词模板名称不能为空");
        }
    }

    /**
     * 验证提示词类型和角色
     */
    private void validatePromptTypeAndRole() {
        if (promptType == null) {
            throw new IllegalArgumentException("提示词模板类型不能为空");
        }
        if (promptRole == null) {
            throw new IllegalArgumentException("提示词角色不能为空");
        }
    }

    // ==================== 辅助方法 ====================

    /**
     * 创建提示词模板的副本（用于审计）
     */
    public Prompt copy() {
        return new Prompt(
            this.id,
            this.promptName,
            this.promptType,
            this.promptRole,
            this.promptContent,
            this.enabled,
            this.responseFormat,
            this.remark,
            this.auditInfo
        );
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Prompt prompt = (Prompt) o;
        return id != null && id.equals(prompt.id);
    }

    @Override
    public int hashCode() {
        return id != null ? id.hashCode() : 0;
    }

    @Override
    public String toString() {
        return "Prompt{" +
                "id=" + id +
                ", promptName=" + promptName +
                ", promptType=" + promptType +
                ", promptRole=" + promptRole +
                ", enabled=" + enabled +
                '}';
    }
}