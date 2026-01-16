package com.contract.management.infrastructure.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.contract.management.infrastructure.entity.ClauseEntity;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Delete;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

/**
 * 条款数据访问接口
 *
 * @author SaltyFish
 * @since 1.0.0
 */
@Mapper
public interface ClauseMapper extends BaseMapper<ClauseEntity> {

    /**
     * 通过id查询未被删除的实体
     *
     * @param id 主键
     * @return 实体
     */
    @Select("SELECT * FROM clause WHERE id = #{id} AND is_deleted = false")
    ClauseEntity findById(@Param("id") Long id);

    
    /**
     * 根据抽取任务ID查询条款
     *
     * @param extractionTaskId 抽取任务ID
     * @return 条款列表
     */
    @Select("SELECT * FROM clause WHERE extraction_task_id = #{extractionTaskId} AND is_deleted = false ORDER BY created_time DESC")
    List<ClauseEntity> findByExtractionTaskId(@Param("extractionTaskId") Long extractionTaskId);

    /**
     * 根据合同ID查询条款
     *
     * @param contractId 合同ID
     * @return 条款列表
     */
    @Select("SELECT * FROM clause WHERE contract_id = #{contractId} AND is_deleted = false ORDER BY created_time DESC")
    List<ClauseEntity> findByContractId(@Param("contractId") Long contractId);

    /**
     * 根据条款类型查询条款
     *
     * @param clauseType 条款类型
     * @return 条款列表
     */
    @Select("SELECT * FROM clause WHERE clause_type = #{clauseType} AND is_deleted = false ORDER BY created_time DESC")
    List<ClauseEntity> findByClauseType(@Param("clauseType") String clauseType);

    /**
     * 根据风险等级查询条款
     *
     * @param riskLevel 风险等级
     * @return 条款列表
     */
    @Select("SELECT * FROM clause WHERE risk_level = #{riskLevel} AND is_deleted = false ORDER BY created_time DESC")
    List<ClauseEntity> findByRiskLevel(@Param("riskLevel") String riskLevel);

    /**
     * 查找高风险条款
     *
     * @return 条款列表
     */
    @Select("SELECT * FROM clause WHERE risk_level IN ('HIGH', 'CRITICAL') AND is_deleted = false ORDER BY created_time DESC")
    List<ClauseEntity> findHighRiskClauses();

    /**
     * 查找需要人工审核的条款（高风险或低置信度）
     *
     * @return 条款列表
     */
    @Select("SELECT * FROM clause WHERE is_deleted = false AND " +
            "(risk_level IN ('HIGH', 'CRITICAL') OR confidence_score < 60.00) " +
            "ORDER BY created_time DESC")
    List<ClauseEntity> findClausesNeedingReview();

    /**
     * 根据内容关键词搜索条款
     *
     * @param keyword 关键词
     * @return 条款列表
     */
    @Select("SELECT * FROM clause WHERE is_deleted = false AND " +
            "(clause_title ILIKE CONCAT('%', #{keyword}, '%') OR clause_content ILIKE CONCAT('%', #{keyword}, '%')) " +
            "ORDER BY created_time DESC")
    List<ClauseEntity> findByContentKeyword(@Param("keyword") String keyword);

    
    /**
     * 根据抽取任务ID统计条款数量
     *
     * @param extractionTaskId 抽取任务ID
     * @return 数量
     */
    @Select("SELECT COUNT(*) FROM clause WHERE extraction_task_id = #{extractionTaskId} AND is_deleted = false")
    long countByExtractionTaskId(@Param("extractionTaskId") Long extractionTaskId);

    /**
     * 根据合同ID统计条款数量
     *
     * @param contractId 合同ID
     * @return 数量
     */
    @Select("SELECT COUNT(*) FROM clause WHERE contract_id = #{contractId} AND is_deleted = false")
    long countByContractId(@Param("contractId") Long contractId);

    /**
     * 根据条款类型统计数量
     *
     * @param clauseType 条款类型
     * @return 数量
     */
    @Select("SELECT COUNT(*) FROM clause WHERE clause_type = #{clauseType} AND is_deleted = false")
    long countByClauseType(@Param("clauseType") String clauseType);

    /**
     * 根据风险等级统计数量
     *
     * @param riskLevel 风险等级
     * @return 数量
     */
    @Select("SELECT COUNT(*) FROM clause WHERE risk_level = #{riskLevel} AND is_deleted = false")
    long countByRiskLevel(@Param("riskLevel") String riskLevel);

    /**
     * 获取条款类型统计
     *
     * @return 统计结果
     */
    @Select("SELECT clause_type as type, COUNT(*) as count FROM clause WHERE is_deleted = false GROUP BY clause_type")
    List<Map<String, Object>> getClauseTypeStatistics();

    /**
     * 获取风险等级统计
     *
     * @return 统计结果
     */
    @Select("SELECT risk_level as level, COUNT(*) as count FROM clause WHERE is_deleted = false GROUP BY risk_level")
    List<Map<String, Object>> getRiskLevelStatistics();

    
    /**
     * 根据抽取任务ID批量删除条款（软删除）
     *
     * @param extractionTaskId 抽取任务ID
     * @return 删除数量
     */
    @Delete("UPDATE clause SET is_deleted = true WHERE extraction_task_id = #{extractionTaskId} AND is_deleted = false")
    int deleteAllByExtractionTaskId(@Param("extractionTaskId") Long extractionTaskId);

    /**
     * 根据合同ID批量删除条款
     *
     * @param contractId 合同ID
     * @return 删除数量
     */
    @Delete("UPDATE clause SET is_deleted = true WHERE contract_id = #{contractId} AND is_deleted = false")
    int deleteAllByContractId(@Param("contractId") Long contractId);

    /**
     * 复杂条件分页查询
     *
     * @param page 分页参数
     * @param taskId 任务ID
     * @param contractId 合同ID
     * @param clauseType 条款类型
     * @param riskLevel 风险等级
     * @param titleKeyword 标题关键词
     * @param contentKeyword 内容关键词
     * @param minConfidenceScore 最小置信度
     * @param maxConfidenceScore 最大置信度
     * @param highRiskOnly 仅高风险
     * @param hasRiskFactorsOnly 有风险因子
     * @param createdTimeStart 创建时间开始
     * @param createdTimeEnd 创建时间结束
     * @param createdBy 创建人
     * @return 分页结果
     */
    IPage<ClauseEntity> selectByFilters(
            Page<ClauseEntity> page,
            @Param("extractionTaskId") Long extractionTaskId,
            @Param("contractId") Long contractId,
            @Param("clauseType") String clauseType,
            @Param("riskLevel") String riskLevel,
            @Param("titleKeyword") String titleKeyword,
            @Param("contentKeyword") String contentKeyword,
            @Param("minConfidenceScore") BigDecimal minConfidenceScore,
            @Param("maxConfidenceScore") BigDecimal maxConfidenceScore,
            @Param("highRiskOnly") Boolean highRiskOnly,
            @Param("hasRiskFactorsOnly") Boolean hasRiskFactorsOnly,
            @Param("createdTimeStart") LocalDateTime createdTimeStart,
            @Param("createdTimeEnd") LocalDateTime createdTimeEnd,
            @Param("createdBy") Long createdBy
    );
}