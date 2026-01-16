package com.contract.management.domain.service;

import com.contract.management.domain.model.Prompt;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * 提示词模板验证服务
 *
 * @author SaltyFish
 * @since 1.0.0
 */
@Slf4j
@Service
public class PromptValidationService {

    /**
     * 验证创建时的业务规则
     *
     * @param prompt 提示词模板
     * @throws PromptDomainException 验证失败时抛出
     */
    public void validatePromptForCreation(Prompt prompt) {
        if (prompt == null) {
            throw new PromptDomainException("提示词模板不能为空");
        }

        // 基本验证已在聚合根中完成
        log.debug("提示词模板创建验证通过: {}", prompt.getId());
    }

    /**
     * 验证更新时的业务规则
     *
     * @param prompt 提示词模板
     * @throws PromptDomainException 验证失败时抛出
     */
    public void validatePromptForUpdate(Prompt prompt) {
        if (prompt == null) {
            throw new PromptDomainException("提示词模板不能为空");
        }

        if (prompt.getId() == null) {
            throw new PromptDomainException("更新提示词模板时ID不能为空");
        }

        // 基本验证已在聚合根中完成
        log.debug("提示词模板更新验证通过: {}", prompt.getId());
    }

    /**
     * 验证删除时的业务规则
     *
     * @param prompt 提示词模板
     * @throws PromptDomainException 验证失败时抛出
     */
    public void validatePromptForDeletion(Prompt prompt) {
        if (prompt == null) {
            throw new PromptDomainException("提示词模板不能为空");
        }

        // 检查是否为系统内置模板
        if (!prompt.canDelete()) {
            throw new PromptDomainException("系统内置模板不允许删除，只能禁用");
        }

        log.debug("提示词模板删除验证通过: {}", prompt.getId());
    }

    /**
     * 验证启用时的业务规则
     *
     * @param prompt 提示词模板
     * @throws PromptDomainException 验证失败时抛出
     */
    public void validatePromptForEnable(Prompt prompt) {
        if (prompt == null) {
            throw new PromptDomainException("提示词模板不能为空");
        }

        // 检查内容是否为空
        if (prompt.getPromptContent() == null || prompt.getPromptContent().isEmpty()) {
            throw new PromptDomainException("提示词内容为空，无法启用");
        }

        log.debug("提示词模板启用验证通过: {}", prompt.getId());
    }

    /**
     * 验证禁用时的业务规则
     *
     * @param prompt 提示词模板
     * @throws PromptDomainException 验证失败时抛出
     */
    public void validatePromptForDisable(Prompt prompt) {
        if (prompt == null) {
            throw new PromptDomainException("提示词模板不能为空");
        }

        log.debug("提示词模板禁用验证通过: {}", prompt.getId());
    }
}