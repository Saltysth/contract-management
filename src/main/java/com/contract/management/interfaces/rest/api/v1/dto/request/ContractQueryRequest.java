package com.contract.management.interfaces.rest.api.v1.dto.request;

import com.contract.common.constant.ContractStatus;
import com.contract.common.constant.ContractType;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

/**
 * 合同查询请求DTO
 *
 * @author SaltyFish
 * @since 1.0.0
 */
@Data
@Schema(description = "合同查询请求")
public class ContractQueryRequest {
    
    @Schema(description = "合同名称（模糊查询）", example = "销售合同")
    private String contractName;
    
    @Schema(description = "合同类型列表", example = "[\"SALES\", \"PURCHASE\"]")
    private List<ContractType> contractTypes;
    
    @Schema(description = "合同状态列表", example = "[\"DRAFT\", \"ACTIVE\"]")
    private List<ContractStatus> contractStatuses;
    
    @Schema(description = "甲方公司名称（模糊查询）", example = "北京科技")
    private String partyAName;
    
    @Schema(description = "乙方公司名称（模糊查询）", example = "上海公司")
    private String partyBName;
    
    @Schema(description = "合同金额范围 - 最小值", example = "10000.00")
    private BigDecimal minAmount;
    
    @Schema(description = "合同金额范围 - 最大值", example = "1000000.00")
    private BigDecimal maxAmount;
    
    @Schema(description = "签署日期范围 - 开始日期", example = "2024-01-01")
    private LocalDate signDateStart;
    
    @Schema(description = "签署日期范围 - 结束日期", example = "2024-12-31")
    private LocalDate signDateEnd;
    
    @Schema(description = "生效日期范围 - 开始日期", example = "2024-01-01")
    private LocalDate effectiveDateStart;
    
    @Schema(description = "生效日期范围 - 结束日期", example = "2024-12-31")
    private LocalDate effectiveDateEnd;
    
    @Schema(description = "到期日期范围 - 开始日期", example = "2024-01-01")
    private LocalDate expiryDateStart;
    
    @Schema(description = "到期日期范围 - 结束日期", example = "2024-12-31")
    private LocalDate expiryDateEnd;
    
    @Schema(description = "标签列表（包含任一标签）", example = "[\"重要\", \"长期\"]")
    private List<String> tags;
    
    @Schema(description = "创建人ID", example = "1")
    private Long createdBy;
    
    @Schema(description = "是否包含已删除的记录", example = "false")
    private Boolean includeDeleted;
}