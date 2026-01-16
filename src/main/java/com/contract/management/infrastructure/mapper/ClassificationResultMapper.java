package com.contract.management.infrastructure.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.contract.management.infrastructure.entity.ClassificationResultEntity;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 合同分类结果Mapper
 *
 * @author SaltyFish
 * @since 1.0.0
 */
@Mapper
public interface ClassificationResultMapper extends BaseMapper<ClassificationResultEntity> {

    /**
     * 查找合同的活跃分类结果
     *
     * @param contractId 合同ID
     * @return 活跃的分类结果
     */
    @Select("SELECT * FROM classification_result WHERE contract_id = #{contractId} AND is_active = TRUE AND is_deleted = FALSE")
    ClassificationResultEntity findActiveByContractId(@Param("contractId") Long contractId);

    /**
     * 查找合同的所有分类结果（包括历史记录）
     *
     * @param contractId 合同ID
     * @return 所有分类结果列表
     */
    @Select("SELECT * FROM classification_result WHERE contract_id = #{contractId} AND is_deleted = FALSE ORDER BY created_time DESC")
    List<ClassificationResultEntity> findAllByContractId(@Param("contractId") Long contractId);

    /**
     * 停用合同的活跃分类结果
     *
     * @param contractId 合同ID
     * @param operatorId 操作人ID
     * @param updatedTime 更新时间
     * @return 更新的记录数
     */
    @Update("UPDATE classification_result SET is_active = FALSE, updated_by = #{operatorId}, updated_time = #{updatedTime} " +
            "WHERE contract_id = #{contractId} AND is_active = TRUE AND is_deleted = FALSE")
    int deactivateByContractId(@Param("contractId") Long contractId,
                               @Param("operatorId") Long operatorId,
                               @Param("updatedTime") LocalDateTime updatedTime);
}