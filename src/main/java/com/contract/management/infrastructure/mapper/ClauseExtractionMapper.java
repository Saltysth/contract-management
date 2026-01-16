package com.contract.management.infrastructure.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.contract.management.infrastructure.entity.ClauseExtractionEntity;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Update;

/**
 * 条款抽取Mapper
 *
 * @author SaltyFish
 * @since 1.0.0
 */
@Mapper
public interface ClauseExtractionMapper extends BaseMapper<ClauseExtractionEntity> {

    /**
     * 根据合同ID软删除条款抽取
     */
    @Update("UPDATE clause_extractions SET is_deleted = true, updated_time = CURRENT_TIMESTAMP WHERE contract_id = #{contractId} AND is_deleted = false")
    int deleteByContractId(@Param("contractId") Long contractId);
}