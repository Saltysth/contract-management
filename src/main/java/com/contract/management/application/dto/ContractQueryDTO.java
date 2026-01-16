package com.contract.management.application.dto;

import com.contract.common.constant.ContractType;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

/**
 * 合同查询应用DTO
 *
 * @author SaltyFish
 * @since 1.0.0
 */
@Data
public class ContractQueryDTO {
    
    /**
     * 合同名称（模糊查询）
     */
    private String contractName;
    
    /**
     * 合同类型列表
     */
    private List<ContractType> contractTypes;
    
        
    /**
     * 甲方公司名称（模糊查询）
     */
    private String partyAName;
    
    /**
     * 乙方公司名称（模糊查询）
     */
    private String partyBName;
    
    /**
     * 合同金额范围 - 最小值
     */
    private BigDecimal minAmount;
    
    /**
     * 合同金额范围 - 最大值
     */
    private BigDecimal maxAmount;
    
    /**
     * 签署日期范围 - 开始日期
     */
    private LocalDate signDateStart;
    
    /**
     * 签署日期范围 - 结束日期
     */
    private LocalDate signDateEnd;
    
    /**
     * 生效日期范围 - 开始日期
     */
    private LocalDate effectiveDateStart;
    
    /**
     * 生效日期范围 - 结束日期
     */
    private LocalDate effectiveDateEnd;
    
    /**
     * 到期日期范围 - 开始日期
     */
    private LocalDate expiryDateStart;
    
    /**
     * 到期日期范围 - 结束日期
     */
    private LocalDate expiryDateEnd;
    
        
    /**
     * 创建人ID
     */
    private Long createdBy;
    
    /**
     * 是否包含已删除的记录
     */
    private Boolean includeDeleted;
}