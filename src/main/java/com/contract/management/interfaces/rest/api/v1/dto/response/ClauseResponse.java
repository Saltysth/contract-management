package com.contract.management.interfaces.rest.api.v1.dto.response;

import com.contract.management.domain.model.ClauseType;
import com.contract.management.domain.model.RiskLevel;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 条款响应DTO
 *
 * @author SaltyFish
 * @since 1.0.0
 */
@Data
@Schema(description = "条款响应")
public class ClauseResponse {

    @Schema(description = "条款ID", example = "1")
    private Long id;

    @Schema(description = "抽取任务ID", example = "1")
    private Long extractionTaskId;

    @Schema(description = "合同ID", example = "1")
    private Long contractId;

    @Schema(description = "条款类型", example = "PAYMENT")
    private ClauseType clauseType;

    @Schema(description = "条款类型描述", example = "付款条款")
    private String clauseTypeDescription;

    @Schema(description = "条款标题", example = "付款条款")
    private String clauseTitle;

    @Schema(description = "条款内容", example = "买方应在收到货物后30天内支付全部款项")
    private String clauseContent;

    @Schema(description = "条款位置（JSON格式）", example = "{\"page\": 1, \"start\": 100, \"end\": 200}")
    private String clausePosition;

    @Schema(description = "置信度分数", example = "95.50")
    private BigDecimal confidenceScore;

    @Schema(description = "提取的实体（JSON格式）", example = "{\"parties\": [\"买方\", \"卖方\"], \"amount\": \"全部款项\"}")
    private String extractedEntities;

    @Schema(description = "风险等级", example = "LOW")
    private RiskLevel riskLevel;

    @Schema(description = "风险等级描述", example = "低风险")
    private String riskLevelDescription;

    @Schema(description = "风险因子（JSON格式）", example = "{\"risks\": [\"付款延迟风险\"]}")
    private String riskFactors;

    @Schema(description = "创建时间", example = "2024-01-15T10:30:00")
    private LocalDateTime createdTime;

    @Schema(description = "是否有风险", example = "false")
    private Boolean hasRisk;

    @Schema(description = "是否为高风险", example = "false")
    private Boolean isHighRisk;

    @Schema(description = "是否有置信度分数", example = "true")
    private Boolean hasConfidenceScore;

    @Schema(description = "是否有提取的实体", example = "true")
    private Boolean hasExtractedEntities;

    @Schema(description = "是否有风险因子", example = "true")
    private Boolean hasRiskFactors;
}