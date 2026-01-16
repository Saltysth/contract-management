package com.contract.management.application.service;

import com.contract.management.application.dto.*;
import com.contract.management.domain.model.Prompt;
import com.contract.management.domain.model.PromptId;
import com.contract.management.domain.model.valueobject.*;
import com.contract.management.domain.repository.PromptFilters;
import com.contract.management.domain.service.PromptDomainService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * 提示词模板应用服务
 * 负责提示词模板的业务流程编排和DTO转换
 *
 * @author SaltyFish
 * @since 1.0.0
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class PromptApplicationService {

    private final PromptDomainService promptDomainService;

    /**
     * 创建提示词模板
     *
     * @param request 创建请求
     * @return 创建的提示词模板DTO
     */
    @Transactional
    public PromptDTO createPrompt(CreatePromptRequest request) {
        log.info("创建提示词模板: {}", request.getPromptName());

        // 创建新的提示词模板ID（临时使用null，由数据库生成）
        PromptId promptId = null;

        // 构建领域模型
        PromptName promptName = PromptName.of(request.getPromptName());
        PromptType promptType = request.getPromptType();
        PromptRole promptRole = request.getPromptRole();
        PromptContent promptContent = PromptContent.of(request.getPromptContent());
        Boolean enabled = request.getEnabled();
        ResponseFormat responseFormat = request.getResponseFormat() != null ?
            ResponseFormat.of(request.getResponseFormat()) : null;

        // 使用默认创建人ID（实际应该从上下文中获取）
        Long createdBy = 1L;

        Prompt prompt = new Prompt(
            promptId,
            promptName,
            promptType,
            promptRole,
            promptContent,
            enabled,
            responseFormat,
            request.getRemark(),
            createdBy
        );

        // 通过领域服务创建
        Prompt createdPrompt = promptDomainService.createPrompt(prompt);

        // 转换为DTO
        PromptDTO result = convertToDTO(createdPrompt);
        log.info("提示词模板创建成功，ID: {}", result.getId());
        return result;
    }

    /**
     * 更新提示词模板
     *
     * @param id      提示词模板ID
     * @param request 更新请求
     * @return 更新后的提示词模板DTO
     */
    @Transactional
    public PromptDTO updatePrompt(Long id, UpdatePromptRequest request) {
        log.info("更新提示词模板: {}", id);

        // 加载现有的提示词模板
        Prompt existingPrompt = promptDomainService.findById(PromptId.of(id))
            .orElseThrow(() -> new IllegalArgumentException("提示词模板不存在: " + id));

        // 更新基本信息
        if (request.getPromptName() != null) {
            existingPrompt.updateBasicInfo(
                PromptName.of(request.getPromptName()),
                PromptContent.of(request.getPromptContent()),
                request.getResponseFormat() != null ? ResponseFormat.of(request.getResponseFormat()) : null,
                request.getRemark()
            );
        }

        // 更新启用状态（如果提供了）
        if (request.getEnabled() != null && !request.getEnabled().equals(existingPrompt.isEnabled())) {
            if (request.getEnabled()) {
                existingPrompt.enable();
            } else {
                existingPrompt.disable();
            }
        }

        // 通过领域服务更新
        Prompt updatedPrompt = promptDomainService.updatePrompt(existingPrompt);

        // 转换为DTO
        PromptDTO result = convertToDTO(updatedPrompt);
        log.info("提示词模板更新成功，ID: {}", result.getId());
        return result;
    }

    /**
     * 根据ID获取提示词模板
     *
     * @param id 提示词模板ID
     * @return 提示词模板DTO
     */
    public PromptDTO getPromptById(Long id) {
        log.info("查询提示词模板: {}", id);

        Prompt prompt = promptDomainService.findById(PromptId.of(id))
            .orElseThrow(() -> new IllegalArgumentException("提示词模板不存在: " + id));

        return convertToDTO(prompt);
    }

    /**
     * 删除提示词模板
     *
     * @param id 提示词模板ID
     * @return 是否删除成功
     */
    @Transactional
    public boolean deletePrompt(Long id) {
        log.info("删除提示词模板: {}", id);

        try {
            promptDomainService.deletePrompt(PromptId.of(id));
            return true;
        } catch (Exception e) {
            log.error("删除提示词模板失败: {}", id, e);
            return false;
        }
    }

    /**
     * 启用提示词模板
     *
     * @param id 提示词模板ID
     * @return 启用后的提示词模板DTO
     */
    @Transactional
    public PromptDTO enablePrompt(Long id) {
        log.info("启用提示词模板: {}", id);

        Prompt prompt = promptDomainService.enablePrompt(PromptId.of(id));
        return convertToDTO(prompt);
    }

    /**
     * 禁用提示词模板
     *
     * @param id 提示词模板ID
     * @return 禁用后的提示词模板DTO
     */
    @Transactional
    public PromptDTO disablePrompt(Long id) {
        log.info("禁用提示词模板: {}", id);

        Prompt prompt = promptDomainService.disablePrompt(PromptId.of(id));
        return convertToDTO(prompt);
    }

    /**
     * 分页查询提示词模板
     *
     * @param queryDTO 查询条件
     * @param pageNum  页码
     * @param pageSize 每页大小
     * @return 分页结果
     */
    public Page<PromptDTO> findPrompts(PromptQueryDTO queryDTO, int pageNum, int pageSize) {
        log.info("分页查询提示词模板，条件: {}", queryDTO);

        // 构建分页参数
        Pageable pageable = PageRequest.of(pageNum - 1, pageSize, Sort.by(Sort.Direction.DESC, "createdTime"));

        // 构建查询过滤器
        PromptFilters filters = buildFilters(queryDTO);

        // 执行查询
        Page<Prompt> promptPage = promptDomainService.findByFilters(filters, pageable);

        // 转换为DTO
        List<PromptDTO> promptDTOs = promptPage.getContent().stream()
            .map(this::convertToDTO)
            .collect(Collectors.toList());

        return new org.springframework.data.domain.PageImpl<>(promptDTOs, pageable, promptPage.getTotalElements());
    }

    /**
     * 查询提示词模板列表
     *
     * @param queryDTO 查询条件
     * @return 提示词模板列表
     */
    public List<PromptDTO> queryPrompts(PromptQueryDTO queryDTO) {
        log.info("查询提示词模板列表，条件: {}", queryDTO);

        // 构建查询过滤器
        PromptFilters filters = buildFilters(queryDTO);

        // 执行查询
        List<Prompt> prompts = promptDomainService.findByFilters(filters);

        // 转换为DTO
        return prompts.stream()
            .map(this::convertToDTO)
            .collect(Collectors.toList());
    }

    /**
     * 根据类型查询启用的提示词模板
     *
     * @param type 提示词模板类型
     * @return 提示词模板列表
     */
    public List<PromptDTO> findEnabledPromptsByType(PromptType type) {
        log.info("查询启用的提示词模板，类型: {}", type);

        List<Prompt> prompts = promptDomainService.findEnabledPromptsByType(type);

        return prompts.stream()
            .map(this::convertToDTO)
            .collect(Collectors.toList());
    }

    /**
     * 查询所有启用的提示词模板
     *
     * @return 提示词模板列表
     */
    public List<PromptDTO> findAllEnabledPrompts() {
        log.info("查询所有启用的提示词模板");

        List<Prompt> prompts = promptDomainService.findAllEnabledPrompts();

        return prompts.stream()
            .map(this::convertToDTO)
            .collect(Collectors.toList());
    }

    /**
     * 构建查询过滤器
     *
     * @param queryDTO 查询DTO
     * @return 过滤器
     */
    private PromptFilters buildFilters(PromptQueryDTO queryDTO) {
        return PromptFilters.builder()
            .promptName(queryDTO.getPromptName())
            .promptType(queryDTO.getPromptTypes() != null && !queryDTO.getPromptTypes().isEmpty() ?
                queryDTO.getPromptTypes().get(0) : null)
            .promptRole(queryDTO.getPromptRoles() != null && !queryDTO.getPromptRoles().isEmpty() ?
                queryDTO.getPromptRoles().get(0) : null)
            .enabled(queryDTO.getEnabled())
            .responseFormat(queryDTO.getResponseFormat())
            .createdBy(queryDTO.getCreatedBy())
            .createdTimeStart(queryDTO.getCreatedTimeStart())
            .createdTimeEnd(queryDTO.getCreatedTimeEnd())
            .updatedTimeStart(queryDTO.getUpdatedTimeStart())
            .updatedTimeEnd(queryDTO.getUpdatedTimeEnd())
            .remark(queryDTO.getRemark())
            .keyword(queryDTO.getKeyword())
            .build();
    }

    /**
     * 领域模型转DTO
     *
     * @param prompt 领域模型
     * @return DTO
     */
    private PromptDTO convertToDTO(Prompt prompt) {
        PromptDTO dto = new PromptDTO();

        if (prompt.getId() != null) {
            dto.setId(prompt.getId().getValue());
        }

        dto.setPromptName(prompt.getPromptName().getValue());
        dto.setPromptType(prompt.getPromptType());
        dto.setPromptRole(prompt.getPromptRole());

        if (prompt.getPromptContent() != null) {
            dto.setPromptContent(prompt.getPromptContent().getValue());
        }

        dto.setEnabled(prompt.getEnabled());

        if (prompt.getResponseFormat() != null) {
            dto.setResponseFormat(prompt.getResponseFormat().getValue());
        }

        dto.setRemark(prompt.getRemark());

        if (prompt.getAuditInfo() != null) {
            dto.setCreatedBy(prompt.getAuditInfo().getCreatedBy());
            dto.setUpdatedBy(prompt.getAuditInfo().getUpdatedBy());
            dto.setCreatedTime(prompt.getAuditInfo().getCreatedTime());
            dto.setUpdatedTime(prompt.getAuditInfo().getUpdatedTime());
            dto.setObjectVersionNumber(prompt.getAuditInfo().getObjectVersionNumber());
        }

        return dto;
    }
}