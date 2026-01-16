package com.contract.management.domain.repository;

import com.contract.management.domain.model.ReviewRule;
import com.contract.management.domain.model.valueobject.PromptMode;
import com.contract.management.domain.model.valueobject.ReviewRuleId;
import com.contract.management.domain.model.valueobject.RuleType;

import java.util.List;
import java.util.Optional;

/**
 * 审查规则仓储接口
 *
 * @author SaltyFish
 * @since 1.0.0
 */
public interface ReviewRuleRepository {

    /**
     * 保存审查规则
     *
     * @param reviewRule 审查规则
     * @return 保存后的审查规则
     */
    ReviewRule save(ReviewRule reviewRule);

    /**
     * 根据ID查找审查规则
     *
     * @param id 规则ID
     * @return 审查规则，可能为空
     */
    Optional<ReviewRule> findById(ReviewRuleId id);

    /**
     * 根据规则名称查找审查规则
     *
     * @param ruleName 规则名称
     * @return 审查规则，可能为空
     */
    Optional<ReviewRule> findByRuleName(String ruleName);

    /**
     * 查找所有审查规则
     *
     * @return 所有审查规则列表
     */
    List<ReviewRule> findAll();

    /**
     * 根据启用状态查找规则
     *
     * @param enabled 启用状态
     * @return 符合条件的规则列表
     */
    List<ReviewRule> findByEnabled(Boolean enabled);

    /**
     * 根据规则类型查找规则
     *
     * @param ruleType 规则类型
     * @return 符合条件的规则列表
     */
    List<ReviewRule> findByRuleType(RuleType ruleType);

    /**
     * 根据提示词模式查找规则
     *
     * @param promptMode 提示词模式
     * @return 符合条件的规则列表
     */
    List<ReviewRule> findByPromptMode(PromptMode promptMode);

    /**
     * 根据合同类型查找适用的规则
     *
     * @param contractType 合同类型
     * @return 适用的规则列表
     */
    List<ReviewRule> findByContractType(String contractType);

    /**
     * 根据合同类型和条款类型查找适用的规则
     *
     * @param contractType 合同类型
     * @param clauseType 条款类型
     * @return 适用的规则列表
     */
    List<ReviewRule> findByContractTypeAndClauseType(String contractType, String clauseType);

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
     * （规则模式编码 <= 目标模式编码）
     *
     * @param promptMode 提示词模式
     * @return 有效的规则列表
     */
    List<ReviewRule> findEffectiveRulesByMode(PromptMode promptMode);

    /**
     * 删除审查规则
     *
     * @param id 规则ID
     */
    void deleteById(ReviewRuleId id);

    /**
     * 检查规则名称是否已存在
     *
     * @param ruleName  规则名称
     * @param excludeId
     * @return 是否存在
     */
    boolean existsByRuleName(String ruleName, Long excludeId);

    /**
     * 统计规则数量
     *
     * @param enabled 启用状态
     * @return 规则数量
     */
    long countByEnabled(Boolean enabled);

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
     * 根据分类列表查询审查规则
     *
     * @param categories 分类列表
     * @return 符合条件的规则列表
     */
    List<ReviewRule> findByCategories(List<String> categories);
}