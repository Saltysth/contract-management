package com.contract.management.domain.model.valueobject;

import lombok.EqualsAndHashCode;
import lombok.Getter;

import java.util.Objects;

/**
 * 审查规则ID值对象
 *
 * @author SaltyFish
 * @since 1.0.0
 */
@Getter
@EqualsAndHashCode
public class ReviewRuleId {
    private final Long value;

    public ReviewRuleId(Long value) {
        if (value == null) {
            throw new IllegalArgumentException("Review rule ID cannot be null");
        }
        this.value = value;
    }

    /**
     * 创建审查规则ID
     */
    public static ReviewRuleId of(Long value) {
        return new ReviewRuleId(value);
    }

    @Override
    public String toString() {
        return "ReviewRuleId{" + value + "}";
    }
}