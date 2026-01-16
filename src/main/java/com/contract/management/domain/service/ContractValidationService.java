package com.contract.management.domain.service;

import com.contract.common.constant.ContractStatus;
import com.contract.management.domain.model.Contract;
import com.contract.management.domain.model.valueobject.Money;
import com.contract.management.domain.model.valueobject.PartyInfo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

/**
 * 合同验证服务
 *
 * @author SaltyFish
 * @since 1.0.0
 */
@Slf4j
@Service
public class ContractValidationService {
    
    /**
     * 验证合同创建
     *
     * @param contract 合同
     * @throws ContractValidationException 验证失败
     */
    public void validateContractForCreation(Contract contract) {
        validateBasicInfo(contract);
        validateParties(contract);
        validateAmount(contract);
        validatePeriod(contract);
        validateBusinessRules(contract);
    }
    
    /**
     * 验证合同更新
     *
     * @param contract 合同
     * @throws ContractValidationException 验证失败
     */
    public void validateContractForUpdate(Contract contract) {
        validateContractForCreation(contract);
    }
    
    /**
     * 验证基本信息
     */
    private void validateBasicInfo(Contract contract) {
        if (contract.getContractName() == null) {
            throw new ContractValidationException("合同名称不能为空");
        }

        // 移除合同类型的强制校验，允许为空
        // 合同类型可以在后续业务流程中补充
    }
    
    /**
     * 验证当事方信息
     */
    private void validateParties(Contract contract) {
        // 移除甲乙方信息的强制校验，允许为空
        // 当事方信息可以在后续业务流程中补充

        PartyInfo partyA = contract.getPartyA();
        PartyInfo partyB = contract.getPartyB();

        // 只有当甲乙方信息都不为空时才验证不能相同
        if (partyA != null && partyB != null && partyA.getName().equals(partyB.getName())) {
            throw new ContractValidationException("甲方和乙方不能是同一个实体");
        }
    }
    
    /**
     * 验证金额信息
     */
    private void validateAmount(Contract contract) {
        Money amount = contract.getContractAmount();

        // 金额可以为空，如果不为空则只验证不能为负数
        // 其他校验（如精度、范围）已经在Money值对象中处理
        if (amount != null && !amount.isPositive() && !amount.isZero()) {
            throw new ContractValidationException("合同金额不能为负数");
        }
    }
    
    /**
     * 验证期限信息
     */
    private void validatePeriod(Contract contract) {
        // 期限验证逻辑已在ContractPeriod值对象中实现
        // 这里可以添加额外的业务规则验证
        
        if (contract.getPeriod() != null) {
            // 验证合同期限不能过长（例如不超过100年）
            Long totalDays = contract.getPeriod().getTotalDays();
            if (totalDays != null && totalDays > 36500) { // 100年
                throw new ContractValidationException("合同期限不能超过100年");
            }
        }
    }
    
    /**
     * 验证业务规则
     */
    private void validateBusinessRules(Contract contract) {
        // 根据合同类型验证特定的业务规则
        switch (contract.getContractType()) {
            case EMPLOYMENT:
                validateEmploymentContract(contract);
                break;
            case LEASE:
                validateLeaseContract(contract);
                break;
            case SALES:
            case PURCHASE:
                validateSalesPurchaseContract(contract);
                break;
            case SERVICE:
                validateServiceContract(contract);
                break;
            case OTHER:
                // 其他类型的通用验证
                break;
        }
    }
    
    /**
     * 验证劳动合同特定规则
     */
    private void validateEmploymentContract(Contract contract) {
        // 劳动合同必须有期限
        if (contract.getPeriod() == null || 
            contract.getPeriod().getEffectiveDate() == null) {
            throw new ContractValidationException("劳动合同必须设置生效日期");
        }
    }
    
    /**
     * 验证租赁合同特定规则
     */
    private void validateLeaseContract(Contract contract) {
        // 移除租赁合同的强制金额校验，允许为空
        // 租赁合同的金额和期限信息可以在后续业务流程中补充

        // 保留期限校验，如果提供了期限信息就进行验证
        if (contract.getPeriod() != null &&
            (contract.getPeriod().getEffectiveDate() == null ||
             contract.getPeriod().getExpiryDate() == null)) {
            throw new ContractValidationException("如果设置了租赁期限，必须设置完整的生效和到期日期");
        }
    }
    
    /**
     * 验证销售/采购合同特定规则
     */
    private void validateSalesPurchaseContract(Contract contract) {
        // 移除销售/采购合同的金额校验提示
        // 金额信息可以在后续业务流程中补充
    }
    
    /**
     * 验证服务合同特定规则
     */
    private void validateServiceContract(Contract contract) {
        // 移除服务合同的期限提示
        // 期限信息可以在后续业务流程中补充
    }
    
}