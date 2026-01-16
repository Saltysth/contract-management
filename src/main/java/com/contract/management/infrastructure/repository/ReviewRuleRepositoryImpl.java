package com.contract.management.infrastructure.repository;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.contract.management.domain.model.ReviewRule;
import com.contract.management.domain.model.valueobject.PromptMode;
import com.contract.management.domain.model.valueobject.ReviewRuleId;
import com.contract.management.domain.model.valueobject.RuleType;
import com.contract.management.domain.repository.ReviewRuleRepository;
import com.contract.management.infrastructure.converter.ReviewRuleConverter;
import com.contract.management.infrastructure.entity.ReviewRuleEntity;
import com.contract.management.infrastructure.mapper.ReviewRuleMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

/**
 * 审查规则仓储实现
 * 支持复杂查询和分页
 *
 * @author SaltyFish
 * @since 1.0.0
 */
@Slf4j
@Repository
@RequiredArgsConstructor
public class ReviewRuleRepositoryImpl implements ReviewRuleRepository {

    private final ReviewRuleMapper reviewRuleMapper;

    @Override
    @Transactional
    public ReviewRule save(ReviewRule reviewRule) {
        ReviewRuleEntity entity = ReviewRuleConverter.toEntity(reviewRule);

        if (entity.getId() == null) {
            // 新增
            reviewRuleMapper.insert(entity);
            log.debug("Created new review rule: {}", entity.getId());
        } else {
            // 更新
            reviewRuleMapper.updateById(entity);
            log.debug("Updated review rule: {}", entity.getId());
        }

        return ReviewRuleConverter.toDomain(entity);
    }

    @Override
    public Optional<ReviewRule> findById(ReviewRuleId id) {
        if (id == null) {
            return Optional.empty();
        }

        ReviewRuleEntity entity = reviewRuleMapper.selectById(id.getValue());
        return Optional.ofNullable(ReviewRuleConverter.toDomain(entity));
    }

    @Override
    public Optional<ReviewRule> findByRuleName(String ruleName) {
        if (ruleName == null || ruleName.trim().isEmpty()) {
            return Optional.empty();
        }

        ReviewRuleEntity entity = reviewRuleMapper.findByRuleName(ruleName);
        return Optional.ofNullable(ReviewRuleConverter.toDomain(entity));
    }

    @Override
    public List<ReviewRule> findAll() {
        QueryWrapper<ReviewRuleEntity> queryWrapper = new QueryWrapper<>();
        queryWrapper.orderByDesc("create_time");

        List<ReviewRuleEntity> entities = reviewRuleMapper.selectList(queryWrapper);
        return ReviewRuleConverter.toDomainList(entities);
    }

    @Override
    public List<ReviewRule> findByEnabled(Boolean enabled) {
        List<ReviewRuleEntity> entities = reviewRuleMapper.findByEnabled(enabled);
        return ReviewRuleConverter.toDomainList(entities);
    }

    @Override
    public List<ReviewRule> findByRuleType(RuleType ruleType) {
        if (ruleType == null) {
            return List.of();
        }

        List<ReviewRuleEntity> entities = reviewRuleMapper.findByRuleType(ruleType.getCode());
        return ReviewRuleConverter.toDomainList(entities);
    }

    @Override
    public List<ReviewRule> findByPromptMode(PromptMode promptMode) {
        if (promptMode == null) {
            return List.of();
        }

        List<ReviewRuleEntity> entities = reviewRuleMapper.findByPromptMode(promptMode.getCode());
        return ReviewRuleConverter.toDomainList(entities);
    }

    @Override
    public List<ReviewRule> findByContractType(String contractType) {
        if (contractType == null || contractType.trim().isEmpty()) {
            return List.of();
        }

        List<ReviewRuleEntity> entities = reviewRuleMapper.findByContractType(contractType);
        return ReviewRuleConverter.toDomainList(entities);
    }

    @Override
    public List<ReviewRule> findByContractTypeAndClauseType(String contractType, String clauseType) {
        if (contractType == null || contractType.trim().isEmpty() ||
            clauseType == null || clauseType.trim().isEmpty()) {
            return List.of();
        }

        List<ReviewRuleEntity> entities = reviewRuleMapper.findByContractTypeAndClauseType(
                contractType, clauseType);
        return ReviewRuleConverter.toDomainList(entities);
    }

    @Override
    public List<ReviewRule> findApplicableRules(String contractType, String clauseType, PromptMode promptMode) {
        if (contractType == null || contractType.trim().isEmpty() ||
            clauseType == null || clauseType.trim().isEmpty() ||
            promptMode == null) {
            return List.of();
        }

        List<ReviewRuleEntity> entities = reviewRuleMapper.findApplicableRules(
                contractType, clauseType, promptMode.getCode());
        return ReviewRuleConverter.toDomainList(entities);
    }

    @Override
    public List<ReviewRule> findEffectiveRulesByMode(PromptMode promptMode) {
        if (promptMode == null) {
            return List.of();
        }

        List<ReviewRuleEntity> entities = reviewRuleMapper.findEffectiveRulesByMode(promptMode.getCode());
        return ReviewRuleConverter.toDomainList(entities);
    }

    @Override
    @Transactional
    public void deleteById(ReviewRuleId id) {
        if (id != null) {
            reviewRuleMapper.deleteById(id.getValue());
            log.debug("Deleted review rule: {}", id.getValue());
        }
    }

    @Override
    public boolean existsByRuleName(String ruleName, Long excludeId) {
        if (ruleName == null || ruleName.trim().isEmpty()) {
            return false;
        }

        return reviewRuleMapper.existsByRuleName(ruleName, excludeId);
    }

    @Override
    public long countByEnabled(Boolean enabled) {
        return reviewRuleMapper.countByEnabled(enabled);
    }

    /**
     * 分页查询规则（支持复杂条件）
     * 参考 ClauseRepositoryImpl 的实现模式
     */
    public org.springframework.data.domain.Page<ReviewRule> findByConditions(String ruleName, String ruleType, Boolean enabled,
                                                                           Short promptModeCode, String contractType, String clauseType,
                                                                           Pageable pageable) {
        com.baomidou.mybatisplus.extension.plugins.pagination.Page<ReviewRuleEntity> page =
            new com.baomidou.mybatisplus.extension.plugins.pagination.Page<>(pageable.getPageNumber() + 1, pageable.getPageSize());

        IPage<ReviewRuleEntity> result = reviewRuleMapper.selectByConditions(
                page, ruleName, ruleType, enabled, promptModeCode, contractType, clauseType);

        List<ReviewRule> rules = ReviewRuleConverter.toDomainList(result.getRecords());
        return new PageImpl<>(rules, pageable, result.getTotal());
    }

    @Override
    public List<ReviewRule> findByCategories(List<String> categories) {
        if (categories == null || categories.isEmpty()) {
            return List.of();
        }

        List<ReviewRuleEntity> entities = reviewRuleMapper.findByCategories(categories);
        return ReviewRuleConverter.toDomainList(entities);
    }
}