package com.contract.management.application.convertor;

import com.contract.management.application.dto.ContractDTO;
import com.contract.management.application.dto.ContractQueryDTO;
import com.contract.management.domain.model.Contract;
import com.contract.management.domain.model.ContractId;
import com.contract.management.domain.model.valueobject.AuditInfo;
import com.contract.management.domain.model.valueobject.ContractName;
import com.contract.management.domain.model.valueobject.ContractPeriod;
import com.contract.management.domain.model.valueobject.Money;
import com.contract.management.domain.model.valueobject.PartyInfo;
import com.contract.management.domain.repository.ContractFilters;
import org.mapstruct.*;
import org.mapstruct.factory.Mappers;

import java.util.List;

/**
 * 合同应用层转换器 - MapStruct实现
 * 负责领域模型与应用DTO之间的转换
 *
 * @author SaltyFish
 * @since 1.0.0
 */
@Mapper(componentModel = "spring")
public interface ContractApplicationConvertor {
    
    ContractApplicationConvertor INSTANCE = Mappers.getMapper(ContractApplicationConvertor.class);
    
    /**
     * 领域模型转换为DTO
     *
     * @param contract 领域模型
     * @return DTO
     */
    @Mapping(source = "id.value", target = "id")
    @Mapping(source = "contractName.value", target = "contractName")
    @Mapping(source = "contractAmount.amount", target = "contractAmount")
    @Mapping(source = "period.signDate", target = "signDate")
    @Mapping(source = "period.effectiveDate", target = "effectiveDate")
    @Mapping(source = "period.expiryDate", target = "expiryDate")
    @Mapping(source = "auditInfo.createdBy", target = "createdBy")
    @Mapping(source = "auditInfo.updatedBy", target = "updatedBy")
    @Mapping(source = "auditInfo.createdTime", target = "createdTime")
    @Mapping(source = "auditInfo.updatedTime", target = "updatedTime")
    @Mapping(source = "auditInfo.objectVersionNumber", target = "objectVersionNumber")
    ContractDTO toDTO(Contract contract);
    
    /**
     * DTO转换为领域模型（用于创建）
     * 由于Contract是不可变对象，使用手动转换
     *
     * @param dto DTO
     * @return 领域模型
     */
    default Contract toDomain(ContractDTO dto) {
        if (dto == null) {
            return null;
        }
        
        ContractId id = dto.getId() != null ? ContractId.of(dto.getId()) : null;
        ContractName contractName = new ContractName(dto.getContractName());
        Money contractAmount = dto.getContractAmount() != null ? Money.ofCNY(dto.getContractAmount()) : null;
        ContractPeriod period = new ContractPeriod(dto.getSignDate(), dto.getEffectiveDate(), dto.getExpiryDate());

        PartyInfo partyA = dto.getPartyA() != null ? toPartyInfo(dto.getPartyA()) : null;
        PartyInfo partyB = dto.getPartyB() != null ? toPartyInfo(dto.getPartyB()) : null;
        
        AuditInfo auditInfo;
        if (dto.getId() != null && dto.getCreatedTime() != null) {
            // 构造函数参数顺序: createdBy, createdTime, updatedTime, updatedBy, objectVersionNumber
            auditInfo = new AuditInfo(
                dto.getCreatedBy() != null ? dto.getCreatedBy() : 1L,
                dto.getCreatedTime(),
                dto.getUpdatedTime(),
                dto.getUpdatedBy(),
                dto.getObjectVersionNumber() != null ? dto.getObjectVersionNumber() : 1L
            );
        } else {
            auditInfo = AuditInfo.create(dto.getCreatedBy() != null ? dto.getCreatedBy() : 1L);
        }
        
        // 根据Contract的构造函数创建对象
        if (dto.getId() != null) {
            // 更新现有合同 - 使用完整构造函数
            return new Contract(
                id,
                contractName,
                dto.getContractType(),
                partyA,
                partyB,
                contractAmount,
                period,
                dto.getAttachmentUuid(),
                dto.getDescription(),
                auditInfo
            );
        } else {
            // 创建新合同 - 使用创建构造函数
            return new Contract(
                id,
                contractName,
                dto.getContractType(),
                partyA,
                partyB,
                contractAmount,
                period,
                dto.getAttachmentUuid(),
                dto.getDescription(),
                dto.getCreatedBy() != null ? dto.getCreatedBy() : 1L
            );
        }
    }
    
    /**
     * PartyInfo值对象转换为DTO
     */
    @Mapping(source = "name", target = "companyName")
    @Mapping(source = "contact", target = "contactInfo")
    ContractDTO.PartyInfoDTO toPartyInfoDTO(PartyInfo partyInfo);
    
    /**
     * PartyInfoDTO转换为值对象
     */
    @Mapping(source = "companyName", target = "name")
    @Mapping(source = "contactInfo", target = "contact")
    PartyInfo toPartyInfo(ContractDTO.PartyInfoDTO dto);
    
    /**
     * 批量转换 - 领域模型转DTO
     */
    List<ContractDTO> toDTOList(List<Contract> contracts);
    
    /**
     * 批量转换 - DTO转领域模型
     */
    List<Contract> toDomainList(List<ContractDTO> dtos);
    
    /**
     * 查询过滤器转换
     */
    @Mapping(target = "contractTypes", source = "contractTypes")
    @Mapping(target = "contractName", source = "contractName")
    @Mapping(target = "partyAName", source = "partyAName")
    @Mapping(target = "partyBName", source = "partyBName")
    @Mapping(target = "minAmount", source = "minAmount")
    @Mapping(target = "maxAmount", source = "maxAmount")
    @Mapping(target = "signDateStart", source = "signDateStart")
    @Mapping(target = "signDateEnd", source = "signDateEnd")
    @Mapping(target = "effectiveDateStart", source = "effectiveDateStart")
    @Mapping(target = "effectiveDateEnd", source = "effectiveDateEnd")
    @Mapping(target = "expiryDateStart", source = "expiryDateStart")
    @Mapping(target = "expiryDateEnd", source = "expiryDateEnd")
    @Mapping(target = "createdBy", source = "createdBy")
    @Mapping(target = "includeDeleted", source = "includeDeleted")
    ContractFilters toFilters(ContractQueryDTO queryDTO);
}