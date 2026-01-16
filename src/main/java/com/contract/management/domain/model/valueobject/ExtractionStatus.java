package com.contract.management.domain.model.valueobject;

import lombok.Getter;

/**
 * 条款抽取状态枚举
 *
 * @author SaltyFish
 * @since 1.0.0
 */
@Getter
public enum ExtractionStatus {

    /**
     * 待处理 - 已提交请求，等待处理
     */
    PENDING("待处理"),

    /**
     * 处理中 - AI模型正在分析
     */
    PROCESSING("处理中"),

    /**
     * 已完成 - 成功完成条款抽取
     */
    COMPLETED("已完成"),

    /**
     * 失败 - 抽取过程中发生错误
     */
    FAILED("失败"),

    /**
     * 已取消 - 用户取消或超时
     */
    CANCELLED("已取消");

    private final String description;

    ExtractionStatus(String description) {
        this.description = description;
    }

    /**
     * 检查是否为终态
     */
    public boolean isFinalState() {
        return this == COMPLETED || this == FAILED || this == CANCELLED;
    }

    /**
     * 检查是否可以进行状态转换
     */
    public boolean canTransitionTo(ExtractionStatus newStatus) {
        if (this == newStatus) {
            return true;
        }

        // 终态不能转换到其他状态
        if (this.isFinalState()) {
            return false;
        }

        // 定义允许的状态转换
        switch (this) {
            case PENDING:
                return newStatus == PROCESSING || newStatus == CANCELLED;
            case PROCESSING:
                return newStatus == COMPLETED || newStatus == FAILED || newStatus == CANCELLED;
            default:
                return false;
        }
    }
}