package com.contract.management.interfaces.rest.api.v1.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 审查类型详情响应DTO
 * 用于返回审查规则分类的详细信息
 *
 * @author SaltyFish
 * @since 1.0.0
 */
@Data
@Schema(description = "审查类型详情响应")
public class ReviewTypeDetailResponse {

    /**
     * 枚举名称
     */
    @Schema(description = "枚举名称", example = "RISK_ASSESSMENT")
    private String name;

    /**
     * 显示名称
     */
    @Schema(description = "显示名称", example = "风险评估")
    private String displayName;

    /**
     * 描述
     */
    @Schema(description = "描述", example = "识别潜在法律和商业风险")
    private String description;
}