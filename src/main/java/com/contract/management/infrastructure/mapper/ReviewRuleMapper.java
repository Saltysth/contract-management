package com.contract.management.infrastructure.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.contract.management.infrastructure.entity.ReviewRuleEntity;
import org.apache.ibatis.annotations.*;

import java.util.List;

/**
 * 审查规则 Mapper
 * 提供复杂查询和分页支持
 *
 * @author SaltyFish
 * @since 1.0.0
 */
@Mapper
public interface ReviewRuleMapper extends BaseMapper<ReviewRuleEntity> {

    /**
     * 根据规则名称查找规则
     *
     * @param ruleName 规则名称
     * @return 规则实体
     */
    @Select("SELECT * FROM contract_review_rule WHERE rule_name = #{ruleName}")
    ReviewRuleEntity findByRuleName(@Param("ruleName") String ruleName);

    /**
     * 根据规则名称查找规则（排除指定ID）
     *
     * @param ruleName 规则名称
     * @param excludeId 排除的规则ID
     * @return 规则实体
     */
    @Select("SELECT * FROM contract_review_rule WHERE rule_name = #{ruleName} AND id != #{excludeId}")
    ReviewRuleEntity findByRuleNameExcludeId(@Param("ruleName") String ruleName, @Param("excludeId") Long excludeId);

    /**
     * 根据规则类型查找规则
     *
     * @param ruleType 规则类型
     * @return 规则列表
     */
    @Select("SELECT * FROM contract_review_rule WHERE rule_type = #{ruleType} ORDER BY create_time DESC")
    List<ReviewRuleEntity> findByRuleType(@Param("ruleType") String ruleType);

    /**
     * 根据启用状态查找规则
     *
     * @param enabled 启用状态
     * @return 规则列表
     */
    @Select("SELECT * FROM contract_review_rule WHERE enabled = #{enabled} ORDER BY create_time DESC")
    List<ReviewRuleEntity> findByEnabled(@Param("enabled") Boolean enabled);

    /**
     * 根据提示词模式查找规则
     *
     * @param promptModeCode 提示词模式编码
     * @return 规则列表
     */
    @Select("SELECT * FROM contract_review_rule WHERE prompt_mode_code = #{promptModeCode} ORDER BY create_time DESC")
    List<ReviewRuleEntity> findByPromptMode(@Param("promptModeCode") Short promptModeCode);

    /**
     * 根据合同类型查找适用的规则
     *
     * @param contractType 合同类型
     * @return 适用的规则列表
     */
    @Select("SELECT * FROM contract_review_rule WHERE enabled = true " +
            "AND (#{contractType} = ANY(applicable_contract_type) OR 'all' = ANY(applicable_contract_type)) " +
            "ORDER BY rule_type, create_time DESC")
    List<ReviewRuleEntity> findByContractType(@Param("contractType") String contractType);

    /**
     * 根据合同类型和条款类型查找适用的规则
     *
     * @param contractType 合同类型
     * @param clauseType 条款类型
     * @return 适用的规则列表
     */
    @Select("SELECT * FROM contract_review_rule WHERE enabled = true " +
            "AND (#{contractType} = ANY(applicable_contract_type) OR 'all' = ANY(applicable_contract_type)) " +
            "AND #{clauseType} = ANY(applicable_clause_type) " +
            "ORDER BY rule_type, create_time DESC")
    List<ReviewRuleEntity> findByContractTypeAndClauseType(@Param("contractType") String contractType,
                                                          @Param("clauseType") String clauseType);

    /**
     * 根据合同类型、条款类型和提示词模式查找适用的已启用规则
     *
     * @param contractType 合同类型
     * @param clauseType 条款类型
     * @param promptModeCode 提示词模式编码
     * @return 适用的规则列表
     */
    @Select("SELECT * FROM contract_review_rule WHERE enabled = true " +
            "AND prompt_mode_code <= #{promptModeCode} " +
            "AND (#{contractType} = ANY(applicable_contract_type) OR 'all' = ANY(applicable_contract_type)) " +
            "AND #{clauseType} = ANY(applicable_clause_type) " +
            "ORDER BY rule_type, prompt_mode_code, create_time DESC")
    List<ReviewRuleEntity> findApplicableRules(@Param("contractType") String contractType,
                                              @Param("clauseType") String clauseType,
                                              @Param("promptModeCode") Short promptModeCode);

    /**
     * 根据提示词模式查找所有已启用且有效的规则
     *
     * @param promptModeCode 提示词模式编码
     * @return 有效的规则列表
     */
    @Select("SELECT * FROM contract_review_rule WHERE enabled = true " +
            "AND prompt_mode_code <= #{promptModeCode} " +
            "ORDER BY rule_type, prompt_mode_code, create_time DESC")
    List<ReviewRuleEntity> findEffectiveRulesByMode(@Param("promptModeCode") Short promptModeCode);

    /**
     * 根据条件分页查询规则（支持多条件组合）
     *
     * @param page 分页对象
     * @param ruleName 规则名称（模糊查询）
     * @param ruleType 规则类型
     * @param enabled 启用状态
     * @param promptModeCode 提示词模式编码
     * @param contractType 合同类型（数组包含查询）
     * @param clauseType 条款类型（数组包含查询）
     * @return 分页结果
     */
    IPage<ReviewRuleEntity> selectByConditions(
            Page<ReviewRuleEntity> page,
            @Param("ruleName") String ruleName,
            @Param("ruleType") String ruleType,
            @Param("enabled") Boolean enabled,
            @Param("promptModeCode") Short promptModeCode,
            @Param("contractType") String contractType,
            @Param("clauseType") String clauseType
    );

    /**
     * 统计规则数量
     *
     * @param enabled 启用状态
     * @return 规则数量
     */
    @Select("SELECT COUNT(*) FROM contract_review_rule WHERE enabled = #{enabled}")
    long countByEnabled(@Param("enabled") Boolean enabled);

    /**
     * 按规则类型统计规则数量
     *
     * @return 统计结果
     */
    @Select("SELECT rule_type, COUNT(*) as count FROM contract_review_rule GROUP BY rule_type ORDER BY count DESC")
    List<java.util.Map<String, Object>> getRuleTypeStatistics();

    /**
     * 按提示词模式统计规则数量
     *
     * @return 统计结果
     */
    @Select("SELECT prompt_mode_code, COUNT(*) as count FROM contract_review_rule GROUP BY prompt_mode_code ORDER BY prompt_mode_code")
    List<java.util.Map<String, Object>> getPromptModeStatistics();

    /**
     * 检查是否存在指定的规则名称
     *
     * @param ruleName  规则名称
     * @param excludeId
     * @return 是否存在
     */
    boolean existsByRuleName(@Param("ruleName") String ruleName, @Param("excludeId") Long excludeId);

    /**
     * 根据分类列表查询审查规则
     *
     * @param categories 分类列表
     * @return 符合条件的规则列表
     */
    @Select("<script>" +
            "SELECT * FROM contract_review_rule WHERE category IN " +
            "<foreach collection='categories' item='category' open='(' separator=',' close=')'>" +
            "#{category}" +
            "</foreach>" +
            "ORDER BY create_time DESC" +
            "</script>")
    List<ReviewRuleEntity> findByCategories(@Param("categories") List<String> categories);
}