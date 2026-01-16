package com.contract.management.domain.model.valueobject;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 审查规则类型枚举
 *
 * @author SaltyFish
 * @since 1.0.0
 */
@Getter
@AllArgsConstructor
public enum RuleType {
    /**
     * 专属规则（合同/条款类型专属）
     */
    SPECIFIC("specific", "专属规则"),

    /**
     * 兜底规则（所有合同通用）
     */
    FALLBACK("fallback", "兜底规则"),

    /**
     * 扩展规则（仅严格模式启用）
     */
    EXTENDED("extended", "扩展规则");

    private final String code;
    private final String description;

    /**
     * 根据代码获取枚举
     */
    public static RuleType fromCode(String code) {
        if (code == null) {
            return null;
        }
        for (RuleType type : values()) {
            if (type.code.equals(code)) {
                return type;
            }
        }
        throw new IllegalArgumentException("Unknown rule type code: " + code);
    }
}