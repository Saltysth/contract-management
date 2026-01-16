package com.contract.management.infrastructure.repository;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.contract.common.constant.ContractType;
import com.contract.management.domain.model.Contract;
import com.contract.management.domain.model.ContractId;
import com.contract.management.domain.model.valueobject.ContractName;
import com.contract.management.domain.repository.ContractFilters;
import com.contract.management.domain.repository.ContractRepository;
import com.contract.management.infrastructure.converter.ContractConverter;
import com.contract.management.infrastructure.entity.ContractEntity;
import com.contract.management.infrastructure.mapper.ContractMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 合同仓储实现类 - MyBatis Plus版本
 *
 * @author SaltyFish
 * @since 1.0.0
 */
@Slf4j
@Repository
@RequiredArgsConstructor
public class ContractRepositoryImpl implements ContractRepository {


    private final ContractMapper contractMapper;

    @Override
    @Transactional
    public Contract save(Contract contract) {
        ContractEntity entity = ContractConverter.toEntity(contract);
        contractMapper.insert(entity);

        return ContractConverter.toDomain(entity);
    }

    @Override
    public boolean markDeleted(ContractName contractName, ContractId id, Long operatorId) {
        int rows = contractMapper.markDeletedById(
            contractName.getValue(),
            id.getValue(),
            operatorId,
            LocalDateTime.now()
        );
        if (rows == 0) {
            log.warn("标记逻辑删除失败或已删除: id={}", id.getValue());
            return false;
        } else {
            return true;
        }
    }

    @Override
    public void restoreDeleted(ContractId id, Long operatorId) {
        int rows = contractMapper.restoreDeletedById(
            id.getValue(),
            operatorId,
            LocalDateTime.now()
        );
        if (rows == 0) {
            log.warn("恢复逻辑删除失败或未被删除: id={}", id.getValue());
        }
    }

    @Override
    @Transactional
    public Contract update(Contract contract) {
        // 先查询现有实体，保留版本号
        ContractEntity existingEntity = contractMapper.selectById(contract.getId().getValue());
        if (existingEntity == null) {
            throw new RuntimeException("合同不存在");
        }

        // 转换为实体并保留版本号
        ContractEntity entity = ContractConverter.toEntity(contract);
        entity.setObjectVersionNumber(existingEntity.getObjectVersionNumber());

        contractMapper.updateById(entity);
        return ContractConverter.toDomain(entity);
    }

    @Override
    public Optional<Contract> findById(ContractId id) {
        ContractEntity entity = contractMapper.selectById(id.getValue());
        return Optional.ofNullable(ContractConverter.toDomain(entity));
    }

    @Override
    @Transactional
    public void delete(ContractId id) {
        contractMapper.deleteById(id.getValue());
    }

    @Override
    public boolean existsById(ContractId id) {
        return contractMapper.selectById(id.getValue()) != null;
    }

    @Override
    public org.springframework.data.domain.Page<Contract> findByFilters(ContractFilters filters, Pageable pageable) {
        // 构建MyBatis Plus分页对象
        Page<ContractEntity> page = new Page<>(pageable.getPageNumber() + 1, pageable.getPageSize());
        
        // 构建查询条件
        QueryWrapper<ContractEntity> queryWrapper = new QueryWrapper<>();
        
        // 根据条件构建查询
        if (filters.getContractTypes() != null && !filters.getContractTypes().isEmpty()) {
            queryWrapper.in("contract_type", filters.getContractTypes().stream()
                    .map(Enum::name).collect(Collectors.toList()));
        }
                
        if (filters.getContractName() != null && !filters.getContractName().trim().isEmpty()) {
            queryWrapper.like("contract_name", filters.getContractName());
        }
        if (filters.getPartyAName() != null && !filters.getPartyAName().trim().isEmpty()) {
            queryWrapper.like("party_a_name", filters.getPartyAName());
        }
        if (filters.getPartyBName() != null && !filters.getPartyBName().trim().isEmpty()) {
            queryWrapper.like("party_b_name", filters.getPartyBName());
        }
        
        // 金额范围查询
        if (filters.getMinAmount() != null) {
            queryWrapper.ge("contract_amount", filters.getMinAmount());
        }
        if (filters.getMaxAmount() != null) {
            queryWrapper.le("contract_amount", filters.getMaxAmount());
        }
        
        // 日期范围查询
        if (filters.getSignDateStart() != null) {
            queryWrapper.ge("sign_date", filters.getSignDateStart());
        }
        if (filters.getSignDateEnd() != null) {
            queryWrapper.le("sign_date", filters.getSignDateEnd());
        }
        
        if (filters.getCreatedBy() != null) {
            queryWrapper.eq("created_by", filters.getCreatedBy());
        }
        
        // 是否包含已删除记录
        if (filters.getIncludeDeleted() == null || !filters.getIncludeDeleted()) {
            queryWrapper.eq("is_deleted", false);
        }
        
        // 执行分页查询
        IPage<ContractEntity> result = contractMapper.selectPage(page, queryWrapper);
        
        // 转换为领域模型
        List<Contract> contracts = ContractConverter.toDomainList(result.getRecords());
        
        return new PageImpl<>(contracts, pageable, result.getTotal());
    }

    @Override
    public List<Contract> findByType(ContractType type) {
        QueryWrapper<ContractEntity> wrapper = new QueryWrapper<>();
        wrapper.eq("contract_type", type.name())
               .eq("is_deleted", false);
        
        List<ContractEntity> entities = contractMapper.selectList(wrapper);
        return ContractConverter.toDomainList(entities);
    }

    
    @Override
    public List<Contract> findByCreatedBy(Long userId) {
        QueryWrapper<ContractEntity> wrapper = new QueryWrapper<>();
        wrapper.eq("created_by", userId)
               .eq("is_deleted", false);
        
        List<ContractEntity> entities = contractMapper.selectList(wrapper);
        return ContractConverter.toDomainList(entities);
    }

