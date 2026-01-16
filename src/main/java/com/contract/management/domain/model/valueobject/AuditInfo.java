package com.contract.management.domain.model.valueobject;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

import java.time.LocalDateTime;

/**
 * 审计信息值对象
 *
 * @author SaltyFish
 * @since 1.0.0
 */
@Getter
@EqualsAndHashCode
@ToString
public class AuditInfo {
    
    private final Long createdBy;
    private final Long updatedBy;
    private final LocalDateTime createdTime;
    private final LocalDateTime updatedTime;
    private final Long objectVersionNumber;

    public AuditInfo(Long createdBy, LocalDateTime createdTime, LocalDateTime updatedTime, Long updatedBy, Long objectVersionNumber) {
        if (createdBy == null || createdBy <= 0) {
            throw new IllegalArgumentException("创建人ID不能为空或非正数");
        }
        if (createdTime == null) {
            throw new IllegalArgumentException("创建时间不能为空");
        }
        
        this.createdBy = createdBy;
        this.createdTime = createdTime;
        this.updatedTime = updatedTime != null ? updatedTime : createdTime;
        this.updatedBy = updatedBy;
        this.objectVersionNumber = objectVersionNumber;
    }
    
    /**
     * 创建新的审计信息
     *
     * @param createdBy 创建人ID
     * @return 审计信息实例
     */
    public static AuditInfo create(Long createdBy) {
        LocalDateTime now = LocalDateTime.now();
        return new AuditInfo(createdBy, now, now, 1L, 0L);
    }
    
    /**
     * 更新审计信息
     *
     * @return 新的审计信息实例
     */
    public AuditInfo update() {
        return new AuditInfo(this.createdBy, this.createdTime, LocalDateTime.now(), this.updatedBy, this.objectVersionNumber);
    }
    

    /**
     * 检查是否已更新
     *
     * @return 是否已更新
     */
    public boolean isUpdated() {
        return !updatedTime.equals(createdTime);
    }
}