package com.contract.management.domain.model;

/**
 * 风险等级枚举
 *
 * @author SaltyFish
 * @since 1.0.0
 */
public enum RiskLevel {
    /**
     * 低风险
     */
    LOW("低风险"),

    /**
     * 中等风险
     */
    MEDIUM("中等风险"),

    /**
     * 高风险
     */
    HIGH("高风险"),

    /**
     * 极高风险
     */
    CRITICAL("极高风险"),

    /**
     * 未知风险
     */
    UNKNOWN("未知风险");

    private final String description;

    RiskLevel(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }

    /**
     * 根据字符串获取风险等级
     *
     * @param level 风险等级字符串
     * @return 风险等级
     */
    public static RiskLevel fromString(String level) {
        if (level == null || level.trim().isEmpty()) {
            return UNKNOWN;
        }

        try {
            return RiskLevel.valueOf(level.toUpperCase().trim());
        } catch (IllegalArgumentException e) {
            return UNKNOWN;
        }
    }
}