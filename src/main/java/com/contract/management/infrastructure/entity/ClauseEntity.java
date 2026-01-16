package com.contract.management.infrastructure.entity;

import com.baomidou.mybatisplus.annotation.*;
import com.contract.management.infrastructure.handler.ClausePositionTypeHandler;
import com.contract.management.infrastructure.handler.ExtractedEntitiesSafeTypeHandler;
import com.contract.management.infrastructure.handler.RiskFactorsTypeHandler;
import com.contract.management.domain.model.valueobject.ClausePosition;
import com.contract.management.domain.model.valueobject.RiskFactors;
import com.contract.management.domain.model.valueobject.ExtractedEntities;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 条款实体类
 *
 * @author SaltyFish
 * @since 1.0.0
 */
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("clause")
public class ClauseEntity {

    /**
     * 条款ID
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

  
    /**
     * 抽取任务ID
     */
    @TableField("extraction_task_id")
    private Long extractionTaskId;

    /**
     * 合同ID
     */
    @TableField("contract_id")
    private Long contractId;

    /**
     * 条款类型
     */
    @TableField("clause_type")
    private String clauseType;

    /**
     * 条款标题
     */
    @TableField("clause_title")
    private String clauseTitle;

    /**
     * 条款内容
     */
    @TableField("clause_content")
    private String clauseContent;

    /**
     * 条款位置（JSON格式）
     */
    @TableField(value = "clause_position", typeHandler = ClausePositionTypeHandler.class)
    private ClausePosition clausePosition;

    /**
     * 置信度分数
     */
    @TableField("confidence_score")
    private BigDecimal confidenceScore;

    /**
     * 提取的实体（JSON格式）
     */
    @TableField(value = "extracted_entities", typeHandler = ExtractedEntitiesSafeTypeHandler.class)
    private ExtractedEntities extractedEntities;

    /**
     * 风险等级
     */
    @TableField("risk_level")
    private String riskLevel;

    /**
     * 风险因子（JSON格式）
     */
    @TableField(value = "risk_factors", typeHandler = RiskFactorsTypeHandler.class)
    private RiskFactors riskFactors;

    /**
     * 创建时间
     */
    @TableField(value = "created_time", fill = FieldFill.INSERT)
    private LocalDateTime createdTime;

    /**
     * 版本号（乐观锁）
     */
    @Version
    @TableField("object_version_number")
    private Long objectVersionNumber;

    /**
     * 逻辑删除标识
     */
    @TableLogic
    @TableField("is_deleted")
    private Boolean isDeleted;
}