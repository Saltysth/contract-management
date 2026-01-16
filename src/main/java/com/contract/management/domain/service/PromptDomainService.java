package com.contract.management.domain.service;

import com.contract.management.domain.model.Prompt;
import com.contract.management.domain.model.PromptId;
import com.contract.management.domain.model.valueobject.PromptName;
import com.contract.management.domain.model.valueobject.PromptType;
import com.contract.management.domain.repository.PromptFilters;
import com.contract.management.domain.repository.PromptRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

/**
 * 提示词模板领域服务
 *
 * @author SaltyFish
 * @since 1.0.0
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class PromptDomainService {

    private final PromptRepository promptRepository;
    private final PromptValidationService promptValidationService;

    /**
     * 验证提示词模板名称唯一性
     *
     * @param promptName 提示词模板名称
     * @param type       提示词模板类型
     * @return 验证后的名称（如果重复会添加时间戳）
     */
    public String getValidatedPromptNameUniqueness(String promptName, PromptType type) {
        if (promptRepository.existsByNameAndType(promptName, type)) {
            log.warn("提示词模板名称已存在: type={}, name={}", type, promptName);
            return promptName + "_" + String.valueOf(System.currentTimeMillis()).substring(0, 4);
        }
        return promptName;
    }

    /**
     * 验证提示词模板名称唯一性（排除指定模板）
     *
     * @param promptName  提示词模板名称
     * @param type        提示词模板类型
     * @param excludeId   排除的模板ID
     * @throws PromptDomainException 如果名称已存在
     */
    public void validatePromptNameUniqueness(String promptName, PromptType type, PromptId excludeId) {
        if (promptRepository.existsByNameAndTypeExcludeId(promptName, type, excludeId)) {
            log.warn("提示词模板名称可能重复，需要进一步验证: promptName={}, type={}, excludeId={}",
                promptName, type, excludeId.getValue());
            throw new PromptDomainException("提示词模板名称已存在");
        }
    }

    // ========== 领域服务对外编排能力（封装仓储访问） ==========

    /**
     * 根据ID查找提示词模板
     */
    public Optional<Prompt> findById(PromptId id) {
        return promptRepository.findById(id);
    }

    /**
     * 检查提示词模板是否存在
     */
    public boolean existsById(PromptId id) {
        return promptRepository.existsById(id);
    }

    /**
     * 创建提示词模板：负责创建前的领域校验并持久化
     */
    @Transactional
    public Prompt createPrompt(Prompt prompt) {
        // 验证名称唯一性
        String validatedName = getValidatedPromptNameUniqueness(
            prompt.getPromptName().getValue(),
            prompt.getPromptType()
        );

        // 更新名称
        prompt.updateBasicInfo(
            PromptName.of(validatedName),
            prompt.getPromptContent(),
            prompt.getResponseFormat(),
            prompt.getRemark()
        );

        // 执行创建验证
        promptValidationService.validatePromptForCreation(prompt);

        return promptRepository.save(prompt);
    }

    /**
     * 更新提示词模板：负责加载旧聚合、编辑性校验、名称唯一性（排除自身）并持久化
     */
    @Transactional
    public Prompt updatePrompt(Prompt prompt) {
        PromptId id = prompt.getId();
        if (id == null) {
            throw new IllegalArgumentException("更新提示词模板时ID不能为空");
        }

        // 加载现有模板
        Prompt existingPrompt = promptRepository.findById(id)
            .orElseThrow(() -> new PromptDomainException("提示词模板不存在: " + id.getValue()));

        // 验证名称唯一性（排除自身）
        validatePromptNameUniqueness(
            prompt.getPromptName().getValue(),
            prompt.getPromptType(),
            id
        );

        // 执行更新验证
        promptValidationService.validatePromptForUpdate(prompt);

        return promptRepository.update(prompt);
    }

    /**
     * 删除提示词模板：负责删除规则校验并持久化
     */
    @Transactional
    public void deletePrompt(PromptId id) {
        Prompt prompt = promptRepository.findById(id)
            .orElseThrow(() -> new PromptDomainException("提示词模板不存在: " + id.getValue()));

        // 检查是否可以删除
        if (!prompt.canDelete()) {
            throw new PromptDomainException("系统内置模板不允许删除，只能禁用");
        }

        promptValidationService.validatePromptForDeletion(prompt);
        promptRepository.delete(id);
    }

    /**
     * 启用提示词模板
     */
    @Transactional
    public Prompt enablePrompt(PromptId id) {
        Prompt prompt = promptRepository.findById(id)
            .orElseThrow(() -> new PromptDomainException("提示词模板不存在: " + id.getValue()));

        prompt.enable();
        return promptRepository.update(prompt);
    }

    /**
     * 禁用提示词模板
     */
    @Transactional
    public Prompt disablePrompt(PromptId id) {
        Prompt prompt = promptRepository.findById(id)
            .orElseThrow(() -> new PromptDomainException("提示词模板不存在: " + id.getValue()));

        prompt.disable();
        return promptRepository.update(prompt);
    }

    /**
     * 批量启用提示词模板
     */
    @Transactional
    public void enablePrompts(List<PromptId> ids) {
        promptRepository.enableAll(ids);
    }

    /**
     * 批量禁用提示词模板
     */
    @Transactional
    public void disablePrompts(List<PromptId> ids) {
        promptRepository.disableAll(ids);
    }

    /**
     * 根据过滤条件分页查询提示词模板
     */
    public org.springframework.data.domain.Page<Prompt> findByFilters(
            PromptFilters filters,
            org.springframework.data.domain.Pageable pageable) {
        return promptRepository.findByFilters(filters, pageable);
    }

    /**
     * 根据过滤条件查询提示词模板列表
     */
    public List<Prompt> findByFilters(PromptFilters filters) {
        return promptRepository.findByFilters(filters);
    }

    /**
     * 根据类型查找启用的提示词模板
     */
    public List<Prompt> findEnabledPromptsByType(PromptType type) {
        return promptRepository.findEnabledByType(type);
    }

    /**
     * 查找所有启用的提示词模板
     */
    public List<Prompt> findAllEnabledPrompts() {
        return promptRepository.findEnabledPrompts();
    }
}