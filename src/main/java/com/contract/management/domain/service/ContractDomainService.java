package com.contract.management.domain.service;

import com.contract.management.domain.event.ContractCreatedEvent;
import com.contract.management.domain.model.Contract;
import com.contract.management.domain.model.ContractId;
import com.contract.management.domain.model.valueobject.ContractName;
import com.contract.management.domain.repository.ContractFilters;
import com.contract.management.domain.repository.ContractRepository;
import com.contract.management.infrastructure.messaging.ContractEventPublisher;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Optional;

/**
 * 合同领域服务
 *
 * @author SaltyFish
 * @since 1.0.0
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class ContractDomainService {

    private final ContractRepository contractRepository;
    private final ContractValidationService contractValidationService;
    private final ContractEventPublisher eventPublisher;
    
    /**
     * 验证合同名称唯一性
     *
     * @param contractName 合同名称
     * @param createdBy 创建人ID
     * @throws ContractDomainException 如果名称已存在
     */
    public String getValidatedContractNameUniqueness(String contractName, Long createdBy) {
        if (contractRepository.existsByNameAndCreatedBy(contractName, createdBy)) {
            log.warn("DUPLICATE_CONTRACT_NAME", "合同名称已存在: " + contractName);
            return contractName + "_" + String.valueOf(System.currentTimeMillis()).substring(0, 4);
        }
        return contractName;
    }
    
    /**
     * 验证合同名称唯一性（排除指定合同）
     *
     * @param contractName 合同名称
     * @param createdBy 创建人ID
     * @param excludeContractId 排除的合同ID
     * @throws ContractDomainException 如果名称已存在
     */
    public void validateContractNameUniqueness(String contractName, Long createdBy, ContractId excludeContractId) {
        if (contractRepository.existsByNameAndCreatedByAndIdNot(contractName, createdBy, excludeContractId)) {
            log.error("合同名称重复: contractName={}, createdBy={}, excludeId={}",
                contractName, createdBy, excludeContractId.getValue());
            throw new ContractDomainException("DUPLICATE_CONTRACT_NAME",
                "合同名称已存在: " + contractName);
        }
    }


    // ========== 领域服务对外编排能力（封装仓储访问） ==========
    
    public Optional<Contract> findById(ContractId id) {
        return contractRepository.findById(id);
    }
    
    public boolean existsById(ContractId id) {
        return contractRepository.existsById(id);
    }
    
    /**
     * 创建合同：负责创建前的领域校验并持久化
     */
    public Contract createContract(Contract contract) {
        String contractName =
            getValidatedContractNameUniqueness(contract.getContractName().getValue(),
                contract.getAuditInfo().getCreatedBy());
        contract.setContractName(ContractName.of(contractName));

        // 保存合同
        Contract savedContract = contractRepository.save(contract);

        // 发布合同创建事件
        ContractCreatedEvent event = new ContractCreatedEvent(
            savedContract.getId(),
            savedContract.getContractName().getValue(),
            savedContract.getContractType() != null ? savedContract.getContractType().name() : null,
            savedContract.getAttachmentUuid(),
            savedContract.getAuditInfo().getCreatedBy()
        );
        eventPublisher.publishContractCreated(event);

        return savedContract;
    }
    
    /**
     * 更新合同：负责加载旧聚合、编辑性校验、名称唯一性（排除自身）并持久化
     */
    public Contract updateContract(Contract contract) {
        ContractId id = contract.getId();
        if (id == null) {
            throw new IllegalArgumentException("更新合同时ID不能为空");
        }
        Contract old = contractRepository.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("合同不存在: " + id.getValue()));
        validateContractNameUniqueness(contract.getContractName().getValue(), contract.getAuditInfo().getCreatedBy(), id);
        return contractRepository.update(contract);
    }
    
    /**
     * 逻辑删除合同：加载聚合、删除规则校验、标记删除并持久化（仅更新 is_deleted）
     *
     */
    public boolean deleteContract(ContractId id) {
        Contract contract = contractRepository.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("合同不存在: " + id.getValue()));
        // 在聚合上打删除标记，保持内存状态一致
        contract.delete();
        // 仅更新 is_deleted，避免普通更新路径覆盖删除标记
        Long operatorId = 1L;
        return contractRepository.markDeleted(contract.getContractName(), id, operatorId);
    }
    
    public org.springframework.data.domain.Page<Contract> findByFilters(
            ContractFilters filters,
            org.springframework.data.domain.Pageable pageable) {
        return contractRepository.findByFilters(filters, pageable);
    }
    
    public java.util.List<Contract> findByFilters(
            ContractFilters filters) {
        return contractRepository.findByFilters(filters);
    }
}