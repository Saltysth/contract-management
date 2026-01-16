package com.contract.management.infrastructure.converter;

import com.contract.management.domain.model.Prompt;
import com.contract.management.domain.model.PromptId;
import com.contract.management.domain.model.valueobject.AuditInfo;
import com.contract.management.domain.model.valueobject.PromptContent;
import com.contract.management.domain.model.valueobject.PromptName;
import com.contract.management.domain.model.valueobject.PromptRole;
import com.contract.management.domain.model.valueobject.PromptType;
import com.contract.management.domain.model.valueobject.ResponseFormat;
import com.contract.management.infrastructure.entity.PromptEntity;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 提示词模板领域模型与实体转换器
 *
 * @author SaltyFish
 * @since 1.0.0
 */
public class PromptConverter {

    /**
     * 实体转领域模型
     *
     * @param entity 实体
     * @return 领域模型
     */
    public static Prompt toDomain(PromptEntity entity) {
        if (entity == null) {
            return null;
        }

        // 构建值对象
        PromptId promptId = new PromptId(entity.getId());
        PromptName promptName = new PromptName(entity.getPromptName());
        PromptType promptType = PromptType.fromCode(entity.getPromptType());
        PromptRole promptRole = PromptRole.fromCode(entity.getPromptRole());
        PromptContent promptContent = new PromptContent(entity.getPromptContent());
        ResponseFormat responseFormat = entity.getResponseFormat() != null ?
            ResponseFormat.of(entity.getResponseFormat()) : null;

        AuditInfo auditInfo = new AuditInfo(
            entity.getCreatedBy(),
            entity.getCreatedTime(),
            entity.getUpdatedTime(),
            entity.getUpdatedBy(),
            entity.getObjectVersionNumber()
        );

        // 使用重建构造函数
        return new Prompt(
            promptId,
            promptName,
            promptType,
            promptRole,
            promptContent,
            entity.getEnabled(),
            responseFormat,
            entity.getRemark(),
            auditInfo
        );
    }

    /**
     * 领域模型转实体
     *
     * @param prompt 领域模型
     * @return 实体
     */
    public static PromptEntity toEntity(Prompt prompt) {
        if (prompt == null) {
            return null;
        }

        PromptEntity entity = new PromptEntity();

        // 基本信息
        if (prompt.getId() != null) {
            entity.setId(prompt.getId().getValue());
        }
        entity.setPromptName(prompt.getPromptName().getValue());
        entity.setPromptType(prompt.getPromptType().getCode());
        entity.setPromptRole(prompt.getPromptRole().getCode());

        // 内容信息
        if (prompt.getPromptContent() != null) {
            entity.setPromptContent(prompt.getPromptContent().getValue());
        }

        entity.setEnabled(prompt.getEnabled());

        if (prompt.getResponseFormat() != null) {
            entity.setResponseFormat(prompt.getResponseFormat().getValue());
        }

        entity.setRemark(prompt.getRemark());

        // 审计信息
        if (prompt.getAuditInfo() != null) {
            entity.setCreatedBy(prompt.getAuditInfo().getCreatedBy());
            entity.setUpdatedBy(prompt.getAuditInfo().getUpdatedBy());
            entity.setCreatedTime(prompt.getAuditInfo().getCreatedTime());
            entity.setUpdatedTime(prompt.getAuditInfo().getUpdatedTime());
            entity.setObjectVersionNumber(prompt.getAuditInfo().getObjectVersionNumber());
        }

        return entity;
    }

    /**
     * 批量转换实体列表为领域模型列表
     *
     * @param entities 实体列表
     * @return 领域模型列表
     */
    public static List<Prompt> toDomainList(List<PromptEntity> entities) {
        if (entities == null || entities.isEmpty()) {
            return List.of();
        }
        return entities.stream()
                .map(PromptConverter::toDomain)
                .collect(Collectors.toList());
    }

    /**
     * 批量转换领域模型列表为实体列表
     *
     * @param prompts 领域模型列表
     * @return 实体列表
     */
    public static List<PromptEntity> toEntityList(List<Prompt> prompts) {
        if (prompts == null || prompts.isEmpty()) {
            return List.of();
        }
        return prompts.stream()
                .map(PromptConverter::toEntity)
                .collect(Collectors.toList());
    }
}