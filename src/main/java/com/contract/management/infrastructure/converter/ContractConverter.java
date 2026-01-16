package com.contract.management.infrastructure.converter;

import com.contract.management.application.dto.ContractQueryDTO;
import com.contract.management.domain.model.valueobject.AuditInfo;
import com.contract.management.domain.model.valueobject.ContractName;
import com.contract.management.domain.model.valueobject.ContractPeriod;
import com.contract.management.domain.model.valueobject.Money;
import com.contract.management.domain.model.valueobject.PartyInfo;
import com.contract.common.constant.ContractType;
import com.contract.management.domain.model.Contract;
import com.contract.management.domain.model.ContractId;
import com.contract.management.infrastructure.entity.ContractEntity;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 合同领域模型与实体转换器
 *
 * @author SaltyFish
 * @since 1.0.0
 */
public class ContractConverter {

    /**
     * 实体转领域模型
     *
     * @param entity 实体
     * @return 领域模型
     */
    public static Contract toDomain(ContractEntity entity) {
        if (entity == null) {
            return null;
        }

        // 构建值对象
        ContractId contractId = new ContractId(entity.getId());
        ContractName contractName = new ContractName(entity.getContractName());
        ContractType contractType = entity.getContractType() != null ? ContractType.valueOf(entity.getContractType()) : null;

        PartyInfo partyA = new PartyInfo(
            entity.getPartyAName(),
            entity.getPartyAContact(),
            entity.getPartyAAddress()
        );

        PartyInfo partyB = new PartyInfo(
            entity.getPartyBName(),
            entity.getPartyBContact(),
            entity.getPartyBAddress()
        );

        Money contractAmount = null;
        if (entity.getContractAmount() != null) {
            contractAmount = Money.ofCNY(entity.getContractAmount());
        }

        ContractPeriod period = null;
        if (entity.getSignDate() != null || entity.getEffectiveDate() != null || entity.getExpiryDate() != null) {
            period = new ContractPeriod(
                entity.getSignDate(),
                entity.getEffectiveDate(),
                entity.getExpiryDate()
            );
        }

        AuditInfo auditInfo = new AuditInfo(
            entity.getCreatedBy(),
            entity.getCreatedTime(),
            entity.getUpdatedTime(),
            entity.getUpdatedBy(),
            entity.getObjectVersionNumber()
        );

        // 使用重建构造函数
        return new Contract(
            contractId,
            contractName,
            contractType,
            partyA,
            partyB,
            contractAmount,
            period,
            entity.getAttachmentUuid(),
            entity.getDescription(),
            auditInfo
        );
    }

    /**
     * 领域模型转实体
     *
     * @param contract 领域模型
     * @return 实体
     */
    public static ContractEntity toEntity(Contract contract) {
        if (contract == null) {
            return null;
        }

        ContractEntity entity = new ContractEntity();
        
        // 基本信息
        if (contract.getId() != null) {
            entity.setId(contract.getId().getValue());
        }
        entity.setContractName(contract.getContractName().getValue());
        if (contract.getContractType() != null) {
            entity.setContractType(contract.getContractType().name());
        }

        // 当事方信息
        if (contract.getPartyA() != null) {
            entity.setPartyAName(contract.getPartyA().getName());
            entity.setPartyAContact(contract.getPartyA().getContact());
            entity.setPartyAAddress(contract.getPartyA().getAddress());
        }
        
        if (contract.getPartyB() != null) {
            entity.setPartyBName(contract.getPartyB().getName());
            entity.setPartyBContact(contract.getPartyB().getContact());
            entity.setPartyBAddress(contract.getPartyB().getAddress());
        }
        
        // 金额信息
        if (contract.getContractAmount() != null) {
            entity.setContractAmount(contract.getContractAmount().getAmount());
        }
        
        // 期限信息
        if (contract.getPeriod() != null) {
            entity.setSignDate(contract.getPeriod().getSignDate());
            entity.setEffectiveDate(contract.getPeriod().getEffectiveDate());
            entity.setExpiryDate(contract.getPeriod().getExpiryDate());
        }
        
        // 其他信息
        entity.setAttachmentUuid(contract.getAttachmentUuid());
        entity.setDescription(contract.getDescription());
        entity.setIsDeleted(contract.getIsDeleted()); // 默认未删除，逻辑删除由MyBatis Plus处理

        // 审计信息
        if (contract.getAuditInfo() != null) {
            entity.setCreatedBy(contract.getAuditInfo().getCreatedBy());
            entity.setUpdatedBy(contract.getAuditInfo().getUpdatedBy());
            entity.setCreatedTime(contract.getAuditInfo().getCreatedTime());
            entity.setUpdatedTime(contract.getAuditInfo().getUpdatedTime());
            entity.setObjectVersionNumber(contract.getAuditInfo().getObjectVersionNumber());
        }

        return entity;
    }

    /**
     * 批量转换实体列表为领域模型列表
     *
     * @param entities 实体列表
     * @return 领域模型列表
     */
    public static List<Contract> toDomainList(List<ContractEntity> entities) {
        if (entities == null || entities.isEmpty()) {
            return List.of();
        }
        return entities.stream()
                .map(ContractConverter::toDomain)
                .collect(Collectors.toList());
    }

    /**
     * 批量转换领域模型列表为实体列表
     *
     * @param contracts 领域模型列表
     * @return 实体列表
     */
    public static List<ContractEntity> toEntityList(List<Contract> contracts) {
        if (contracts == null || contracts.isEmpty()) {
            return List.of();
        }
        return contracts.stream()
                .map(ContractConverter::toEntity)
                .collect(Collectors.toList());
    }
}