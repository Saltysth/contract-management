package com.contract.management.infrastructure.repository;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.contract.management.domain.model.Clause;
import com.contract.management.domain.model.ClauseId;
import com.contract.management.domain.model.ClauseType;
import com.contract.management.domain.model.RiskLevel;
import com.contract.management.domain.repository.ClauseFilters;
import com.contract.management.domain.repository.ClauseRepository;
import com.contract.management.infrastructure.converter.ClauseConverter;
import com.contract.management.infrastructure.entity.ClauseEntity;
import com.contract.management.infrastructure.mapper.ClauseMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

/**
 * 条款仓储实现类
 *
 * @author SaltyFish
 * @since 1.0.0
 */
@Slf4j
@Repository
@RequiredArgsConstructor
public class ClauseRepositoryImpl implements ClauseRepository {

    private final ClauseMapper clauseMapper;

    @Override
    @Transactional
    public Clause save(Clause clause) {
        ClauseEntity entity = ClauseConverter.toEntity(clause);
        clauseMapper.insert(entity);
        return ClauseConverter.toDomain(entity);
    }

    @Override
    @Transactional
    public Clause update(Clause clause) {
        // 先查询现有实体，保留版本号
        ClauseEntity existingEntity = clauseMapper.selectById(clause.getId().getValue());
        if (existingEntity == null) {
            throw new RuntimeException("条款不存在");
        }

        // 转换为实体并保留版本号
        ClauseEntity entity = ClauseConverter.toEntity(clause);
        entity.setObjectVersionNumber(existingEntity.getObjectVersionNumber());

        clauseMapper.updateById(entity);
        return ClauseConverter.toDomain(entity);
    }

    @Override
    public Optional<Clause> findById(ClauseId id) {
        if (id == null) {
            return Optional.empty();
        }
        ClauseEntity entity = clauseMapper.findById(id.getValue());
        return Optional.ofNullable(ClauseConverter.toDomain(entity));
    }

    @Override
    @Transactional
    public void delete(ClauseId id) {
        if (id != null) {
            clauseMapper.deleteById(id.getValue());
        }
    }

    @Override
    public boolean existsById(ClauseId id) {
        if (id == null) {
            return false;
        }
        return clauseMapper.selectById(id.getValue()) != null;
    }

    
    @Override
    public List<Clause> findByExtractionTaskId(Long extractionTaskId) {
        if (extractionTaskId == null) {
            return List.of();
        }
        List<ClauseEntity> entities = clauseMapper.findByExtractionTaskId(extractionTaskId);
        return ClauseConverter.toDomainList(entities);
    }

    @Override
    public List<Clause> findByContractId(Long contractId) {
        if (contractId == null) {
            return List.of();
        }
        List<ClauseEntity> entities = clauseMapper.findByContractId(contractId);
        return ClauseConverter.toDomainList(entities);
    }

    @Override
    public org.springframework.data.domain.Page<Clause> findByContractId(Long contractId, Pageable pageable) {
        if (contractId == null) {
            return new PageImpl<>(List.of(), pageable, 0);
        }

        QueryWrapper<ClauseEntity> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("contract_id", contractId)
                   .eq("is_deleted", false)
                   .orderByDesc("created_time");

        Page<ClauseEntity> page = new Page<>(pageable.getPageNumber() + 1, pageable.getPageSize());
        IPage<ClauseEntity> result = clauseMapper.selectPage(page, queryWrapper);

        List<Clause> clauses = ClauseConverter.toDomainList(result.getRecords());
        return new PageImpl<>(clauses, pageable, result.getTotal());
    }

    @Override
    public List<Clause> findByClauseType(ClauseType clauseType) {
        if (clauseType == null) {
            return List.of();
        }
        List<ClauseEntity> entities = clauseMapper.findByClauseType(clauseType.name());
        return ClauseConverter.toDomainList(entities);
    }

    @Override
    public List<Clause> findByRiskLevel(RiskLevel riskLevel) {
        if (riskLevel == null) {
            return List.of();
        }
        List<ClauseEntity> entities = clauseMapper.findByRiskLevel(riskLevel.name());
        return ClauseConverter.toDomainList(entities);
    }

    @Override
    public List<Clause> findHighRiskClauses() {
        List<ClauseEntity> entities = clauseMapper.findHighRiskClauses();
        return ClauseConverter.toDomainList(entities);
    }

