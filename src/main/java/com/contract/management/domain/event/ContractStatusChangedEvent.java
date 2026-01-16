package com.contract.management.domain.event;

import com.contract.common.constant.ContractStatus;
import com.contract.management.domain.model.ContractId;
import lombok.Getter;
import lombok.ToString;

import java.time.LocalDateTime;

/**
 * 合同状态变更事件
 *
 * @author SaltyFish
 * @since 1.0.0
 */
@Getter
@ToString
public class ContractStatusChangedEvent {
    
    private final ContractId contractId;
    private final String contractName;
    private final ContractStatus oldStatus;
    private final ContractStatus newStatus;
    private final Long changedBy;
    private final LocalDateTime timestamp;
    
    public ContractStatusChangedEvent(ContractId contractId, String contractName,
                                     ContractStatus oldStatus, ContractStatus newStatus, 
                                     Long changedBy) {
        this.contractId = contractId;
        this.contractName = contractName;
        this.oldStatus = oldStatus;
        this.newStatus = newStatus;
        this.changedBy = changedBy;
        this.timestamp = LocalDateTime.now();
    }
    
    /**
     * 获取事件类型
     */
    public String getEventType() {
        return "ContractStatusChanged";
    }
}