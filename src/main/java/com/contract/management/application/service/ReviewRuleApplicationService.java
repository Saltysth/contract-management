package com.contract.management.application.service;

import com.contract.management.application.dto.ReviewRuleDTO;
import com.contract.management.application.dto.CreateReviewRuleCommand;
import com.contract.management.application.dto.UpdateReviewRuleCommand;
import com.contract.management.application.dto.ReviewRuleQueryRequest;
import com.contract.management.application.convertor.ReviewRuleApplicationConvertor;
import com.contract.management.domain.model.ReviewRule;
import com.contract.management.domain.model.valueobject.*;
import com.contract.management.domain.service.ReviewRuleDomainService;
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

/**
 * 审查规则应用服务
 * 提供审查规则的CRUD操作和复杂查询功能
 *
 * @author SaltyFish
 * @since 1.0.0
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class ReviewRuleApplicationService {

    private final ReviewRuleDomainService reviewRuleDomainService;
    private final ReviewRuleApplicationConvertor reviewRuleApplicationConvertor;

    /**
     * 创建审查规则
     */
    @Transactional
    public ReviewRuleDTO createRule(CreateReviewRuleCommand command) {
        log.info("Creating review rule: {}", command.getRuleName());

        // 1. 转换命令为领域对象
        ReviewRule reviewRule = reviewRuleApplicationConvertor.toDomainForCreate(command);

        // 2. 委托给领域服务处理业务逻辑
        ReviewRule createdRule = reviewRuleDomainService.createRule(reviewRule);

        // 3. 转换领域对象为DTO
        ReviewRuleDTO result = reviewRuleApplicationConvertor.toDTO(createdRule);

        log.info("Successfully created review rule: {}", result.getId());
        return result;
    }

    /**
     * 更新审查规则
     */
    @Transactional
    public ReviewRuleDTO updateRule(Long id, UpdateReviewRuleCommand command) {
        log.info("Updating review rule: {}", id);

        // 1. 检查规则是否存在
        Optional<ReviewRule> existingRuleOpt = reviewRuleDomainService.findRuleById(ReviewRuleId.of(id));
        if (existingRuleOpt.isEmpty()) {
            throw new IllegalArgumentException("Review rule not found: " + id);
        }

        // 2. 获取现有规则并更新其属性
        ReviewRule existingRule = existingRuleOpt.get();

        // 3. 更新规则信息
        existingRule.updateRule(
            RuleName.of(command.getRuleName()),
            RuleType.fromCode(command.getRuleType()),
            ApplicableContractTypes.of(command.getApplicableContractType()),
            ApplicableClauseTypes.of(command.getApplicableClauseType()),
            RuleContent.of(command.getRuleContent()),
            PromptMode.fromCode(command.getPromptModeCode()),
            command.getRemark()
        );

        // 4. 委托给领域服务处理业务逻辑
        ReviewRule updatedRule = reviewRuleDomainService.updateRule(existingRule);

        // 5. 转换领域对象为DTO
        ReviewRuleDTO result = reviewRuleApplicationConvertor.toDTO(updatedRule);

        log.info("Successfully updated review rule: {}", result.getId());
        return result;
    }

    /**
     * 根据ID查询审查规则
     */
    public Optional<ReviewRuleDTO> getRuleById(Long id) {
        log.debug("Finding review rule by ID: {}", id);

        Optional<ReviewRule> ruleOpt = reviewRuleDomainService.findRuleById(ReviewRuleId.of(id));
        return ruleOpt.map(reviewRuleApplicationConvertor::toDTO);
    }

    /**
     * 分页查询审查规则（支持复杂条件和排序）
     * 参考 ClauseApplicationService.searchClauses 的实现模式
     */
    public Page<ReviewRuleDTO> searchRules(ReviewRuleQueryRequest queryRequest) {
        log.debug("Searching review rules with conditions: {}", queryRequest);

        // 1. 构建排序对象
        Sort sort = Sort.by(Sort.Direction.fromString(queryRequest.getDirection()), queryRequest.getSort());

        // 2. 构建分页对象
        Pageable pageable = PageRequest.of(queryRequest.getPage(), queryRequest.getSize(), sort);

        // 3. 委托给领域服务进行查询
        org.springframework.data.domain.Page<ReviewRule> reviewRulePage =
            reviewRuleDomainService.findByConditions(
                queryRequest.getRuleName(),
                queryRequest.getRuleType(),
                queryRequest.getEnabled(),
                queryRequest.getPromptModeCode(),
                queryRequest.getContractType(),
                queryRequest.getClauseType(),
                pageable
            );

        // 4. 转换为DTO
        List<ReviewRuleDTO> reviewRuleDTOs = reviewRuleApplicationConvertor.toDTOList(reviewRulePage.getContent());

        return new org.springframework.data.domain.PageImpl<>(
                reviewRuleDTOs,
                pageable,
                reviewRulePage.getTotalElements()
        );
    }

    /**
     * 删除审查规则
     */
    @Transactional
    public void deleteRule(Long id) {
        log.info("Deleting review rule: {}", id);

        reviewRuleDomainService.deleteRule(ReviewRuleId.of(id));

        log.info("Successfully deleted review rule: {}", id);
    }

    /**
     * 启用审查规则
     */
    @Transactional
    public Optional<ReviewRuleDTO> enableRule(Long id) {
        log.info("Enabling review rule: {}", id);

        Optional<ReviewRule> ruleOpt = reviewRuleDomainService.enableRule(ReviewRuleId.of(id));
        return ruleOpt.map(reviewRuleApplicationConvertor::toDTO);
    }

    /**
     * 禁用审查规则
     */
    @Transactional
    public Optional<ReviewRuleDTO> disableRule(Long id) {
        log.info("Disabling review rule: {}", id);

        Optional<ReviewRule> ruleOpt = reviewRuleDomainService.disableRule(ReviewRuleId.of(id));
        return ruleOpt.map(reviewRuleApplicationConvertor::toDTO);
    }

    /**
     * 获取所有审查规则
     */
    public List<ReviewRuleDTO> getAllRules() {
        log.debug("Getting all review rules");

        List<ReviewRule> rules = reviewRuleDomainService.findAllRules();
        return reviewRuleApplicationConvertor.toDTOList(rules);
    }

    /**
     * 根据规则类型查询规则
     */
    public List<ReviewRuleDTO> getRulesByType(String ruleType) {
        log.debug("Getting review rules by type: {}", ruleType);

        RuleType type = RuleType.fromCode(ruleType);
        List<ReviewRule> rules = reviewRuleDomainService.findRulesByType(type);
        return reviewRuleApplicationConvertor.toDTOList(rules);
    }

    /**
     * 根据启用状态查询规则
     */
    public List<ReviewRuleDTO> getRulesByEnabled(Boolean enabled) {
        log.debug("Getting review rules by enabled status: {}", enabled);

        List<ReviewRule> rules = reviewRuleDomainService.findRulesByEnabled(enabled);
        return reviewRuleApplicationConvertor.toDTOList(rules);
    }

    /**
     * 检查规则名称是否可用
     */
    public boolean isRuleNameAvailable(String ruleName) {
        log.debug("Checking rule name availability: {}", ruleName);

        return reviewRuleDomainService.isRuleNameAvailable(ruleName);
    }

    /**
     * 统计规则数量
     */
    public long countRules(Boolean enabled) {
        log.debug("Counting review rules with enabled status: {}", enabled);

        return reviewRuleDomainService.countRules(enabled);
    }

    /**
     * 根据合同类型和条款类型查找适用的规则
     */
    public List<ReviewRuleDTO> getApplicableRules(String contractType, String clauseType) {
        log.debug("Getting applicable rules for contract: {}, clause: {}", contractType, clauseType);

        List<ReviewRule> rules = reviewRuleDomainService.findApplicableRules(contractType, clauseType);
        return reviewRuleApplicationConvertor.toDTOList(rules);
    }

    /**
     * 根据提示词模式查找有效的规则
     */
    public List<ReviewRuleDTO> getEffectiveRulesByMode(String promptMode) {
        log.debug("Getting effective rules for prompt mode: {}", promptMode);

        PromptMode mode = PromptMode.fromCode(Short.valueOf(promptMode));
        List<ReviewRule> rules = reviewRuleDomainService.findEffectiveRulesByMode(mode);
        return reviewRuleApplicationConvertor.toDTOList(rules);
    }

    /**
     * 根据分类列表查询审查规则
     */
    public List<ReviewRuleDTO> getRulesByCategories(List<String> categories) {
        log.debug("Getting review rules by categories: {}", categories);

        List<ReviewRule> rules = reviewRuleDomainService.findRulesByCategories(categories);
        return reviewRuleApplicationConvertor.toDTOList(rules);
    }
}