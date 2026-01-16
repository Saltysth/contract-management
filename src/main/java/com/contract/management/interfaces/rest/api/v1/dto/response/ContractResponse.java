package com.contract.management.interfaces.rest.api.v1.dto.response;

import com.contract.common.constant.ContractStatus;
import com.contract.common.constant.ContractType;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 合同响应DTO
 *
 * @author SaltyFish
 * @since 1.0.0
 */
@Data
@Schema(description = "合同响应")
public class ContractResponse {
    
    @Schema(description = "合同ID", example = "1")
    private Long id;
    
    @Schema(description = "合同名称", example = "销售合同-2024001")
    private String contractName;
    
    @Schema(description = "合同类型", example = "SALES")
    private ContractType contractType;
    
    @Schema(description = "合同状态", example = "DRAFT")
    private ContractStatus contractStatus;
    
    @Schema(description = "甲方信息")
    private PartyInfoResponse partyA;
    
    @Schema(description = "乙方信息")
    private PartyInfoResponse partyB;
    
    @Schema(description = "合同金额", example = "100000.00")
    private BigDecimal contractAmount;

    @Schema(description = "签署日期", example = "2024-01-15")
    private LocalDate signDate;
    
    @Schema(description = "生效日期", example = "2024-01-16")
    private LocalDate effectiveDate;
    
    @Schema(description = "到期日期", example = "2024-12-31")
    private LocalDate expiryDate;
    
    @Schema(description = "合同描述", example = "这是一份销售合同")
    private String description;
    
    @Schema(description = "附件UUID", example = "attachment-uuid-123")
    private String attachmentUuid;
    
    @Schema(description = "标签列表", example = "[\"重要\", \"长期\"]")
    private List<String> tags;
    
    @Schema(description = "创建人ID", example = "1")
    private Long createdBy;
    
    @Schema(description = "更新人ID", example = "1")
    private Long updatedBy;
    
    @Schema(description = "创建时间", example = "2024-01-15T10:00:00")
    private LocalDateTime createdTime;
    
    @Schema(description = "更新时间", example = "2024-01-15T10:00:00")
    private LocalDateTime updatedTime;
    
    @Schema(description = "版本号", example = "1")
    private Long objectVersionNumber;
    
    /**
     * 甲乙方信息响应DTO
     */
    @Data
    @Schema(description = "当事方信息")
    public static class PartyInfoResponse {
        
        @Schema(description = "公司名称", example = "北京科技有限公司")
        private String companyName;
        
        @Schema(description = "联系方式", example = "13800138000")
        private String contactInfo;
        
        @Schema(description = "地址", example = "北京市朝阳区xxx大厦")
        private String address;
    }
}