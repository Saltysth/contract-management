package com.contract.management.infrastructure.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 合同实体类
 *
 * @author SaltyFish
 * @since 1.0.0
 */
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("contract")
public class ContractEntity {

    /**
     * 合同ID
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 合同名称
     */
    @TableField("contract_name")
    private String contractName;

    /**
     * 合同类型
     */
    @TableField("contract_type")
    private String contractType;

    
    /**
     * 甲方名称
     */
    @TableField("party_a_name")
    private String partyAName;

    /**
     * 甲方联系方式
     */
    @TableField("party_a_contact")
    private String partyAContact;

    /**
     * 甲方地址
     */
    @TableField("party_a_address")
    private String partyAAddress;

    /**
     * 乙方名称
     */
    @TableField("party_b_name")
    private String partyBName;

    /**
     * 乙方联系方式
     */
    @TableField("party_b_contact")
    private String partyBContact;

    /**
     * 乙方地址
     */
    @TableField("party_b_address")
    private String partyBAddress;

    /**
     * 合同金额
     */
    @TableField("contract_amount")
    private BigDecimal contractAmount;

    /**
     * 签署日期
     */
    @TableField("sign_date")
    private LocalDate signDate;

    /**
     * 生效日期
     */
    @TableField("effective_date")
    private LocalDate effectiveDate;

    /**
     * 到期日期
     */
    @TableField("expiry_date")
    private LocalDate expiryDate;

    /**
     * 附件UUID
     */
    @TableField("attachment_uuid")
    private String attachmentUuid;

    
    /**
     * 合同描述
     */
    @TableField("description")
    private String description;

    /**
     * 创建人ID
     */
    @TableField(value = "created_by", fill = FieldFill.INSERT)
    private Long createdBy;

    /**
     * 更新人ID
     */
    @TableField(value = "updated_by", fill = FieldFill.INSERT_UPDATE)
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