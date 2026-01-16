package com.contract.management.domain.event;

import com.contract.management.domain.model.ContractId;
import lombok.Getter;
import lombok.ToString;

import java.time.LocalDateTime;

/**
 * 合同创建事件
 *
 * @author SaltyFish
 * @since 1.0.0
 */
@Getter
@ToString
public class ContractCreatedEvent {
    
    private final ContractId contractId;
    private final String contractName;
    private final String contractType;
    private final String attachmentUuid;
    private final Long createdBy;
    private final LocalDateTime timestamp;

    public ContractCreatedEvent(ContractId contractId, String contractName,
                               String contractType, String attachmentUuid, Long createdBy) {
        this.contractId = contractId;
        this.contractName = contractName;
        this.contractType = contractType;
        this.attachmentUuid = attachmentUuid;
        this.createdBy = createdBy;
        this.timestamp = LocalDateTime.now();
    }
    
    /**
     * 获取事件类型
     */
    public String getEventType() {
        return "ContractCreated";
    }

    /**
     * 获取合同ID的Long值，便于外部访问
     */
    public Long getContractId() {
        return contractId.getValue();
    }
}