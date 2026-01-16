package com.contract.management.infrastructure.repository;

import com.contract.management.domain.model.valueobject.ContractName;
import com.contract.management.domain.model.valueobject.ContractPeriod;
import com.contract.management.domain.model.valueobject.Money;
import com.contract.management.domain.model.valueobject.PartyInfo;
import com.contract.common.constant.ContractStatus;
import com.contract.common.constant.ContractType;
import com.contract.common.constant.Currency;
import com.contract.management.domain.model.Contract;
import com.contract.management.domain.model.ContractId;
import com.contract.management.infrastructure.converter.ContractConverter;
import com.contract.management.infrastructure.entity.ContractEntity;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

/**
 * ContractRepositoryImpl测试类
 * 
 * 简化的单元测试，专注于验证核心转换和业务逻辑
 *
 * @author SaltyFish
 * @since 1.0.0
 */
public class ContractRepositoryImplTest {

    @Test
    public void testContractCreation() {
        // 创建测试合同
        Contract contract = createTestContract();
        
        // 验证合同创建成功
        assertNotNull(contract);
        assertEquals("测试合同", contract.getContractName().getValue());
        assertEquals(ContractType.SALES, contract.getContractType());
    }

    @Test
    public void testContractValidation() {
        // 测试合同验证逻辑
        Contract contract = createTestContract();
        
        // 验证基本信息
        assertNotNull(contract.getContractName());
        assertNotNull(contract.getContractType());
        assertNotNull(contract.getPartyA());
        assertNotNull(contract.getPartyB());
        
        // 验证业务规则
        assertFalse(contract.isActive()); // 草稿状态不是活跃状态
    }

    @Test
    public void testConverterDomainToEntity() {
        // 测试领域模型转实体
        Contract contract = createTestContract();
        ContractEntity entity = ContractConverter.toEntity(contract);
        
        assertNotNull(entity);
        assertEquals(contract.getContractName().getValue(), entity.getContractName());
        assertEquals(contract.getContractType().name(), entity.getContractType());
        assertEquals(contract.getPartyA().getName(), entity.getPartyAName());
        assertEquals(contract.getPartyB().getName(), entity.getPartyBName());
    }

    @Test
    public void testConverterEntityToDomain() {
        // 创建测试实体
        ContractEntity entity = createTestEntity();
        Contract contract = ContractConverter.toDomain(entity);
        
        assertNotNull(contract);
        assertEquals(entity.getContractName(), contract.getContractName().getValue());
        assertEquals(entity.getContractType(), contract.getContractType().name());
        assertEquals(entity.getPartyAName(), contract.getPartyA().getName());
        assertEquals(entity.getPartyBName(), contract.getPartyB().getName());
    }


    /**
     * 创建测试合同
     */
    private Contract createTestContract() {
        ContractId contractId = ContractId.of(1L); // 生成测试ID
        ContractName contractName = new ContractName("测试合同");
        ContractType contractType = ContractType.SALES;
        
        PartyInfo partyA = new PartyInfo("甲方公司", "13800138000", "甲方地址");
        PartyInfo partyB = new PartyInfo("乙方公司", "13800138001", "乙方地址");
        
        Money contractAmount = new Money(new BigDecimal("100000.00"), Currency.CNY);
        
        ContractPeriod period = new ContractPeriod(
            LocalDate.now(),
            LocalDate.now().plusDays(1),
            LocalDate.now().plusYears(1)
        );
        
        String description = "这是一个测试合同";
        Long createdBy = 1L;
        
        return new Contract(
            contractId,
            contractName,
            contractType,
            partyA,
            partyB,
            contractAmount,
            period,
            "testuuid",
            description,
            createdBy
        );
    }

    /**
     * 创建测试实体
     */
    private ContractEntity createTestEntity() {
        ContractEntity entity = new ContractEntity();
        entity.setId(1L);
        entity.setContractName("测试合同");
        entity.setContractType("SALES");
        entity.setPartyAName("甲方公司");
        entity.setPartyAContact("13800138000");
        entity.setPartyAAddress("甲方地址");
        entity.setPartyBName("乙方公司");
        entity.setPartyBContact("13800138001");
        entity.setPartyBAddress("乙方地址");
        entity.setContractAmount(new BigDecimal("100000.00"));
        entity.setSignDate(LocalDate.now());
        entity.setEffectiveDate(LocalDate.now().plusDays(1));
        entity.setExpiryDate(LocalDate.now().plusYears(1));
        entity.setDescription("这是一个测试合同");
        entity.setCreatedBy(1L);
        entity.setUpdatedBy(1L);
        entity.setCreatedTime(java.time.LocalDateTime.now());
        entity.setUpdatedTime(java.time.LocalDateTime.now());
        entity.setObjectVersionNumber(0L);
        entity.setIsDeleted(false);
        return entity;
    }
}