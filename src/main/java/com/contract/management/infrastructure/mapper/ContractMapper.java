package com.contract.management.infrastructure.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.contract.management.infrastructure.entity.ContractEntity;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Delete;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

/**
 * 合同数据访问接口
 *
 * @author SaltyFish
 * @since 1.0.0
 */
@Mapper
public interface ContractMapper extends BaseMapper<ContractEntity> {

    /**
     * 通过id查询未被删除的实体
     * @param id 主键
     * @return 实体
     */
    @Select("SELECT * FROM contract WHERE id = #{id} AND is_deleted = false")
    ContractEntity findById(@Param("id") Long id);

    /**
     * 逻辑删除（仅更新 is_deleted 及审计字段）
     */
    @Delete("UPDATE contract " +
        "SET is_deleted = TRUE, " +
        "    contract_name = #{contractName, jdbcType=VARCHAR}, " +
        "    updated_by = #{operatorId, jdbcType=BIGINT}, " +
        "    updated_time = #{updatedTime, jdbcType=TIMESTAMP} " +
        "WHERE id = #{id, jdbcType=BIGINT} AND is_deleted = FALSE")
    int markDeletedById(@Param("contractName") String contractName, @Param("id") Long id,
        @Param("operatorId") Long operatorId,
        @Param("updatedTime") LocalDateTime updatedTime);

    /**
     * 恢复逻辑删除（仅更新 is_deleted 及审计字段）
     */
    @Delete("UPDATE contract " +
        "SET is_deleted = FALSE, " +
        "    updated_by = #{operatorId, jdbcType=BIGINT}, " +
        "    updated_time = #{updatedTime, jdbcType=TIMESTAMP} " +
        "WHERE id = #{id, jdbcType=BIGINT} AND is_deleted = TRUE")
    int restoreDeletedById(@Param("id") Long id,
        @Param("operatorId") Long operatorId,
        @Param("updatedTime") LocalDateTime updatedTime);

    /**
     * 查找即将到期的合同
     *
     * @param beforeDate 截止日期
     * @return 合同列表
     */
    @Select("SELECT * FROM contract WHERE is_deleted = false AND expiry_date IS NOT NULL AND expiry_date <= #{beforeDate}")
    List<ContractEntity> findExpiringContracts(@Param("beforeDate") LocalDate beforeDate);

    
    /**
     * 检查合同名称是否已存在（同一创建人）
     *
     * @param contractName 合同名称
     * @param createdBy 创建人ID
     * @return 数量
     */
    @Select("SELECT COUNT(*) FROM contract WHERE is_deleted = false AND contract_name = #{contractName, jdbcType=VARCHAR} AND created_by = #{createdBy, jdbcType=BIGINT}")
    long countByNameAndCreatedBy(@Param("contractName") String contractName, @Param("createdBy") Long createdBy);

    /**
     * 检查合同名称是否已存在（同一创建人，排除指定合同）
     *
     * @param contractName 合同名称
     * @param createdBy 创建人ID
     * @param excludeContractId 排除的合同ID
     * @return 数量
     */
    @Select("SELECT COUNT(*) FROM contract WHERE is_deleted = false AND contract_name = #{contractName, jdbcType=VARCHAR} AND created_by = #{createdBy, jdbcType=BIGINT} AND id != #{excludeContractId, jdbcType=BIGINT}")
    long countByNameAndCreatedByAndIdNot(@Param("contractName") String contractName, @Param("createdBy") Long createdBy, @Param("excludeContractId") Long excludeContractId);

    /**
     * 获取类型统计
     *
     * @return 统计结果
     */
    @Select("SELECT contract_type as type, COUNT(*) as count FROM contract WHERE is_deleted = false GROUP BY contract_type")
    List<Map<String, Object>> getTypeStatistics();

    
    /**
     * 查找需要关注的合同（即将到期等）
     *
     * @return 合同列表
     */
    @Select("SELECT * FROM contract WHERE is_deleted = false AND " +
            "expiry_date IS NOT NULL AND expiry_date <= CURRENT_DATE + INTERVAL '30 days'")
    List<ContractEntity> findContractsNeedingAttention();

    
    /**
     * 复杂条件分页查询
     *
     * @param page 分页参数
     * @param contractType 合同类型
     * @param createdBy 创建人
     * @param keyword 关键词
     * @param startDate 开始日期
     * @param endDate 结束日期
     * @return 分页结果
     */
    IPage<ContractEntity> selectByFilters(
        Page<ContractEntity> page,
        @Param("contractType") String contractType,
        @Param("createdBy") Long createdBy,
        @Param("keyword") String keyword,
        @Param("startDate") LocalDate startDate,
        @Param("endDate") LocalDate endDate
    );
}