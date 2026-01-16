package com.contract.management.domain.model;

import com.contract.management.domain.model.valueobject.AuditInfo;
import com.contract.management.domain.model.valueobject.ClauseExtractionResult;
import com.contract.management.domain.model.valueobject.ExtractionId;
import com.contract.management.domain.model.valueobject.ExtractionStatus;
import lombok.Getter;

import java.time.LocalDateTime;

/**
 * 条款抽取聚合根
 *
 * @author SaltyFish
 * @since 1.0.0
 */
@Getter
public class ClauseExtraction {

    private ExtractionId id;
    private final Long contractId;
    private ExtractionStatus status;
    private String errorMessage;
    private ClauseExtractionResult result;
    private AuditInfo auditInfo;
    private LocalDateTime startedAt;
    private LocalDateTime completedAt;

    /**
     * 构造函数 - 创建新的条款抽取任务（ID由数据库生成）
     */
    public ClauseExtraction(Long contractId, Long createdBy) {
        if (contractId == null || contractId <= 0) {
            throw new IllegalArgumentException("合同ID必须是正数");
        }

        this.id = null; // ID将由数据库生成
        this.contractId = contractId;
        this.status = ExtractionStatus.PENDING;
        this.errorMessage = null;
        this.result = null;
        this.auditInfo = AuditInfo.create(createdBy);
        this.startedAt = LocalDateTime.now();
        this.completedAt = null;
    }

    
    /**
     * 构造函数 - 从数据库重建
     */
    public ClauseExtraction(ExtractionId id, Long contractId, ExtractionStatus status,
                           String errorMessage, ClauseExtractionResult result,
                           AuditInfo auditInfo, LocalDateTime startedAt, LocalDateTime completedAt) {
        this.id = id;
        this.contractId = contractId;
        this.status = status;
        this.errorMessage = errorMessage;
        this.result = result;
        this.auditInfo = auditInfo;
        this.startedAt = startedAt;
        this.completedAt = completedAt;
    }

    /**
     * 设置数据库生成的ID
     * 此方法仅供Repository层在保存后调用，用于设置数据库生成的ID
     */
    public void setId(ExtractionId id) {
        if (this.id != null) {
            throw new IllegalStateException("ID已经设置，不能重复设置");
        }
        this.id = id;
    }

    // ==================== 领域行为方法 ====================

    /**
     * 开始处理
     */
    public void startProcessing() {
        validateStatusTransition(ExtractionStatus.PROCESSING);
        this.status = ExtractionStatus.PROCESSING;
        this.auditInfo = this.auditInfo.update();
    }

    /**
     * 处理成功
     */
    public void complete(ClauseExtractionResult result) {
        if (result == null) {
            throw new IllegalArgumentException("抽取结果不能为空");
        }

        validateStatusTransition(ExtractionStatus.COMPLETED);
        this.status = ExtractionStatus.COMPLETED;
        this.result = result;
        this.completedAt = LocalDateTime.now();
        this.errorMessage = null;
        this.auditInfo = this.auditInfo.update();
    }

    /**
     * 处理失败
     */
    public void fail(String errorMessage) {
        if (errorMessage == null || errorMessage.trim().isEmpty()) {
            throw new IllegalArgumentException("错误信息不能为空");
        }

        validateStatusTransition(ExtractionStatus.FAILED);
        this.status = ExtractionStatus.FAILED;
        this.errorMessage = errorMessage.trim();
        this.completedAt = LocalDateTime.now();
        this.result = null;
        this.auditInfo = this.auditInfo.update();
    }

    /**
     * 取消处理
     */
    public void cancel() {
        validateStatusTransition(ExtractionStatus.CANCELLED);
        this.status = ExtractionStatus.CANCELLED;
        this.completedAt = LocalDateTime.now();
        this.result = null;
        this.auditInfo = this.auditInfo.update();
    }

    /**
     * 重置任务以便重试
     */
    public void resetForRetry() {
        if (!canRetry()) {
            throw new IllegalStateException("当前状态不允许重试: " + this.status);
        }

        this.status = ExtractionStatus.PENDING;
        this.errorMessage = null;
        this.result = null;
        this.completedAt = null;
        this.auditInfo = this.auditInfo.update();
    }

    // ==================== 业务规则验证方法 ====================

    /**
     * 检查是否正在处理中
     */
    public boolean isProcessing() {
        return this.status == ExtractionStatus.PROCESSING;
    }

    /**
     * 检查是否已完成
     */
    public boolean isCompleted() {
        return this.status == ExtractionStatus.COMPLETED;
    }

    /**
     * 检查是否失败
     */
    public boolean isFailed() {
        return this.status == ExtractionStatus.FAILED;
    }

    /**
     * 检查是否为终态
     */
    public boolean isInFinalState() {
        return this.status.isFinalState();
    }

    /**
     * 检查是否可以重试
     * 只有失败或已取消的任务可以重试
     */
    public boolean canRetry() {
        return this.status == ExtractionStatus.FAILED || this.status == ExtractionStatus.CANCELLED;
    }

    /**
     * 检查是否正在进行中
     */
    public boolean isInProgress() {
        return this.status == ExtractionStatus.PENDING || this.status == ExtractionStatus.PROCESSING;
    }

    /**
     * 获取处理耗时
     */
    public Long getProcessingDurationMs() {
        if (startedAt == null) {
            return null;
        }

        LocalDateTime endTime = completedAt != null ? completedAt : LocalDateTime.now();
        return java.time.Duration.between(startedAt, endTime).toMillis();
    }

    /**
     * 验证状态转换是否合法
     */
    private void validateStatusTransition(ExtractionStatus newStatus) {
        if (!this.status.canTransitionTo(newStatus)) {
            throw new IllegalStateException(
                String.format("无法从状态 %s 转换到状态 %s", this.status, newStatus)
            );
        }
    }

    // ==================== 辅助方法 ====================

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ClauseExtraction that = (ClauseExtraction) o;
        return id != null && id.equals(that.id);
    }

    @Override
    public int hashCode() {
        return id != null ? id.hashCode() : 0;
    }

    @Override
    public String toString() {
        return "ClauseExtraction{" +
                "id=" + id +
                ", contractId=" + contractId +
                ", status=" + status +
                ", startedAt=" + startedAt +
                ", completedAt=" + completedAt +
                '}';
    }
}