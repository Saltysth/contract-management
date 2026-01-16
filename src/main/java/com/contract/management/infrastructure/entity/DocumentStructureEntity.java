package com.contract.management.infrastructure.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 文档结构实体类
 *
 * @author SaltyFish
 * @since 1.0.0
 */
@Data
@TableName("document_structure")
public class DocumentStructureEntity {

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
     * 任务ID（冗余字段）
     */
    @TableField("task_id")
    private Long taskId;

    /**
     * 总页数
     */
    @TableField("total_pages")
    private Integer totalPages;

    /**
     * 文档类型
     */
    @TableField("document_type")
    private String documentType;

    /**
     * 合同双方信息（JSON格式）
     */
    @TableField("parties")
    private String parties;

    /**
     * 合同日期
     */
    @TableField("contract_date")
    private LocalDateTime contractDate;

    /**
     * 生效日期
     */
    @TableField("effective_date")
    private LocalDateTime effectiveDate;

    /**
     * 结构信息（JSON格式）
     */
    @TableField("structure_info")
    private String structureInfo;

    /**
     * 特殊区域标记（JSON格式）
     */
    @TableField("special_areas")
    private String specialAreas;

    /**
     * 质量指标（JSON格式）
     */
    @TableField("quality_metrics")
    private String qualityMetrics;

    /**
     * 创建时间
     */
    @TableField(value = "created_time", fill = FieldFill.INSERT)
    private LocalDateTime createdTime;
}