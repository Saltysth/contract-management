package com.contract.management.infrastructure.repository;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.contract.management.domain.model.Prompt;
import com.contract.management.domain.model.PromptId;
import com.contract.management.domain.model.valueobject.PromptRole;
import com.contract.management.domain.model.valueobject.PromptType;
import com.contract.management.domain.repository.PromptFilters;
import com.contract.management.domain.repository.PromptRepository;
import com.contract.management.infrastructure.converter.PromptConverter;
import com.contract.management.infrastructure.entity.PromptEntity;
import com.contract.management.infrastructure.mapper.PromptMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 提示词模板仓储实现类 - MyBatis Plus版本
 *
 * @author SaltyFish
 * @since 1.0.0
 */
@Slf4j
@Repository
@RequiredArgsConstructor
public class PromptRepositoryImpl implements PromptRepository {

    private final PromptMapper promptMapper;

    @Override
    @Transactional
    public Prompt save(Prompt prompt) {
        PromptEntity entity = PromptConverter.toEntity(prompt);
        promptMapper.insert(entity);
        return PromptConverter.toDomain(entity);
    }

    @Override
    @Transactional
    public Prompt update(Prompt prompt) {
        // 先查询现有实体，保留版本号
        PromptEntity existingEntity = promptMapper.selectById(prompt.getId().getValue());
        if (existingEntity == null) {
            throw new RuntimeException("提示词不存在");
        }

        // 转换为实体并保留版本号
        PromptEntity entity = PromptConverter.toEntity(prompt);
        entity.setObjectVersionNumber(existingEntity.getObjectVersionNumber());

        promptMapper.updateById(entity);
        return PromptConverter.toDomain(entity);
    }

    @Override
    @Transactional
    public void delete(PromptId id) {
        promptMapper.deleteById(id.getValue());
    }

    @Override
    public Optional<Prompt> findById(PromptId id) {
        PromptEntity entity = promptMapper.findById(id.getValue());
        return Optional.ofNullable(PromptConverter.toDomain(entity));
    }

    @Override
    public boolean existsById(PromptId id) {
        return promptMapper.selectById(id.getValue()) != null;
    }

    @Override
    public Prompt findOneByFilters(PromptFilters filters) {
        List<Prompt> prompts = findByFilters(filters);
        if (!prompts.isEmpty()) {
            return prompts.get(0);
        } else {
            log.warn("未找到匹配的提示词模板");
            return null;
        }
    }

    @Override
    public org.springframework.data.domain.Page<Prompt> findByFilters(PromptFilters filters, Pageable pageable) {
        // 构建查询条件
        String promptType = filters != null && filters.getPromptType() != null ?
            filters.getPromptType().getCode() : null;
        String promptRole = filters != null && filters.getPromptRole() != null ?
            filters.getPromptRole().getCode() : null;

        // 创建MyBatis Plus分页对象
        Page<PromptEntity> page = new Page<>(pageable.getPageNumber() + 1, pageable.getPageSize());

        // 执行分页查询
        var result = promptMapper.selectByFilters(
            page,
            promptType,
            promptRole,
            filters != null ? filters.getEnabled() : null,
            filters != null ? filters.getResponseFormat() : null,
            filters != null ? filters.getCreatedBy() : null,
            filters != null ? filters.getPromptName() : null,
            filters != null ? filters.getCreatedTimeStart() : null,
            filters != null ? filters.getCreatedTimeEnd() : null,
            filters != null ? filters.getUpdatedTimeStart() : null,
            filters != null ? filters.getUpdatedTimeEnd() : null
        );

        // 转换为Spring Data分页对象
        List<Prompt> prompts = PromptConverter.toDomainList(result.getRecords());
        return new PageImpl<>(prompts, pageable, result.getTotal());
    }

    @Override
    public List<Prompt> findByFilters(PromptFilters filters) {
        // 构建查询条件
        String promptType = filters != null && filters.getPromptType() != null ?
            filters.getPromptType().getCode() : null;
        String promptRole = filters != null && filters.getPromptRole() != null ?
            filters.getPromptRole().getCode() : null;

        // 创建分页对象（设置大页面以获取所有数据）
        Page<PromptEntity> page = new Page<>(1, Integer.MAX_VALUE);

        // 执行查询
        var result = promptMapper.selectByFilters(
            page,
            promptType,
            promptRole,
            filters != null ? filters.getEnabled() : null,
            filters != null ? filters.getResponseFormat() : null,
            filters != null ? filters.getCreatedBy() : null,
            filters != null ? filters.getPromptName() : null,
            filters != null ? filters.getCreatedTimeStart() : null,
            filters != null ? filters.getCreatedTimeEnd() : null,
            filters != null ? filters.getUpdatedTimeStart() : null,
            filters != null ? filters.getUpdatedTimeEnd() : null
        );

        return PromptConverter.toDomainList(result.getRecords());
    }

