package com.contract.management.infrastructure.entity;

import com.baomidou.mybatisplus.annotation.*;
import com.contract.management.domain.model.valueobject.ClauseExtractionResult;
import com.contract.management.infrastructure.handler.ClauseExtractionResultTypeHandler;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 条款抽取实体
 * FUTURE 应该有一个contract_task_id，但是边界设计问题缺失
 * @author SaltyFish
 * @since 1.0.0
 */
@Data
@TableName("clause_extractions")
public class ClauseExtractionEntity {

    /**
     * 主键ID
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 合同ID
     */
    @TableField("contract_id")
    private Long contractId;

    /**
     * 抽取状态
     */
    @TableField("status")
    private String status;

    /**
     * 错误信息
     */
    @TableField("error_message")
    private String errorMessage;

    /**
     * 抽取结果JSON
     */
    @TableField(value = "extraction_result", typeHandler = ClauseExtractionResultTypeHandler.class)
    private ClauseExtractionResult extractionResult;

    /**
     * 开始时间
     */
    @TableField("started_at")
    private LocalDateTime startedAt;

    /**
     * 完成时间
     */
    @TableField("completed_at")
    private LocalDateTime completedAt;

    /**
     * 创建人
     */
    @TableField("created_by")
    private Long createdBy;

    /**
     * 更新人
     */
    @TableField("updated_by")
    private Long updatedBy;

    /**
     * 创建时间
     */
    @TableField(value = "created_time", fill = FieldFill.INSERT)
    private LocalDateTime createdTime;

    /**
     * 更新时间
     */
    @TableField(value = "updated_time", fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updatedTime;

    /**
     * 乐观锁版本号
     */
    @TableField("object_version_number")
    private Long objectVersionNumber;

    /**
     * 软删除标记
     */
    @TableField("is_deleted")
    @TableLogic
    private Boolean isDeleted;
}