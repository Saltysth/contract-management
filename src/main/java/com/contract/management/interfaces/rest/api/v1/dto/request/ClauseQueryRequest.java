package com.contract.management.interfaces.rest.api.v1.dto.request;

import com.contract.management.domain.model.ClauseType;
import com.contract.management.domain.model.RiskLevel;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Max;
import lombok.Data;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Positive;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 条款查询请求DTO
 *
 * @author SaltyFish
 * @since 1.0.0
 */
@Data
@Schema(description = "条款查询请求")
public class ClauseQueryRequest {

    @Schema(description = "抽取任务ID", example = "1")
    @Positive(message = "抽取任务ID必须为正数")
    private Long extractionTaskId;

    @Schema(description = "合同ID", example = "1")
    @Positive(message = "合同ID必须为正数")
    private Long contractId;

    @Schema(description = "条款类型", example = "PAYMENT")
    private ClauseType clauseType;

    @Schema(description = "风险等级", example = "LOW")
    private RiskLevel riskLevel;

    @Schema(description = "条款标题关键词", example = "付款")
    private String titleKeyword;

    @Schema(description = "条款内容关键词", example = "支付")
    private String contentKeyword;

    @DecimalMin(value = "0.00", message = "最小置信度分数不能小于0")
    @DecimalMax(value = "100.00", message = "最小置信度分数不能大于100")
    @Schema(description = "最小置信度分数", example = "80.00")
    private BigDecimal minConfidenceScore;

    @DecimalMin(value = "0.00", message = "最大置信度分数不能小于0")
    @DecimalMax(value = "100.00", message = "最大置信度分数不能大于100")
    @Schema(description = "最大置信度分数", example = "100.00")
    private BigDecimal maxConfidenceScore;

    @Schema(description = "是否仅高风险条款", example = "false")
    private Boolean highRiskOnly;

    @Schema(description = "是否仅包含风险因子的条款", example = "false")
    private Boolean hasRiskFactorsOnly;

    @Schema(description = "创建时间开始", example = "2024-01-01T00:00:00")
    private LocalDateTime createdTimeStart;

    @Schema(description = "创建时间结束", example = "2024-12-31T23:59:59")
    private LocalDateTime createdTimeEnd;

    @Schema(description = "创建人ID", example = "1")
    private Long createdBy;

    @Min(value = 0, message = "页码不能小于0")
    @Schema(description = "页码（从0开始）", example = "0")
    private Integer page = 0;

    @Min(value = 1, message = "页大小不能小于1")
    @Max(value = 100, message = "页大小不能大于100")
    @Schema(description = "页大小", example = "20")
    private Integer size = 20;

    @Schema(description = "排序字段", example = "createdTime")
    private String sort = "createdTime";

    @Schema(description = "排序方向（ASC/DESC）", example = "DESC")
    private String direction = "DESC";
}