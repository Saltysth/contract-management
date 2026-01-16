package com.contract.management.interfaces.rest.api.v1.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import jakarta.validation.Valid;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

/**
 * 更新合同请求DTO
 *
 * @author SaltyFish
 * @since 1.0.0
 */
@Data
@Schema(description = "更新合同请求")
public class UpdateContractRequest {
    
    @NotNull(message = "合同ID不能为空")
    @Schema(description = "合同ID", example = "1")
    private Long id;
    
    @Size(max = 100, message = "合同名称不能超过100个字符")
    @Schema(description = "合同名称", example = "销售合同-2024001")
    private String contractName;
    
    @Valid
    @Schema(description = "甲方信息")
    private CreateContractRequest.PartyInfoRequest partyA;
    
    @Valid
    @Schema(description = "乙方信息")
    private CreateContractRequest.PartyInfoRequest partyB;
    
    @DecimalMin(value = "0", message = "合同金额不能为负数")
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
    
    @Schema(description = "附件UUID", example = "attachment-uuid-123")
    private String attachmentUuid;
    
    @Schema(description = "标签列表", example = "[\"重要\", \"长期\"]")
    private List<String> tags;
    
    @NotNull(message = "版本号不能为空")
    @Schema(description = "版本号（用于乐观锁）", example = "1")
    private Long objectVersionNumber;
}