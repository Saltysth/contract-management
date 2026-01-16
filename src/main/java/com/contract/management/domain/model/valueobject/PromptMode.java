package com.contract.management.domain.model.valueobject;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 提示词模式枚举
 *
 * @author SaltyFish
 * @since 1.0.0
 */
@Getter
@AllArgsConstructor
public enum PromptMode {
    /**
     * 效率模式
     */
    EFFICIENCY((short) 0, "效率模式"),

    /**
     * 标准模式
     */
    STANDARD((short) 1, "标准模式"),

    /**
     * 严格模式
     */
    STRICT((short) 2, "严格模式");

    private final short code;
    private final String description;

    /**
     * 根据代码获取枚举
     */
    public static PromptMode fromCode(short code) {
        for (PromptMode mode : values()) {
            if (mode.code == code) {
                return mode;
            }
        }
        throw new IllegalArgumentException("Unknown prompt mode code: " + code);
    }

    /**
     * 检查指定代码的规则是否在当前模式下生效
     * 规则：某模式下生效的规则 = prompt_mode_code ≤ 目标模式编码
     */
    public boolean isRuleApplicable(short ruleModeCode) {
        return ruleModeCode <= this.code;
    }
}