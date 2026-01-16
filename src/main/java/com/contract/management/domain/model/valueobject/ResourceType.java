package com.contract.management.domain.model.valueobject;

import lombok.Getter;

/**
 * 资源类型枚举
 *
 * @author SaltyFish
 * @since 1.0.0
 */
@Getter
public enum ResourceType {

    /**
     * 合同资源
     */
    CONTRACT("合同"),

    /**
     * 条款抽取资源
     */
    CLAUSE_EXTRACTION("条款抽取"),

    /**
     * 文件资源
     */
    FILE("文件"),

    /**
     * 用户资源
     */
    USER("用户"),

    /**
     * 系统资源
     */
    SYSTEM("系统");

    private final String description;

    ResourceType(String description) {
        this.description = description;
    }

    /**
     * 根据描述获取资源类型
     */
    public static ResourceType fromDescription(String description) {
        for (ResourceType type : values()) {
            if (type.description.equals(description)) {
                return type;
            }
        }
        return null;
    }
}