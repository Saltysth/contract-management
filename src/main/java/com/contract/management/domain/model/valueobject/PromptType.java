package com.contract.management.domain.model.valueobject;

import lombok.Getter;

/**
 * 提示词模板类型枚举
 *
 * @author SaltyFish
 * @since 1.0.0
 */
@Getter
public enum PromptType {
    /**
     * 系统内置模板
     */
    INNER("INNER", "系统内置"),

    /**
     * 用户自定义模板
     */
    CUSTOM("CUSTOM", "用户自定义");

    private final String code;
    private final String description;

    PromptType(String code, String description) {
        this.code = code;
        this.description = description;
    }

    /**
     * 根据代码获取枚举
     *
     * @param code 代码
     * @return PromptType枚举
     */
    public static PromptType fromCode(String code) {
        for (PromptType type : values()) {
            if (type.code.equals(code)) {
                return type;
            }
        }
        throw new IllegalArgumentException("未知的提示词模板类型: " + code);
    }
}