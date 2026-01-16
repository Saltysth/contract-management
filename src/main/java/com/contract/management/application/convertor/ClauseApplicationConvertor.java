package com.contract.management.application.convertor;

import com.contract.management.application.dto.ClauseDTO;
import com.contract.management.application.dto.ClauseQueryDTO;
import com.contract.management.domain.model.Clause;
import com.contract.management.domain.repository.ClauseFilters;
import com.contract.management.domain.model.valueobject.ClauseContent;
import com.contract.management.domain.model.valueobject.ClauseTitle;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

import java.util.List;

/**
 * 条款应用层转换器 - MapStruct实现
 * 负责领域模型与应用DTO之间的转换
 *
 * @author SaltyFish
 * @since 1.0.0
 */
@Mapper(componentModel = "spring")
public interface ClauseApplicationConvertor {

    ClauseApplicationConvertor INSTANCE = Mappers.getMapper(ClauseApplicationConvertor.class);

    /**
     * 领域模型转换为DTO
     *
     * @param clause 领域模型
     * @return DTO
     */
    @Mapping(source = "id.value", target = "id")
    @Mapping(source = "extractionTaskId", target = "extractionTaskId")
    @Mapping(source = "clauseTitle.value", target = "clauseTitle")
    @Mapping(source = "clauseContent.value", target = "clauseContent")
    @Mapping(target = "hasRisk", expression = "java(clause.hasRisk())")
    @Mapping(target = "isHighRisk", expression = "java(clause.isHighRisk())")
    @Mapping(target = "hasConfidenceScore", expression = "java(clause.hasConfidenceScore())")
    @Mapping(target = "hasExtractedEntities", expression = "java(clause.hasExtractedEntities())")
    @Mapping(target = "hasRiskFactors", expression = "java(clause.hasRiskFactors())")
    ClauseDTO toDTO(Clause clause);

    /**
     * DTO转换为领域模型（用于创建）
     *
     * @param dto DTO
     * @return 领域模型
     */
    default Clause toDomainForCreate(ClauseDTO dto) {
        if (dto == null) {
            return null;
        }

        ClauseTitle clauseTitle = dto.getClauseTitle() != null ? ClauseTitle.of(dto.getClauseTitle()) : null;
        ClauseContent clauseContent = ClauseContent.of(dto.getClauseContent());

        return new Clause(
            null, // id
            dto.getExtractionTaskId(),
            dto.getContractId(),
            dto.getClauseType(),
            clauseTitle,
            clauseContent,
            1L // createdBy
        );
    }

    /**
     * DTO转换为领域模型（用于更新）
     *
     * @param dto DTO
     * @return 领域模型
     */
    default Clause toDomainForUpdate(ClauseDTO dto) {
        if (dto == null) {
            return null;
        }

        // 对于更新操作，我们需要先通过应用服务查找现有的条款
        // 这里只创建一个用于更新的对象，实际的更新逻辑在领域服务中处理
        ClauseTitle clauseTitle = dto.getClauseTitle() != null ? ClauseTitle.of(dto.getClauseTitle()) : null;
        ClauseContent clauseContent = ClauseContent.of(dto.getClauseContent());

        return new Clause(
            dto.getId() != null ? com.contract.management.domain.model.ClauseId.of(dto.getId()) : null,
            null, // extractionTaskId 在更新时不应该被修改
            null, // contractId 在更新时不应该被修改
            dto.getClauseType(),
            clauseTitle,
            clauseContent,
            dto.getClausePosition(),
            dto.getConfidenceScore(),
            dto.getExtractedEntities(),
            dto.getRiskLevel(),
            dto.getRiskFactors(),
            dto.getCreatedTime()
        );
    }

    /**
     * 查询DTO转换为过滤器
     *
     * @param queryDTO 查询DTO
     * @return 过滤器
     */
    @Mapping(target = "includeDeleted", constant = "false")
    ClauseFilters toFilters(ClauseQueryDTO queryDTO);

    /**
     * 领域模型列表转换为DTO列表
     *
     * @param clauses 领域模型列表
     * @return DTO列表
     */
    List<ClauseDTO> toDTOList(List<Clause> clauses);
}