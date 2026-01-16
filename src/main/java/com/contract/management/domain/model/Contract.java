package com.contract.management.domain.model;

import com.contract.common.constant.ContractType;
import com.contract.management.domain.model.valueobject.AuditInfo;
import com.contract.management.domain.model.valueobject.ContractName;
import com.contract.management.domain.model.valueobject.ContractPeriod;
import com.contract.management.domain.model.valueobject.Money;
import com.contract.management.domain.model.valueobject.PartyInfo;
import lombok.Getter;
import lombok.Setter;

/**
 * 合同聚合根
 *
 * @author SaltyFish
 * @since 1.0.0
 */
@Getter
public class Contract {
    
    private final ContractId id;
    @Setter
    private ContractName contractName;
    @Setter
    private ContractType contractType;
    private PartyInfo partyA;
    private PartyInfo partyB;
    private Money contractAmount;
    private ContractPeriod period;
    private String attachmentUuid;
    private String description;
    private AuditInfo auditInfo;
    private Boolean isDeleted;

    /**
     * 构造函数 - 创建新合同
     */
    public Contract(ContractId id, ContractName contractName, ContractType contractType,
                   PartyInfo partyA, PartyInfo partyB, Money contractAmount,
                   ContractPeriod period, String attachmentUuid, String description, Long createdBy) {
        this.id = id;
        this.contractName = contractName;
        this.contractType = contractType;
        this.partyA = partyA;
        this.partyB = partyB;
        this.contractAmount = contractAmount;
        this.period = period;
        this.attachmentUuid = attachmentUuid;
        this.description = description;
        this.auditInfo = AuditInfo.create(createdBy);
        this.isDeleted = false;

        // 创建时验证
        validateForCreation();
    }
    
    /**
     * 构造函数 - 从数据库重建
     */
    public Contract(ContractId id, ContractName contractName, ContractType contractType,
                   PartyInfo partyA, PartyInfo partyB, Money contractAmount, ContractPeriod period,
                   String attachmentUuid, String description, AuditInfo auditInfo) {
        this.id = id;
        this.contractName = contractName;
        this.contractType = contractType;
        this.partyA = partyA;
        this.partyB = partyB;
        this.contractAmount = contractAmount;
        this.period = period;
        this.attachmentUuid = attachmentUuid;
        this.description = description;
        this.auditInfo = auditInfo;
    }
    
    // ==================== 领域行为方法 ====================
    
    /**
     * 更新基本信息
     */
    public void updateBasicInfo(ContractName contractName, String description) {
        if (contractName != null) {
            this.contractName = contractName;
        }
        this.description = description;
        this.auditInfo = this.auditInfo.update();
        
        validateForUpdate();
    }
    
    /**
     * 更新当事方信息
     */
    public void updateParties(PartyInfo partyA, PartyInfo partyB) {
        if (partyA != null) {
            this.partyA = partyA;
        }
        if (partyB != null) {
            this.partyB = partyB;
        }
        this.auditInfo = this.auditInfo.update();
        
        validateParties();
    }
    
    /**
     * 更新合同金额
     */
    public void updateAmount(Money amount) {
        this.contractAmount = amount;
        this.auditInfo = this.auditInfo.update();
    }
    
    /**
     * 更新合同期限
     */
    public void updatePeriod(ContractPeriod period) {
        this.period = period;
        this.auditInfo = this.auditInfo.update();
    }
    
        
    /**
     * 附加文件
     */
    public void attachFile(String fileUuid) {
        if (fileUuid == null || fileUuid.trim().isEmpty()) {
            throw new IllegalArgumentException("文件UUID不能为空");
        }
        
        this.attachmentUuid = fileUuid.trim();
        this.auditInfo = this.auditInfo.update();
    }
    
    /**
     * 分离文件
     */
    public void detachFile() {
        this.attachmentUuid = null;
        this.auditInfo = this.auditInfo.update();
    }
    
    /**
     * 软删除合同
     */
    public void delete() {
        this.contractName = ContractName.of(this.contractName.getValue() + "_已删除_" + System.currentTimeMillis());
        this.isDeleted = true;
        this.auditInfo = this.auditInfo.update();
    }

    /**
     * 取消软删除
     */
    public void restore() {
        if (!isDeleted) {
            throw new IllegalStateException("合同当前状态不允许取消删除");
        }
        this.isDeleted = false;
        this.auditInfo = this.auditInfo.update();
    }
    
    // ==================== 业务规则验证方法 ====================
    
        
    /**
     * 检查合同是否已过期
     */
    public boolean isExpired() {
        return period != null && period.isExpired();
    }
    
    /**
     * 检查合同是否处于生效状态
     */
    public boolean isActive() {
        return period == null || period.isActive();
    }
    
    /**
     * 检查合同是否即将到期
     */
    public boolean isExpiringWithin(int days) {
        return period != null && period.isExpiringWithin(days);
    }

    
    /**
     * 验证创建时的业务规则
     */
    public void validateForCreation() {
        validateBasicInfo();
        validateParties();
        validateAmount();
    }
    
    /**
     * 验证更新时的业务规则
     */
    public void validateForUpdate() {
        validateBasicInfo();
        validateParties();
        validateAmount();
    }
    
        
    /**
     * 验证基本信息
     */
    private void validateBasicInfo() {
        if (contractName == null) {
            throw new IllegalArgumentException("合同名称不能为空");
        }
        // 移除合同类型的强制校验，允许为空
    }
    
    /**
     * 验证当事方信息
     */
    private void validateParties() {
        // 移除甲乙方信息的强制校验，允许为空
        // 当事方信息可以在后续业务流程中补充
    }
    
    /**
     * 验证金额信息
     */
    private void validateAmount() {
        // 金额可以为空，但如果不为空则必须有效
        if (contractAmount != null && !contractAmount.isPositive() && !contractAmount.isZero()) {
            throw new IllegalArgumentException("合同金额必须大于等于零");
        }
    }

    
    // ==================== 辅助方法 ====================

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Contract contract = (Contract) o;
        return id != null && id.equals(contract.id);
    }
    
    @Override
    public int hashCode() {
        return id != null ? id.hashCode() : 0;
    }
    
    @Override
    public String toString() {
        return "Contract{" +
                "id=" + id +
                ", contractName=" + contractName +
                ", contractType=" + contractType +
                '}';
    }
}