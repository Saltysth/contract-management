package com.contract.management.domain.model.valueobject;

import lombok.EqualsAndHashCode;
import lombok.Getter;

/**
 * 规则内容值对象
 *
 * @author SaltyFish
 * @since 1.0.0
 */
@Getter
@EqualsAndHashCode
public class RuleContent {
    private final String value;

    public RuleContent(String value) {
        if (value == null || value.trim().isEmpty()) {
            throw new IllegalArgumentException("Rule content cannot be null or empty");
        }
        this.value = value.trim();
    }

    /**
     * 创建规则内容
     */
    public static RuleContent of(String value) {
        return new RuleContent(value);
    }

    @Override
    public String toString() {
        return value;
    }
}