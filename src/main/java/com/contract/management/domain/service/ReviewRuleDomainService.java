package com.contract.management.domain.service;

import com.contract.management.domain.model.ReviewRule;
import com.contract.management.domain.model.valueobject.PromptMode;
import com.contract.management.domain.model.valueobject.ReviewRuleId;
import com.contract.management.domain.model.valueobject.RuleName;
import com.contract.management.domain.model.valueobject.RuleType;
import com.contract.management.domain.repository.ReviewRuleRepository;

import java.util.List;
import java.util.Optional;

/**
 * 审查规则领域服务
 *
 * @author SaltyFish
 * @since 1.0.0
 */
public interface ReviewRuleDomainService {

    /**
     * 创建审查规则
     *
     * @param reviewRule 审查规则
     * @return 创建后的审查规则
     */
    ReviewRule createRule(ReviewRule reviewRule);

    /**
     * 更新审查规则
     *
     * @param reviewRule 审查规则
     * @return 更新后的审查规则
     */
    ReviewRule updateRule(ReviewRule reviewRule);

    /**
     * 根据ID删除审查规则
     *
     * @param id 规则ID
     */
    void deleteRule(ReviewRuleId id);

    /**
     * 启用审查规则
     *
     * @param id 规则ID
     * @return 启用后的规则
     */
    Optional<ReviewRule> enableRule(ReviewRuleId id);

    /**
     * 禁用审查规则
     *
     * @param id 规则ID
     * @return 禁用后的规则
     */
    Optional<ReviewRule> disableRule(ReviewRuleId id);

    /**
     * 根据ID查找审查规则
     *
     * @param id 规则ID
     * @return 审查规则，可能为空
     */
    Optional<ReviewRule> findRuleById(ReviewRuleId id);

    /**
     * 根据规则名称查找审查规则
     *
     * @param ruleName 规则名称
     * @return 审查规则，可能为空
     */
    Optional<ReviewRule> findRuleByRuleName(String ruleName);

    /**
     * 查找所有审查规则
     *
     * @return 所有审查规则列表
     */
    List<ReviewRule> findAllRules();

    /**
     * 根据规则类型查找规则
     *
     * @param ruleType 规则类型
     * @return 符合条件的规则列表
     */
    List<ReviewRule> findRulesByType(RuleType ruleType);

    /**
     * 根据启用状态查找规则
     *
     * @param enabled 启用状态
     * @return 符合条件的规则列表
     */
    List<ReviewRule> findRulesByEnabled(Boolean enabled);

    /**
     * 根据合同类型和条款类型查找适用的规则
     *
     * @param contractType 合同类型
     * @param clauseType 条款类型
     * @return 适用的规则列表
     */
    List<ReviewRule> findApplicableRules(String contractType, String clauseType);

    /**
     * 根据合同类型、条款类型和提示词模式查找适用的已启用规则
     *
     * @param contractType 合同类型
     * @param clauseType 条款类型
     * @param promptMode 提示词模式
     * @return 适用的规则列表
     */
    List<ReviewRule> findApplicableRules(String contractType, String clauseType, PromptMode promptMode);

    /**
     * 根据提示词模式查找所有已启用且有效的规则
     *
     * @param promptMode 提示词模式
     * @return 有效的规则列表
     */
    List<ReviewRule> findEffectiveRulesByMode(PromptMode promptMode);

    /**
     * 检查规则名称是否可用（不存在或排除指定ID）
     *
     * @param ruleName 规则名称
     * @return 是否可用
     */
    boolean isRuleNameAvailable(String ruleName);

    /**
     * 统计规则数量
     *
     * @param enabled 启用状态
     * @return 规则数量
     */
    long countRules(Boolean enabled);

    /**
     * 根据条件分页查询规则
     *
     * @param ruleName 规则名称（模糊查询）
     * @param ruleType 规则类型
     * @param enabled 启用状态
     * @param promptModeCode 提示词模式编码
     * @param contractType 合同类型（数组包含查询）
     * @param clauseType 条款类型（数组包含查询）
     * @param pageable 分页参数
     * @return 分页结果
     */
    org.springframework.data.domain.Page<ReviewRule> findByConditions(String ruleName, String ruleType, Boolean enabled,
                                                                    Short promptModeCode, String contractType, String clauseType,
                                                                    org.springframework.data.domain.Pageable pageable);

    /**
     * 批量删除规则
     *
     * @param ids 规则ID列表
     */
    void deleteRules(List<ReviewRuleId> ids);

    /**
     * 根据分类列表查询审查规则
     *
     * @param categories 分类列表
     * @return 符合条件的规则列表
     */
    List<ReviewRule> findRulesByCategories(List<String> categories);
}