package com.contract.management.domain.model.valueobject;

import lombok.EqualsAndHashCode;
import lombok.Getter;

import java.util.Objects;

/**
 * 条款标题值对象
 *
 * @author SaltyFish
 * @since 1.0.0
 */
@Getter
@EqualsAndHashCode
public class ClauseTitle {

    private final String value;

    public ClauseTitle(String value) {
        if (value != null && value.length() > 255) {
            throw new IllegalArgumentException("条款标题长度不能超过255个字符");
        }
        this.value = value; // 允许为空
    }

    /**
     * 创建新的条款标题
     *
     * @param value 标题值
     * @return 条款标题实例
     */
    public static ClauseTitle of(String value) {
        return new ClauseTitle(value);
    }

    /**
     * 检查是否为空
     *
     * @return 是否为空
     */
    public boolean isEmpty() {
        return value == null || value.trim().isEmpty();
    }

    @Override
    public String toString() {
        return "ClauseTitle{" + "value='" + value + '\'' + '}';
    }
}