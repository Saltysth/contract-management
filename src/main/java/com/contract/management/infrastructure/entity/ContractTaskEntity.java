package com.contract.management.infrastructure.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 合同任务实体
 *
 * @author SaltyFish
 * @since 1.0.0
 */
@Data
@TableName("contract_task")
public class ContractTaskEntity {

    /**
     * 主键ID
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 任务ID
     */
    private Long taskId;

    /**
     * 合同ID
     */
    private Long contractId;

    /**
     * 文件UUID
     */
    private String fileUuid;

    /**
     * 业务标签
     */
    private String businessTags;

    /**
     * 审查类型
     */
    private String reviewType;

    /**
     * 自定义选择的审查类型
     */
    private String customSelectedReviewTypes;

    /**
     * 行业
     */
    private String industry;

    /**
     * 币种
     */
    private String currency;

    /**
     * 合同类型
     */
    private String contractType;

    /**
     * 类型置信度
     */
    private BigDecimal typeConfidence;

    /**
     * 审查规则
     */
    private String reviewRules;

    /**
     * 提示模板
     */
    private String promptTemplate;

    /**
     * 结果数据
     */
    private String resultData;

    /**
     * 是否启用术语
     */
    private Boolean enableTerminology;

    /**
     * 创建人
     */
    @TableField(fill = FieldFill.INSERT)
    private Long createdBy;

    /**
     * 创建时间
     */
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createdTime;

    /**
     * 更新人
     */
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private Long updatedBy;

    /**
     * 更新时间
     */
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updatedTime;

    /**
     * 对象版本号
     */
    @TableField(fill = FieldFill.INSERT)
    private Long objectVersionNumber;
}