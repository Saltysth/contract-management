package com.contract.management.domain.model.valueobject;

import lombok.EqualsAndHashCode;
import lombok.Getter;

/**
 * 规则名称值对象
 *
 * @author SaltyFish
 * @since 1.0.0
 */
@Getter
@EqualsAndHashCode
public class RuleName {
    private final String value;

    public RuleName(String value) {
        if (value == null || value.trim().isEmpty()) {
            throw new IllegalArgumentException("Rule name cannot be null or empty");
        }
        if (value.length() > 255) {
            throw new IllegalArgumentException("Rule name cannot exceed 255 characters");
        }
        this.value = value.trim();
    }

    /**
     * 创建规则名称
     */
    public static RuleName of(String value) {
        return new RuleName(value);
    }

    @Override
    public String toString() {
        return value;
    }
}