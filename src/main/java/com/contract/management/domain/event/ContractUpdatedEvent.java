package com.contract.management.domain.event;

import com.contract.management.domain.model.ContractId;
import lombok.Getter;
import lombok.ToString;

import java.time.LocalDateTime;

/**
 * 合同更新事件
 *
 * @author SaltyFish
 * @since 1.0.0
 */
@Getter
@ToString
public class ContractUpdatedEvent {
    
    private final ContractId contractId;
    private final String contractName;
    private final Long updatedBy;
    private final LocalDateTime timestamp;
    
    public ContractUpdatedEvent(ContractId contractId, String contractName, Long updatedBy) {
        this.contractId = contractId;
        this.contractName = contractName;
        this.updatedBy = updatedBy;
        this.timestamp = LocalDateTime.now();
    }
    
    /**
     * 获取事件类型
     */
    public String getEventType() {
        return "ContractUpdated";
    }
}