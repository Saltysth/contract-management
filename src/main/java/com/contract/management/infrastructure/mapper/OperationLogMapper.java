package com.contract.management.infrastructure.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.contract.management.infrastructure.entity.OperationLogEntity;
import org.apache.ibatis.annotations.*;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 操作日志Mapper
 *
 * @author SaltyFish
 * @since 1.0.0
 */
@Mapper
public interface OperationLogMapper extends BaseMapper<OperationLogEntity> {

    /**
     * 根据资源ID和类型查找操作日志
     */
    @Select("SELECT * FROM operation_log WHERE resource_id = #{resourceId} AND resource_type = #{resourceType} ORDER BY created_time DESC")
    List<OperationLogEntity> findByResourceId(@Param("resourceId") Long resourceId, @Param("resourceType") String resourceType);

    /**
     * 根据合同ID查找条款抽取相关的操作日志
     */
    @Select("SELECT ol.* FROM operation_log ol " +
            "JOIN clause_extractions ce ON ol.resource_id = ce.id " +
            "WHERE ce.contract_id = #{contractId} AND ol.resource_type = '条款抽取' " +
            "ORDER BY ol.created_time DESC")
    List<OperationLogEntity> findClauseExtractionLogsByContractId(@Param("contractId") Long contractId);

    /**
     * 根据用户ID查找操作日志
     */
    @Select("SELECT * FROM operation_log WHERE user_id = #{userId} ORDER BY created_time DESC LIMIT #{limit}")
    List<OperationLogEntity> findByUserId(@Param("userId") Long userId, @Param("limit") int limit);

    /**
     * 根据时间范围查找操作日志
     */
    @Select("SELECT * FROM operation_log WHERE created_time >= #{startTime} AND created_time <= #{endTime} " +
            "ORDER BY created_time DESC LIMIT #{limit}")
    List<OperationLogEntity> findByTimeRange(@Param("startTime") LocalDateTime startTime,
                                             @Param("endTime") LocalDateTime endTime,
                                             @Param("limit") int limit);

    /**
     * 删除指定时间之前的日志
     */
    @Select("DELETE FROM operation_log WHERE created_time < #{beforeTime}")
    int deleteLogsBefore(@Param("beforeTime") LocalDateTime beforeTime);

    /**
     * 统计指定时间范围内的操作日志数量
     */
    @Select("SELECT COUNT(*) FROM operation_log WHERE created_time >= #{startTime} AND created_time <= #{endTime}")
    long countByTimeRange(@Param("startTime") LocalDateTime startTime,
                          @Param("endTime") LocalDateTime endTime);

    /**
     * 根据抽取ID查找指定资源类型的操作日志
     */
    @Select("SELECT * FROM operation_log WHERE resource_type = #{resourceType} " +
            "AND (operation_details::jsonb->'parameters'->>'extractionId')::integer = #{extractionId} " +
            "ORDER BY created_time DESC")
    List<OperationLogEntity> findOperationLogsByExtractionId(@Param("extractionId") Long extractionId,
                                                           @Param("resourceType") String resourceType);

    /**
     * 根据抽取ID查找所有操作日志，按创建时间正序排列
     */
    @Select("SELECT * FROM operation_log WHERE " +
            "(operation_details::jsonb->'parameters'->>'extractionId')::integer = #{extractionId} " +
            "ORDER BY created_time ASC")
    List<OperationLogEntity> findAllOperationLogsByExtractionId(@Param("extractionId") Long extractionId);

    /**
     * 根据合同ID查找最新的条款抽取ID
     */
    @Select("SELECT DISTINCT ol.resource_id as extraction_id, ol.created_time " +
            "FROM operation_log ol " +
            "JOIN clause_extractions ce ON ol.resource_id = ce.id " +
            "WHERE ce.contract_id = #{contractId} AND ol.resource_type = '条款抽取' " +
            "ORDER BY ol.created_time DESC LIMIT 1")
    Long findLatestExtractionIdByContractId(@Param("contractId") Long contractId);

    /**
     * 更新操作日志（使用PostgreSQL显式类型转换）
     */
    @Update("UPDATE operation_log SET operation_type = #{operationType}, " +
            "resource_type = #{resourceType}, " +
            "resource_id = #{resourceId}, " +
            "operation_details = #{operationDetails}, " +
            "user_id = #{userId}, " +
            "ip_address = #{ipAddress}, " +
            "created_time = #{createdTime} " +
            "WHERE id = #{id}")
    int updateOperationLogWithJsonb(OperationLogEntity operationLogEntity);
}