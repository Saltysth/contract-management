package com.contract.management.interfaces.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 操作日志查询DTO
 *
 * @author SaltyFish
 * @since 1.0.0
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class OperationLogDTO {

    /**
     * 资源类型
     */
    private String resourceType;

    /**
     * 操作描述
     */
    private String description;

    /**
     * 操作结果
     */
    private String result;

    /**
     * 错误信息（失败时有值）
     */
    private String errorMessage;

    /**
     * 执行耗时（毫秒）
     */
    private Long executionTimeMs;

    /**
     * 创建时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createdTime;
}