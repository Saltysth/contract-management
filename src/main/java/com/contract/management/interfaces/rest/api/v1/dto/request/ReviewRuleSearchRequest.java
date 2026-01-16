package com.contract.management.interfaces.rest.api.v1.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;

/**
 * 审查规则搜索请求
 * 参考条款搜索请求的设计模式
 *
 * @author SaltyFish
 * @since 1.0.0
 */
@Data
@Schema(description = "审查规则搜索请求")
public class ReviewRuleSearchRequest {

    /**
     * 规则名称（模糊查询）
     */
    @Schema(description = "规则名称（模糊查询）", example = "采购合同")
    private String ruleName;

    /**
     * 规则类型
     */
    @Schema(description = "规则类型", example = "specific", allowableValues = {"specific", "fallback", "extended"})
    private String ruleType;

    /**
     * 合同类型（数组包含查询）
     */
    @Schema(description = "合同类型", example = "采购合同")
    private String contractType;

    /**
     * 条款类型（数组包含查询）
     */
    @Schema(description = "条款类型", example = "付款条款")
    private String clauseType;

    /**
     * 启用状态
     */
    @Schema(description = "启用状态", example = "true")
    private Boolean enabled;

    /**
     * 提示词模式编码
     */
    @Schema(description = "提示词模式编码", example = "1", allowableValues = {"0", "1", "2"})
    private Short promptModeCode;

    /**
     * 页码（从0开始）
     */
    @Min(value = 0, message = "页码不能小于0")
    @Schema(description = "页码（从0开始）", example = "0")
    private Integer page = 0;

    /**
     * 页大小
     */
    @Min(value = 1, message = "页大小不能小于1")
    @Max(value = 100, message = "页大小不能大于100")
    @Schema(description = "页大小", example = "20")
    private Integer size = 20;

    /**
     * 排序字段
     */
    @Schema(description = "排序字段", example = "createTime")
    private String sort = "createTime";

    /**
     * 排序方向（ASC/DESC）
     */
    @Schema(description = "排序方向（ASC/DESC）", example = "DESC", allowableValues = {"ASC", "DESC"})
    private String direction = "DESC";
}