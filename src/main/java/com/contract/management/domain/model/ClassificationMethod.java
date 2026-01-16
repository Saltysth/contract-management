package com.contract.management.domain.model;

/**
 * 合同分类方法枚举
 *
 * @author SaltyFish
 * @since 1.0.0
 */
public enum ClassificationMethod {
    /**
     * 人工智能分类
     */
    AI("AI", "人工智能分类"),

    /**
     * 人工分类
     */
    MANUAL("MANUAL", "人工分类"),

    /**
     * 规则分类
     */
    RULE_BASED("RULE_BASED", "规则分类");

    private final String code;
    private final String description;

    ClassificationMethod(String code, String description) {
        this.code = code;
        this.description = description;
    }

    public String getCode() {
        return code;
    }

    public String getDescription() {
        return description;
    }
}