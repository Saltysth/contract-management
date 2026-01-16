package com.contract.management.domain.model;

import lombok.EqualsAndHashCode;
import lombok.Getter;

import java.util.Objects;

/**
 * 条款ID值对象
 *
 * @author SaltyFish
 * @since 1.0.0
 */
@Getter
@EqualsAndHashCode
public class ClauseId {

    private final Long value;

    public ClauseId(Long value) {
        if (value == null || value <= 0) {
            throw new IllegalArgumentException("条款ID必须为正数");
        }
        this.value = value;
    }

    /**
     * 创建新的条款ID
     *
     * @param value ID值
     * @return 条款ID实例
     */
    public static ClauseId of(Long value) {
        return new ClauseId(value);
    }

    @Override
    public String toString() {
        return "ClauseId{" + "value=" + value + '}';
    }
}