    @Override
    public org.springframework.data.domain.Page<Clause> findByFilters(ClauseFilters filters, Pageable pageable) {
        if (filters == null || filters.isEmpty()) {
            return new PageImpl<>(List.of(), pageable, 0);
        }

        Page<ClauseEntity> page = new Page<>(pageable.getPageNumber() + 1, pageable.getPageSize());
        IPage<ClauseEntity> result = clauseMapper.selectByFilters(
            page,
            filters.getExtractionTaskId(),
            filters.getContractId(),
            filters.getClauseType() != null ? filters.getClauseType().name() : null,
            filters.getRiskLevel() != null ? filters.getRiskLevel().name() : null,
            filters.getTitleKeyword(),
            filters.getContentKeyword(),
            filters.getMinConfidenceScore(),
            filters.getMaxConfidenceScore(),
            filters.getHighRiskOnly(),
            filters.getHasRiskFactorsOnly(),
            filters.getCreatedTimeStart(),
            filters.getCreatedTimeEnd(),
            filters.getCreatedBy()
        );

        List<Clause> clauses = ClauseConverter.toDomainList(result.getRecords());
        return new PageImpl<>(clauses, pageable, result.getTotal());
    }

    @Override
    public List<Clause> findByFilters(ClauseFilters filters) {
        if (filters == null || filters.isEmpty()) {
            return List.of();
        }

        // 使用分页查询获取所有结果
        Pageable pageable = org.springframework.data.domain.PageRequest.of(0, Integer.MAX_VALUE);
        org.springframework.data.domain.Page<Clause> page = findByFilters(filters, pageable);
        return page.getContent();
    }


    @Override
    public long countByContractId(Long contractId) {
        if (contractId == null) {
            return 0;
        }
        return clauseMapper.countByContractId(contractId);
    }

    @Override
    public long countByClauseType(ClauseType clauseType) {
        if (clauseType == null) {
            return 0;
        }
        return clauseMapper.countByClauseType(clauseType.name());
    }

    @Override
    public long countByRiskLevel(RiskLevel riskLevel) {
        if (riskLevel == null) {
            return 0;
        }
        return clauseMapper.countByRiskLevel(riskLevel.name());
    }

    @Override
    public Map<ClauseType, Long> getClauseTypeStatistics() {
        List<Map<String, Object>> results = clauseMapper.getClauseTypeStatistics();
        Map<ClauseType, Long> statistics = new EnumMap<>(ClauseType.class);

        for (Map<String, Object> result : results) {
            String typeStr = (String) result.get("type");
            Long count = ((Number) result.get("count")).longValue();

            if (typeStr != null) {
                try {
                    ClauseType type = ClauseType.valueOf(typeStr);
                    statistics.put(type, count);
                } catch (IllegalArgumentException e) {
                    log.warn("未知的条款类型: {}", typeStr);
                }
            }
        }

        return statistics;
    }

    @Override
    public Map<RiskLevel, Long> getRiskLevelStatistics() {
        List<Map<String, Object>> results = clauseMapper.getRiskLevelStatistics();
        Map<RiskLevel, Long> statistics = new EnumMap<>(RiskLevel.class);

        for (Map<String, Object> result : results) {
            String levelStr = (String) result.get("level");
            Long count = ((Number) result.get("count")).longValue();

            if (levelStr != null) {
                try {
                    RiskLevel level = RiskLevel.valueOf(levelStr);
                    statistics.put(level, count);
                } catch (IllegalArgumentException e) {
                    log.warn("未知的风险等级: {}", levelStr);
                }
            }
        }

        return statistics;
    }

    @Override
    public List<Clause> findClausesNeedingReview() {
        List<ClauseEntity> entities = clauseMapper.findClausesNeedingReview();
        return ClauseConverter.toDomainList(entities);
    }

    @Override
    public List<Clause> findByContentKeyword(String keyword) {
        if (keyword == null || keyword.trim().isEmpty()) {
            return List.of();
        }
        List<ClauseEntity> entities = clauseMapper.findByContentKeyword(keyword.trim());
        return ClauseConverter.toDomainList(entities);
    }

    @Override
    @Transactional
    public List<Clause> saveAll(List<Clause> clauses) {
        if (clauses == null || clauses.isEmpty()) {
            return List.of();
        }

        List<ClauseEntity> entities = ClauseConverter.toEntityList(clauses);
        // 批量插入
        for (ClauseEntity entity : entities) {
            clauseMapper.insert(entity);
        }

        return ClauseConverter.toDomainList(entities);
    }

    @Override
    @Transactional
    public void deleteAll(List<ClauseId> clauseIds) {
        if (clauseIds == null || clauseIds.isEmpty()) {
            return;
        }

        for (ClauseId id : clauseIds) {
            if (id != null) {
                clauseMapper.deleteById(id.getValue());
            }
        }
    }

    
    @Override
    @Transactional
    public void deleteAllByExtractionTaskId(Long extractionTaskId) {
        if (extractionTaskId != null) {
            clauseMapper.deleteAllByExtractionTaskId(extractionTaskId);
        }
    }

    @Override
    @Transactional
    public void deleteAllByContractId(Long contractId) {
        if (contractId != null) {
            clauseMapper.deleteAllByContractId(contractId);
        }
    }
}