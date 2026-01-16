package com.contract.management.domain.repository;

import com.contract.management.domain.model.Clause;
import com.contract.management.domain.model.ClauseId;
import com.contract.management.domain.model.ClauseType;
import com.contract.management.domain.model.RiskLevel;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * 条款仓储接口
 *
 * @author SaltyFish
 * @since 1.0.0
 */
public interface ClauseRepository {

    // ==================== 基础CRUD ====================

    /**
     * 保存条款
     *
     * @param clause 条款
     * @return 保存后的条款
     */
    Clause save(Clause clause);

    /**
     * 更新条款
     *
     * @param clause 条款
     * @return 更新后的条款
     */
    Clause update(Clause clause);

    /**
     * 根据ID查找条款
     *
     * @param id 条款ID
     * @return 条款
     */
    Optional<Clause> findById(ClauseId id);

    /**
     * 删除条款（软删除）
     *
     * @param id 条款ID
     */
    void delete(ClauseId id);

    /**
     * 检查条款是否存在
     *
     * @param id 条款ID
     * @return 是否存在
     */
    boolean existsById(ClauseId id);

    // ==================== 查询方法 ====================

    /**
     * 根据抽取任务ID查找条款
     *
     * @param extractionTaskId 抽取任务ID
     * @return 条款列表
     */
    List<Clause> findByExtractionTaskId(Long extractionTaskId);

    /**
     * 根据合同ID查找条款
     *
     * @param contractId 合同ID
     * @return 条款列表
     */
    List<Clause> findByContractId(Long contractId);

    /**
     * 根据合同ID分页查找条款
     *
     * @param contractId 合同ID
     * @param pageable 分页参数
     * @return 分页结果
     */
    Page<Clause> findByContractId(Long contractId, Pageable pageable);

    /**
     * 根据条款类型查找条款
     *
     * @param clauseType 条款类型
     * @return 条款列表
     */
    List<Clause> findByClauseType(ClauseType clauseType);

    /**
     * 根据风险等级查找条款
     *
     * @param riskLevel 风险等级
     * @return 条款列表
     */
    List<Clause> findByRiskLevel(RiskLevel riskLevel);

    /**
     * 查找高风险条款
     *
     * @return 条款列表
     */
    List<Clause> findHighRiskClauses();

    /**
     * 根据过滤条件分页查询条款
     *
     * @param filters 过滤条件
     * @param pageable 分页参数
     * @return 分页结果
     */
    Page<Clause> findByFilters(ClauseFilters filters, Pageable pageable);

    /**
     * 根据过滤条件查询条款列表
     *
     * @param filters 过滤条件
     * @return 条款列表
     */
    List<Clause> findByFilters(ClauseFilters filters);

    // ==================== 统计方法 ====================
    /**
     * 根据合同ID统计条款数量
     *
     * @param contractId 合同ID
     * @return 数量
     */
    long countByContractId(Long contractId);

    /**
     * 根据条款类型统计数量
     *
     * @param clauseType 条款类型
     * @return 数量
     */
    long countByClauseType(ClauseType clauseType);

    /**
     * 根据风险等级统计数量
     *
     * @param riskLevel 风险等级
     * @return 数量
     */
    long countByRiskLevel(RiskLevel riskLevel);

    /**
     * 获取条款类型统计
     *
     * @return 类型统计Map
     */
    Map<ClauseType, Long> getClauseTypeStatistics();

    /**
     * 获取风险等级统计
     *
     * @return 风险等级统计Map
     */
    Map<RiskLevel, Long> getRiskLevelStatistics();

    // ==================== 业务查询 ====================

    /**
     * 查找需要人工审核的条款（高风险或低置信度）
     *
     * @return 条款列表
     */
    List<Clause> findClausesNeedingReview();

    /**
     * 根据内容关键词搜索条款
     *
     * @param keyword 关键词
     * @return 条款列表
     */
    List<Clause> findByContentKeyword(String keyword);

    // ==================== 批量操作 ====================

    /**
     * 批量保存条款
     *
     * @param clauses 条款列表
     * @return 保存后的条款列表
     */
    List<Clause> saveAll(List<Clause> clauses);

    /**
     * 批量删除条款
     *
     * @param clauseIds 条款ID列表
     */
    void deleteAll(List<ClauseId> clauseIds);

    /**
     * 根据抽取任务ID批量删除条款（软删除）
     *
     * @param extractionTaskId 抽取任务ID
     */
    void deleteAllByExtractionTaskId(Long extractionTaskId);

    /**
     * 根据合同ID批量删除条款
     *
     * @param contractId 合同ID
     */
    void deleteAllByContractId(Long contractId);
}