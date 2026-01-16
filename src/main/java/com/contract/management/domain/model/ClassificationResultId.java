package com.contract.management.domain.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

/**
 * 合同分类结果ID值对象
 *
 * @author SaltyFish
 * @since 1.0.0
 */
@Getter
@EqualsAndHashCode
@ToString
public class ClassificationResultId {
    private final Long value;

    public ClassificationResultId(Long value) {
        if (value == null || value <= 0) {
            throw new IllegalArgumentException("分类结果ID必须是正数");
        }
        this.value = value;
    }

    public static ClassificationResultId of(Long value) {
        return new ClassificationResultId(value);
    }

    @JsonCreator
    public static ClassificationResultId fromValue(Number value) {
        return new ClassificationResultId(value != null ? value.longValue() : null);
    }

    @JsonValue
    public Long toValue() {
        return value;
    }
}