    @Override
    public List<Contract> findExpiringContracts(LocalDate beforeDate) {
        List<ContractEntity> entities = contractMapper.findExpiringContracts(beforeDate);
        return ContractConverter.toDomainList(entities);
    }

    
    @Override
    public Optional<Contract> findByAttachmentUuid(String attachmentUuid) {
        QueryWrapper<ContractEntity> wrapper = new QueryWrapper<>();
        wrapper.eq("attachment_uuid", attachmentUuid)
               .eq("is_deleted", false);
        
        ContractEntity entity = contractMapper.selectOne(wrapper);
        return Optional.ofNullable(ContractConverter.toDomain(entity));
    }

    @Override
    public long countByType(ContractType type) {
        QueryWrapper<ContractEntity> wrapper = new QueryWrapper<>();
        wrapper.eq("contract_type", type.name())
               .eq("is_deleted", false);
        
        return contractMapper.selectCount(wrapper);
    }

    
    @Override
    public long countByCreatedBy(Long userId) {
        QueryWrapper<ContractEntity> wrapper = new QueryWrapper<>();
        wrapper.eq("created_by", userId)
               .eq("is_deleted", false);
        
        return contractMapper.selectCount(wrapper);
    }

    @Override
    public Map<ContractType, Long> getTypeStatistics() {
        List<Map<String, Object>> results = contractMapper.getTypeStatistics();
        
        return results.stream()
                .collect(Collectors.toMap(
                    result -> ContractType.valueOf((String) result.get("type")),
                    result -> ((Number) result.get("count")).longValue()
                ));
    }


    @Override
    public boolean existsByNameAndCreatedBy(String contractName, Long createdBy) {
        return contractMapper.countByNameAndCreatedBy(contractName, createdBy) > 0;
    }

    @Override
    public boolean existsByNameAndCreatedByAndIdNot(String contractName, Long createdBy, ContractId excludeContractId) {
        return contractMapper.countByNameAndCreatedByAndIdNot(contractName, createdBy, excludeContractId.getValue()) > 0;
    }

    @Override
    public List<Contract> findContractsNeedingAttention() {
        List<ContractEntity> entities = contractMapper.findContractsNeedingAttention();
        return ContractConverter.toDomainList(entities);
    }


    @Override
    @Transactional
    public List<Contract> saveAll(List<Contract> contracts) {
        List<ContractEntity> entities = ContractConverter.toEntityList(contracts);
        List<ContractEntity> resultEntities = new ArrayList<>();

        // 批量保存
        for (ContractEntity entity : entities) {
            if (entity.getId() == null) {
                // 新增
                contractMapper.insert(entity);
                resultEntities.add(entity);
            } else {
                // 更新 - 先查询保留版本号
                ContractEntity existingEntity = contractMapper.selectById(entity.getId());
                if (existingEntity != null) {
                    entity.setObjectVersionNumber(existingEntity.getObjectVersionNumber());
                    contractMapper.updateById(entity);
                    resultEntities.add(entity);
                } else {
                    throw new RuntimeException("合同不存在，ID: " + entity.getId());
                }
            }
        }

        return ContractConverter.toDomainList(resultEntities);
    }

    @Override
    @Transactional
    public void deleteAll(List<ContractId> contractIds) {
        List<Long> ids = contractIds.stream()
                .map(ContractId::getValue)
                .collect(Collectors.toList());
        
        contractMapper.deleteBatchIds(ids);
    }
    
    @Override
    public List<Contract> findByFilters(ContractFilters filters) {
        // 构建查询条件
        QueryWrapper<ContractEntity> queryWrapper = new QueryWrapper<>();
        
        // 根据条件构建查询
        if (filters.getContractTypes() != null && !filters.getContractTypes().isEmpty()) {
            queryWrapper.in("contract_type", filters.getContractTypes().stream()
                    .map(Enum::name).collect(Collectors.toList()));
        }
                
        if (filters.getContractName() != null && !filters.getContractName().trim().isEmpty()) {
            queryWrapper.like("contract_name", filters.getContractName());
        }
        if (filters.getPartyAName() != null && !filters.getPartyAName().trim().isEmpty()) {
            queryWrapper.like("party_a_name", filters.getPartyAName());
        }
        if (filters.getPartyBName() != null && !filters.getPartyBName().trim().isEmpty()) {
            queryWrapper.like("party_b_name", filters.getPartyBName());
        }
        
        // 金额范围查询
        if (filters.getMinAmount() != null) {
            queryWrapper.ge("contract_amount", filters.getMinAmount());
        }
        if (filters.getMaxAmount() != null) {
            queryWrapper.le("contract_amount", filters.getMaxAmount());
        }
        
        // 日期范围查询
        if (filters.getSignDateStart() != null) {
            queryWrapper.ge("sign_date", filters.getSignDateStart());
        }
        if (filters.getSignDateEnd() != null) {
            queryWrapper.le("sign_date", filters.getSignDateEnd());
        }
        
        if (filters.getCreatedBy() != null) {
            queryWrapper.eq("created_by", filters.getCreatedBy());
        }
        
        // 是否包含已删除记录
        if (filters.getIncludeDeleted() == null || !filters.getIncludeDeleted()) {
            queryWrapper.eq("is_deleted", false);
        }
        
        // 执行查询
        List<ContractEntity> entities = contractMapper.selectList(queryWrapper);
        
        // 转换为领域模型
        return ContractConverter.toDomainList(entities);
    }
}