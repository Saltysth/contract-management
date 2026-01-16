package com.contract.management.infrastructure.service;

import com.contract.management.domain.model.ReviewRule;
import com.contract.management.domain.model.valueobject.PromptMode;
import com.contract.management.domain.model.valueobject.ReviewRuleId;
import com.contract.management.domain.model.valueobject.RuleType;
import com.contract.management.domain.repository.ReviewRuleRepository;
import com.contract.management.domain.service.ReviewRuleDomainService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

/**
 * 审查规则领域服务实现
 *
 * @author SaltyFish
 * @since 1.0.0
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class ReviewRuleDomainServiceImpl implements ReviewRuleDomainService {

    private final ReviewRuleRepository reviewRuleRepository;

    @Override
    @Transactional
    public ReviewRule createRule(ReviewRule reviewRule) {
        if (reviewRule == null) {
            throw new IllegalArgumentException("Review rule cannot be null");
        }

        // 检查规则名称是否已存在
        if (reviewRuleRepository.existsByRuleName(reviewRule.getRuleName().getValue(), null)) {
            throw new IllegalArgumentException("Rule name already exists: " + reviewRule.getRuleName().getValue());
        }

        ReviewRule savedRule = reviewRuleRepository.save(reviewRule);
        log.info("Created review rule: {}", savedRule.getId().getValue());
        return savedRule;
    }

    @Override
    @Transactional
    public ReviewRule updateRule(ReviewRule reviewRule) {
        if (reviewRule == null || reviewRule.getId() == null) {
            throw new IllegalArgumentException("Review rule and ID cannot be null");
        }

        // 检查规则是否存在
        Optional<ReviewRule> existingRuleOpt = reviewRuleRepository.findById(reviewRule.getId());
        if (existingRuleOpt.isEmpty()) {
            throw new IllegalArgumentException("Review rule not found: " + reviewRule.getId().getValue());
        }

        // 检查规则名称是否与其他规则冲突
        if (reviewRuleRepository.existsByRuleName(reviewRule.getRuleName().getValue(), existingRuleOpt.get().getId().getValue())) {
            throw new IllegalArgumentException("Rule name already exists: " + reviewRule.getRuleName().getValue());
        }

        // 标记为已修改
        reviewRule.markAsModified();

        ReviewRule savedRule = reviewRuleRepository.save(reviewRule);
        log.info("Updated review rule: {}", savedRule.getId().getValue());
        return savedRule;
    }

    @Override
    @Transactional
    public void deleteRule(ReviewRuleId id) {
        if (id == null) {
            return;
        }

        // 检查规则是否存在
        Optional<ReviewRule> existingRuleOpt = reviewRuleRepository.findById(id);
        if (existingRuleOpt.isEmpty()) {
            log.warn("Attempted to delete non-existent review rule: {}", id.getValue());
            return;
        }

        reviewRuleRepository.deleteById(id);
        log.info("Deleted review rule: {}", id.getValue());
    }

    @Override
    @Transactional
    public Optional<ReviewRule> enableRule(ReviewRuleId id) {
        Optional<ReviewRule> ruleOpt = reviewRuleRepository.findById(id);
        if (ruleOpt.isPresent()) {
            ReviewRule rule = ruleOpt.get();
            rule.enable();
            ReviewRule savedRule = reviewRuleRepository.save(rule);
            log.info("Enabled review rule: {}", id.getValue());
            return Optional.of(savedRule);
        }
        return Optional.empty();
    }

    @Override
    @Transactional
    public Optional<ReviewRule> disableRule(ReviewRuleId id) {
        Optional<ReviewRule> ruleOpt = reviewRuleRepository.findById(id);
        if (ruleOpt.isPresent()) {
            ReviewRule rule = ruleOpt.get();
            rule.disable();
            ReviewRule savedRule = reviewRuleRepository.save(rule);
            log.info("Disabled review rule: {}", id.getValue());
            return Optional.of(savedRule);
        }
        return Optional.empty();
    }

    @Override
    public Optional<ReviewRule> findRuleById(ReviewRuleId id) {
        return reviewRuleRepository.findById(id);
    }

    @Override
    public Optional<ReviewRule> findRuleByRuleName(String ruleName) {
        return reviewRuleRepository.findByRuleName(ruleName);
    }

    @Override
    public List<ReviewRule> findAllRules() {
        return reviewRuleRepository.findAll();
    }

    @Override
    public List<ReviewRule> findRulesByType(RuleType ruleType) {
        return reviewRuleRepository.findByRuleType(ruleType);
    }

    @Override
    public List<ReviewRule> findRulesByEnabled(Boolean enabled) {
        return reviewRuleRepository.findByEnabled(enabled);
    }

    @Override
    public List<ReviewRule> findApplicableRules(String contractType, String clauseType) {
        return reviewRuleRepository.findByContractTypeAndClauseType(contractType, clauseType);
    }

    @Override
    public List<ReviewRule> findApplicableRules(String contractType, String clauseType, PromptMode promptMode) {
        return reviewRuleRepository.findApplicableRules(contractType, clauseType, promptMode);
    }

    @Override
    public List<ReviewRule> findEffectiveRulesByMode(PromptMode promptMode) {
        return reviewRuleRepository.findEffectiveRulesByMode(promptMode);
    }

    @Override
    public boolean isRuleNameAvailable(String ruleName) {
        return !reviewRuleRepository.existsByRuleName(ruleName, null);
    }

    @Override
    public long countRules(Boolean enabled) {
        return reviewRuleRepository.countByEnabled(enabled);
    }

    @Override
    public org.springframework.data.domain.Page<ReviewRule> findByConditions(String ruleName, String ruleType, Boolean enabled,
                                                                         Short promptModeCode, String contractType, String clauseType,
                                                                         org.springframework.data.domain.Pageable pageable) {
        // 委托给仓储实现
        return ((com.contract.management.infrastructure.repository.ReviewRuleRepositoryImpl) reviewRuleRepository)
                .findByConditions(ruleName, ruleType, enabled, promptModeCode, contractType, clauseType, pageable);
    }

    @Override
    @Transactional
    public void deleteRules(List<ReviewRuleId> ids) {
        if (ids == null || ids.isEmpty()) {
            return;
        }

        for (ReviewRuleId id : ids) {
            deleteRule(id);
        }
        log.info("Batch deleted {} review rules", ids.size());
    }

    @Override
    public List<ReviewRule> findRulesByCategories(List<String> categories) {
        if (categories == null || categories.isEmpty()) {
            return List.of();
        }

        log.debug("Finding review rules by categories: {}", categories);
        return reviewRuleRepository.findByCategories(categories);
    }
}