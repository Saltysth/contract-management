package com.contract.management.interfaces.rest.api.v1.dto.request;

import com.contract.common.constant.ContractType;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

/**
 * 创建合同请求DTO
 *
 * @author SaltyFish
 * @since 1.0.0
 */
@Data
@Schema(description = "创建合同请求")
public class CreateContractRequest {
    
    @Size(max = 100, message = "合同名称不能超过100个字符")
    @Schema(description = "合同名称", example = "销售合同-2024001")
    private String contractName;
    
    @Schema(description = "合同类型", example = "SALES")
    private ContractType contractType;
    
    @Valid
    @Schema(description = "甲方信息")
    private PartyInfoRequest partyA;
    
    @Valid
    @Schema(description = "乙方信息")
    private PartyInfoRequest partyB;
    
    @Schema(description = "合同金额", example = "100000.00")
    private BigDecimal contractAmount;

    @Schema(description = "签署日期", example = "2024-01-15")
    private LocalDate signDate;
    
    @Schema(description = "生效日期", example = "2024-01-16")
    private LocalDate effectiveDate;
    
    @Schema(description = "到期日期", example = "2024-12-31")
    private LocalDate expiryDate;
    
    @Size(max = 500, message = "合同描述不能超过500个字符")
    @Schema(description = "合同描述", example = "这是一份销售合同")
    private String description;

    @NotBlank
    @Schema(description = "附件UUID", example = "attachment-uuid-123")
    private String attachmentUuid;
    
    @Schema(description = "标签列表", example = "[\"重要\", \"长期\"]")
    private List<String> tags;
    
    /**
     * 甲乙方信息请求DTO
     */
    @Data
    @Schema(description = "当事方信息")
    public static class PartyInfoRequest {
        
        @Size(max = 200, message = "公司名称不能超过200个字符")
        @Schema(description = "公司名称", example = "北京科技有限公司")
        private String companyName;
        
        @Size(max = 100, message = "联系方式不能超过100个字符")
        @Schema(description = "联系方式", example = "13800138000")
        private String contactInfo;
        
        @Size(max = 300, message = "地址不能超过300个字符")
        @Schema(description = "地址", example = "北京市朝阳区xxx大厦")
        private String address;
    }
}