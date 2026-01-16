package com.contract.management.domain.model.valueobject;

import lombok.EqualsAndHashCode;
import lombok.Getter;

import java.util.Objects;

/**
 * 条款抽取标识值对象
 *
 * @author SaltyFish
 * @since 1.0.0
 */
@Getter
@EqualsAndHashCode
public class ExtractionId {

    private final Long value;

    public ExtractionId(Long value) {
        if (value == null || value <= 0) {
            throw new IllegalArgumentException("条款抽取ID必须是正数");
        }
        this.value = value;
    }

    /**
     * 静态工厂方法
     */
    public static ExtractionId of(Long value) {
        return new ExtractionId(value);
    }

    @Override
    public String toString() {
        return "ExtractionId{" + value + "}";
    }
}