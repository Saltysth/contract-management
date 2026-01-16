package com.contract.management.domain.model;

/**
 * 条款类型枚举
 *
 * @author SaltyFish
 * @since 1.0.0
 */
public enum ClauseType {
    /**
     * 付款条款
     */
    PAYMENT("付款条款"),

    /**
     * 交付条款
     */
    DELIVERY("交付条款"),

    /**
     * 违约责任条款
     */
    BREACH("违约责任条款"),

    /**
     * 保密条款
     */
    CONFIDENTIALITY("保密条款"),

    /**
     * 知识产权条款
     */
    INTELLECTUAL_PROPERTY("知识产权条款"),

    /**
     * 争议解决条款
     */
    DISPUTE_RESOLUTION("争议解决条款"),

    /**
     * 不可抗力条款
     */
    FORCE_MAJEURE("不可抗力条款"),

    /**
     * 终止条款
     */
    TERMINATION("终止条款"),

    /**
     * 担保条款
     */
    WARRANTY("担保条款"),

    /**
     * 赔偿条款
     */
    INDEMNITY("赔偿条款"),

    /**
     * 其他条款
     */
    OTHER("其他条款");

    private final String description;

    ClauseType(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }

    /**
     * 根据字符串获取条款类型
     *
     * @param type 类型字符串
     * @return 条款类型
     */
    public static ClauseType fromString(String type) {
        if (type == null || type.trim().isEmpty()) {
            return OTHER;
        }

        try {
            return ClauseType.valueOf(type.toUpperCase().trim());
        } catch (IllegalArgumentException e) {
            return OTHER;
        }
    }
}