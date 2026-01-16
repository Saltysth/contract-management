package com.contract.management.infrastructure.entity;

import com.baomidou.mybatisplus.annotation.*;
import com.contract.management.domain.model.valueobject.ClassificationMetadata;
import com.contract.management.infrastructure.handler.ClassificationMetadataTypeHandler;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 合同分类结果实体
 *
 * @author SaltyFish
 * @since 1.0.0
 */
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("classification_result")
public class ClassificationResultEntity {

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    @TableField("contract_id")
    private Long contractId;

    @TableField("contract_type")
    private String contractType;

    @TableField("classification_method")
    private String classificationMethod;

    @TableField("confidence_score")
    private BigDecimal confidenceScore;

    @TableField("model_version")
    private String modelVersion;

    @TableField("classified_by")
    private Long classifiedBy;

    @TableField("classification_reason")
    private String classificationReason;

    @TableField(value = "metadata", typeHandler = ClassificationMetadataTypeHandler.class)
    private ClassificationMetadata metadata;

    @TableField("is_active")
    private Boolean isActive;

    @TableField(value = "created_by", fill = FieldFill.INSERT)
    private Long createdBy;

    @TableField(value = "updated_by", fill = FieldFill.INSERT_UPDATE)
    private Long updatedBy;

    @TableField(value = "created_time", fill = FieldFill.INSERT)
    private LocalDateTime createdTime;

    @TableField(value = "updated_time", fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updatedTime;

    @Version
    @TableField("object_version_number")
    private Long objectVersionNumber;

    @TableLogic
    @TableField("is_deleted")
    private Boolean isDeleted;
}