package com.contract.management.infrastructure.entity;

import com.baomidou.mybatisplus.annotation.*;
import com.contract.management.infrastructure.handler.OperationDetailsTypeHandler;
import com.contract.management.domain.model.valueobject.OperationDetails;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 操作日志实体
 *
 * @author SaltyFish
 * @since 1.0.0
 */
@Data
@TableName("operation_log")
public class OperationLogEntity {

    /**
     * 主键ID
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 操作类型
     */
    @TableField("operation_type")
    private String operationType;

    /**
     * 资源类型
     */
    @TableField("resource_type")
    private String resourceType;

    /**
     * 资源ID
     */
    @TableField("resource_id")
    private Long resourceId;

    /**
     * 操作详情JSON
     */
    @TableField(value = "operation_details", typeHandler = OperationDetailsTypeHandler.class)
    private OperationDetails operationDetails;

    /**
     * 用户ID
     */
    @TableField("user_id")
    private Long userId;

    /**
     * IP地址
     */
    @TableField("ip_address")
    private String ipAddress;

    /**
     * 用户代理
     */
    @TableField("user_agent")
    private String userAgent;

    /**
     * 执行耗时（毫秒）
     */
    @TableField("execution_time_ms")
    private Long executionTimeMs;

    /**
     * 创建时间
     */
    @TableField(value = "created_time", fill = FieldFill.INSERT)
    private LocalDateTime createdTime;
}