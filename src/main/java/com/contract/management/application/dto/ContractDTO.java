package com.contract.management.application.dto;

import com.contract.common.constant.ContractType;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 合同应用层DTO
 * 用于应用服务层的数据传输
 *
 * @author SaltyFish
 * @since 1.0.0
 */
@Data
public class ContractDTO {
    
    /**
     * 合同ID
     */
    private Long id;
    
    /**
     * 合同名称
     */
    private String contractName;
    
    /**
     * 合同类型
     */
    private ContractType contractType;
    
        
    /**
     * 甲方信息
     */
    private PartyInfoDTO partyA;
    
    /**
     * 乙方信息
     */
    private PartyInfoDTO partyB;
    
    /**
     * 合同金额
     */
    private BigDecimal contractAmount;

    /**
     * 签署日期
     */
    private LocalDate signDate;
    
    /**
     * 生效日期
     */
    private LocalDate effectiveDate;
    
    /**
     * 到期日期
     */
    private LocalDate expiryDate;
    
    /**
     * 合同描述
     */
    private String description;
    
    /**
     * 附件UUID
     */
    private String attachmentUuid;

    /**
     * 本地文件URL（下载后的访问地址）
     */
    private String localFileUrl;

    /**
     * 本地文件名
     */
    private String localFileName;
    
        
    /**
     * 创建人
     */
    private Long createdBy;
    
    /**
     * 更新人
     */
    private Long updatedBy;
    
    /**
     * 创建时间
     */
    private LocalDateTime createdTime;
    
    /**
     * 更新时间
     */
    private LocalDateTime updatedTime;
    
    /**
     * 版本号
     */
    private Long objectVersionNumber;
    
    /**
     * 甲乙方信息DTO
     */
    @Data
    public static class PartyInfoDTO {
        /**
         * 公司名称
         */
        private String companyName;
        
        /**
         * 联系方式
         */
        private String contactInfo;
        
        /**
         * 地址
         */
        private String address;
    }
}