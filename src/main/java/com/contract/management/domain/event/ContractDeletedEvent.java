package com.contract.management.domain.event;

import com.contract.management.domain.model.ContractId;
import lombok.Getter;
import lombok.ToString;

import java.time.LocalDateTime;

/**
 * 合同删除事件
 *
 * @author SaltyFish
 * @since 1.0.0
 */
@Getter
@ToString
public class ContractDeletedEvent {
    
    private final ContractId contractId;
    private final String contractName;
    private final Long deletedBy;
    private final LocalDateTime timestamp;
    
    public ContractDeletedEvent(ContractId contractId, String contractName, Long deletedBy) {
        this.contractId = contractId;
        this.contractName = contractName;
        this.deletedBy = deletedBy;
        this.timestamp = LocalDateTime.now();
    }
    
    /**
     * 获取事件类型
     */
    public String getEventType() {
        return "ContractDeleted";
    }
}