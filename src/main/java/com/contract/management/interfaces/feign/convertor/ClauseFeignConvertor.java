package com.contract.management.interfaces.feign.convertor;

import com.contract.common.feign.dto.ClauseFeignDTO;
import com.contract.common.feign.dto.ClausePageResultFeignDTO;
import com.contract.common.feign.dto.ClauseQueryFeignDTO;
import com.contract.management.application.dto.ClauseDTO;
import com.contract.management.application.dto.ClauseQueryDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;

import java.util.ArrayList;
import java.util.List;

/**
 * 条款Feign转换器
 * 用于应用层DTO与Feign DTO之间的转换
 *
 * @author SaltyFish
 * @since 1.0.0
 */
@Slf4j
public class ClauseFeignConvertor {

    /**
     * 应用层DTO转Feign DTO
     *
     * @param clauseDTO 应用层DTO
     * @return Feign DTO
     */
    public static ClauseFeignDTO convertToFeignDTO(ClauseDTO clauseDTO) {
        if (clauseDTO == null) {
            return null;
        }

        ClauseFeignDTO feignDTO = new ClauseFeignDTO();

        // 基本信息
        feignDTO.setId(clauseDTO.getId());
        feignDTO.setExtractionTaskId(clauseDTO.getExtractionTaskId());
        feignDTO.setContractId(clauseDTO.getContractId());

        // 条款类型信息
        if (clauseDTO.getClauseType() != null) {
            feignDTO.setClauseType(clauseDTO.getClauseType().name());
        }

        // 条款内容
        feignDTO.setClauseTitle(clauseDTO.getClauseTitle());
        feignDTO.setClauseContent(clauseDTO.getClauseContent());
        feignDTO.setClausePosition(clauseDTO.getClausePosition());

        // 分析结果
        feignDTO.setConfidenceScore(clauseDTO.getConfidenceScore());
        feignDTO.setExtractedEntities(clauseDTO.getExtractedEntities());

        // 风险信息
        if (clauseDTO.getRiskLevel() != null) {
            feignDTO.setRiskLevel(clauseDTO.getRiskLevel().name());
        }
        feignDTO.setRiskFactors(clauseDTO.getRiskFactors());

        // 标记字段
        feignDTO.setHasRisk(clauseDTO.getHasRisk());
        feignDTO.setIsHighRisk(clauseDTO.getIsHighRisk());
        feignDTO.setHasConfidenceScore(clauseDTO.getHasConfidenceScore());
        feignDTO.setHasExtractedEntities(clauseDTO.getHasExtractedEntities());
        feignDTO.setHasRiskFactors(clauseDTO.getHasRiskFactors());

        // 时间信息
        feignDTO.setCreatedTime(clauseDTO.getCreatedTime());

        return feignDTO;
    }

    /**
     * Feign查询DTO转应用查询DTO
     *
     * @param feignQuery Feign查询DTO
     * @return 应用查询DTO
     */
    public static ClauseQueryDTO convertToApplicationQuery(ClauseQueryFeignDTO feignQuery) {
        if (feignQuery == null) {
            return new ClauseQueryDTO();
        }

        ClauseQueryDTO query = new ClauseQueryDTO();

        // 基本查询条件
        query.setContractId(feignQuery.getContractId());
        query.setExtractTaskId(feignQuery.getExtractionTaskId());

        // 条款类型转换
        if (feignQuery.getClauseType() != null) {
            try {
                query.setClauseType(com.contract.management.domain.model.ClauseType.valueOf(feignQuery.getClauseType()));
            } catch (IllegalArgumentException e) {
                log.warn("无效的条款类型: {}", feignQuery.getClauseType());
                query.setClauseType(null);
            }
        }

        // 关键词查询条件
        query.setTitleKeyword(feignQuery.getTitleKeyword());
        query.setContentKeyword(feignQuery.getContentKeyword());

        // 风险等级转换
        if (feignQuery.getRiskLevel() != null) {
            try {
                query.setRiskLevel(com.contract.management.domain.model.RiskLevel.valueOf(feignQuery.getRiskLevel()));
            } catch (IllegalArgumentException e) {
                log.warn("无效的风险等级: {}", feignQuery.getRiskLevel());
                query.setRiskLevel(null);
            }
        }

        // 风险标记条件
        query.setHighRiskOnly(feignQuery.getHighRiskOnly());
        query.setHasRiskFactorsOnly(feignQuery.getHasRiskFactorsOnly());

        // 置信度分数范围
        query.setMinConfidenceScore(feignQuery.getMinConfidenceScore());
        query.setMaxConfidenceScore(feignQuery.getMaxConfidenceScore());

        // 时间范围
        query.setCreatedTimeStart(feignQuery.getCreatedTimeStart());
        query.setCreatedTimeEnd(feignQuery.getCreatedTimeEnd());

        // 创建人
        query.setCreatedBy(feignQuery.getCreatedBy());

        // 排序条件
        query.setSort(feignQuery.getSort());
        query.setDirection(feignQuery.getDirection());

        return query;
    }

    /**
     * 应用分页结果转Feign分页结果
     *
     * @param pageResult Spring Data Page对象
     * @return Feign分页结果
     */
    public static ClausePageResultFeignDTO convertToFeignPageResult(Page<ClauseDTO> pageResult) {
        if (pageResult == null) {
            return ClausePageResultFeignDTO.empty();
        }

        log.debug("转换条款分页结果，总记录数: {}, 当前页: {}, 每页大小: {}",
            pageResult.getTotalElements(), pageResult.getNumber() + 1, pageResult.getSize());

        // 转换数据列表
        List<ClauseFeignDTO> records = new ArrayList<>();
        pageResult.getContent().forEach(clauseDTO -> {
            ClauseFeignDTO feignDTO = convertToFeignDTO(clauseDTO);
            if (feignDTO != null) {
                records.add(feignDTO);
            }
        });

        // 构建分页结果
        return ClausePageResultFeignDTO.of(
            records,
            pageResult.getTotalElements(),
            pageResult.getNumber() + 1, // Spring Data页码从0开始，Feign从1开始
            pageResult.getSize()
        );
    }

    /**
     * 应用DTO列表转Feign DTO列表
     *
     * @param clauseDTOs 应用DTO列表
     * @return Feign DTO列表
     */
    public static List<ClauseFeignDTO> convertToFeignDTOList(List<ClauseDTO> clauseDTOs) {
        if (clauseDTOs == null || clauseDTOs.isEmpty()) {
            return List.of();
        }

        List<ClauseFeignDTO> feignDTOs = new ArrayList<>();
        clauseDTOs.forEach(clauseDTO -> {
            ClauseFeignDTO feignDTO = convertToFeignDTO(clauseDTO);
            if (feignDTO != null) {
                feignDTOs.add(feignDTO);
            }
        });

        return feignDTOs;
    }
}