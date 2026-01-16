package com.contract.management.interfaces.rest.api.v1.convertor;

import com.contract.management.application.dto.ClauseDTO;
import com.contract.management.application.dto.ClauseQueryDTO;
import com.contract.management.domain.model.ClauseType;
import com.contract.management.domain.model.RiskLevel;
import com.contract.management.interfaces.rest.api.v1.dto.request.ClauseQueryRequest;
import com.contract.management.interfaces.rest.api.v1.dto.request.CreateClauseRequest;
import com.contract.management.interfaces.rest.api.v1.dto.request.UpdateClauseRequest;
import com.contract.management.interfaces.rest.api.v1.dto.response.ClauseResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.mapstruct.factory.Mappers;

import java.util.List;

/**
 * 条款REST接口转换器 - MapStruct实现
 * 负责REST DTO与应用DTO之间的转换
 *
 * @author SaltyFish
 * @since 1.0.0
 */
@Mapper(componentModel = "spring")
public interface ClauseRestConvertor {

    ClauseRestConvertor INSTANCE = Mappers.getMapper(ClauseRestConvertor.class);

    /**
     * 创建请求转换为应用DTO
     *
     * @param request 创建请求
     * @return 应用DTO
     */
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdTime", ignore = true)
    @Mapping(target = "hasRisk", ignore = true)
    @Mapping(target = "isHighRisk", ignore = true)
    @Mapping(target = "hasConfidenceScore", ignore = true)
    @Mapping(target = "hasExtractedEntities", ignore = true)
    @Mapping(target = "hasRiskFactors", ignore = true)
    ClauseDTO toApplicationDTO(CreateClauseRequest request);

    /**
     * 更新请求转换为应用DTO
     *
     * @param request 更新请求
     * @return 应用DTO
     */
    @Mapping(target = "contractId", ignore = true) // 合同ID不可修改
    @Mapping(target = "createdTime", ignore = true)
    @Mapping(target = "hasRisk", ignore = true)
    @Mapping(target = "isHighRisk", ignore = true)
    @Mapping(target = "hasConfidenceScore", ignore = true)
    @Mapping(target = "hasExtractedEntities", ignore = true)
    @Mapping(target = "hasRiskFactors", ignore = true)
    ClauseDTO toApplicationDTO(UpdateClauseRequest request);

    /**
     * 查询请求转换为应用DTO
     *
     * @param request 查询请求
     * @return 应用DTO
     */
    ClauseQueryDTO toApplicationDTO(ClauseQueryRequest request);

    /**
     * 应用DTO转换为响应DTO
     *
     * @param dto 应用DTO
     * @return 响应DTO
     */
    @Mapping(target = "clauseTypeDescription", source = "clauseType", qualifiedByName = "clauseTypeToDescription")
    @Mapping(target = "riskLevelDescription", source = "riskLevel", qualifiedByName = "riskLevelToDescription")
    ClauseResponse toResponse(ClauseDTO dto);

    /**
     * 应用DTO列表转换为响应DTO列表
     *
     * @param dtos 应用DTO列表
     * @return 响应DTO列表
     */
    List<ClauseResponse> toResponseList(List<ClauseDTO> dtos);

    /**
     * 条款类型转换为描述
     *
     * @param clauseType 条款类型
     * @return 描述
     */
    @Named("clauseTypeToDescription")
    default String clauseTypeToDescription(ClauseType clauseType) {
        return clauseType != null ? clauseType.getDescription() : null;
    }

    /**
     * 风险等级转换为描述
     *
     * @param riskLevel 风险等级
     * @return 描述
     */
    @Named("riskLevelToDescription")
    default String riskLevelToDescription(RiskLevel riskLevel) {
        return riskLevel != null ? riskLevel.getDescription() : null;
    }
}