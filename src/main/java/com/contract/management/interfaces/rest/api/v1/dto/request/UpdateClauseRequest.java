package com.contract.management.interfaces.rest.api.v1.dto.request;

import com.contract.management.domain.model.ClauseType;
import com.contract.management.domain.model.RiskLevel;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import jakarta.validation.constraints.*;
import java.math.BigDecimal;

/**
 * 更新条款请求DTO
 *
 * @author SaltyFish
 * @since 1.0.0
 */
@Data
@Schema(description = "更新条款请求")
public class UpdateClauseRequest {

    @Schema(description = "条款类型", example = "PAYMENT")
    private ClauseType clauseType;

    @Size(max = 255, message = "条款标题不能超过255个字符")
    @Schema(description = "条款标题", example = "付款条款")
    private String clauseTitle;

    @NotBlank(message = "条款内容不能为空")
    @Schema(description = "条款内容", example = "买方应在收到货物后30天内支付全部款项")
    private String clauseContent;

    @Schema(description = "条款位置（JSON格式）", example = "{\"page\": 1, \"start\": 100, \"end\": 200}")
    private String clausePosition;

    @DecimalMin(value = "0.00", message = "置信度分数不能小于0")
    @DecimalMax(value = "100.00", message = "置信度分数不能大于100")
    @Schema(description = "置信度分数", example = "95.50")
    private BigDecimal confidenceScore;

    @Schema(description = "提取的实体（JSON格式）", example = "{\"parties\": [\"买方\", \"卖方\"], \"amount\": \"全部款项\"}")
    private String extractedEntities;

    @Schema(description = "风险等级", example = "LOW")
    private RiskLevel riskLevel;

    @Schema(description = "风险因子（JSON格式）", example = "{\"risks\": [\"付款延迟风险\"]}")
    private String riskFactors;
}