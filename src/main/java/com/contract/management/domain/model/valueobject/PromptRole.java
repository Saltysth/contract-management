package com.contract.management.domain.model.valueobject;

import lombok.Getter;

/**
 * 提示词角色枚举
 *
 * @author SaltyFish
 * @since 1.0.0
 */
@Getter
public enum PromptRole {
    /**
     * 系统角色
     */
    SYSTEM("SYSTEM", "系统"),

    /**
     * 用户角色
     */
    USER("USER", "用户"),

    /**
     * 助手角色
     */
    ASSISTANT("ASSISTANT", "助手");

    private final String code;
    private final String description;

    PromptRole(String code, String description) {
        this.code = code;
        this.description = description;
    }

    /**
     * 根据代码获取枚举
     *
     * @param code 代码
     * @return PromptRole枚举
     */
    public static PromptRole fromCode(String code) {
        for (PromptRole role : values()) {
            if (role.code.equals(code)) {
                return role;
            }
        }
        throw new IllegalArgumentException("未知的提示词角色: " + code);
    }
}