    @Override
    public List<Prompt> findByType(PromptType type) {
        List<PromptEntity> entities = promptMapper.findByType(type.getCode());
        return PromptConverter.toDomainList(entities);
    }

    @Override
    public List<Prompt> findEnabledByType(PromptType type) {
        List<PromptEntity> entities = promptMapper.findEnabledByType(type.getCode());
        return PromptConverter.toDomainList(entities);
    }

    @Override
    public List<Prompt> findByRole(PromptRole role) {
        List<PromptEntity> entities = promptMapper.findByRole(role.getCode());
        return PromptConverter.toDomainList(entities);
    }

    @Override
    public List<Prompt> findByCreatedBy(Long userId) {
        List<PromptEntity> entities = promptMapper.findByCreatedBy(userId);
        return PromptConverter.toDomainList(entities);
    }

    @Override
    public List<Prompt> findEnabledPrompts() {
        List<PromptEntity> entities = promptMapper.findEnabledPrompts();
        return PromptConverter.toDomainList(entities);
    }

    @Override
    public List<Prompt> findDisabledPrompts() {
        List<PromptEntity> entities = promptMapper.findDisabledPrompts();
        return PromptConverter.toDomainList(entities);
    }

    @Override
    public long countByType(PromptType type) {
        return promptMapper.countByType(type.getCode());
    }

    @Override
    public long countByRole(PromptRole role) {
        return promptMapper.countByRole(role.getCode());
    }

    @Override
    public long countByCreatedBy(Long userId) {
        return promptMapper.countByCreatedBy(userId);
    }

    @Override
    public Map<PromptType, Long> getTypeStatistics() {
        var statistics = promptMapper.getTypeStatistics();
        return statistics.stream()
            .collect(Collectors.toMap(
                stat -> PromptType.fromCode((String) stat.get("type")),
                stat -> (Long) stat.get("count")
            ));
    }

    @Override
    public Map<PromptRole, Long> getRoleStatistics() {
        var statistics = promptMapper.getRoleStatistics();
        return statistics.stream()
            .collect(Collectors.toMap(
                stat -> PromptRole.fromCode((String) stat.get("role")),
                stat -> (Long) stat.get("count")
            ));
    }

    @Override
    public boolean existsByNameAndType(String promptName, PromptType type) {
        return promptMapper.countByNameAndType(promptName, type.getCode()) > 0;
    }

    @Override
    public boolean existsByNameAndTypeExcludeId(String promptName, PromptType type, PromptId excludeId) {
        return promptMapper.countByNameAndTypeExcludeId(promptName, type.getCode(), excludeId.getValue()) > 0;
    }

    @Override
    @Transactional
    public List<Prompt> saveAll(List<Prompt> prompts) {
        if (prompts == null || prompts.isEmpty()) {
            return List.of();
        }

        List<PromptEntity> entities = PromptConverter.toEntityList(prompts);
        // 批量插入
        // 注意：MyBatis Plus的saveBatch方法需要IService，这里使用循环插入
        for (PromptEntity entity : entities) {
            promptMapper.insert(entity);
        }

        return PromptConverter.toDomainList(entities);
    }

    @Override
    @Transactional
    public void deleteAll(List<PromptId> promptIds) {
        if (promptIds == null || promptIds.isEmpty()) {
            return;
        }

        List<Long> ids = promptIds.stream()
            .map(PromptId::getValue)
            .collect(Collectors.toList());

        for (Long id : ids) {
            promptMapper.deleteById(id);
        }
    }

    @Override
    @Transactional
    public void enableAll(List<PromptId> promptIds) {
        if (promptIds == null || promptIds.isEmpty()) {
            return;
        }

        List<Long> ids = promptIds.stream()
            .map(PromptId::getValue)
            .collect(Collectors.toList());

        promptMapper.batchEnable(ids, LocalDateTime.now());
    }

    @Override
    @Transactional
    public void disableAll(List<PromptId> promptIds) {
        if (promptIds == null || promptIds.isEmpty()) {
            return;
        }

        List<Long> ids = promptIds.stream()
            .map(PromptId::getValue)
            .collect(Collectors.toList());

        promptMapper.batchDisable(ids, LocalDateTime.now());
    }
}