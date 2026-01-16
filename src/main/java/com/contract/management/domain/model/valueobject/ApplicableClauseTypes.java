package com.contract.management.domain.model.valueobject;

import lombok.EqualsAndHashCode;
import lombok.Getter;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;

/**
 * 适用条款类型值对象
 * 用于PostgreSQL数组字段的安全类型处理
 *
 * @author SaltyFish
 * @since 1.0.0
 */
@Getter
@EqualsAndHashCode
public class ApplicableClauseTypes {

    private final List<String> values;

    public ApplicableClauseTypes(List<String> values) {
        if (values == null || values.isEmpty()) {
            throw new IllegalArgumentException("Applicable clause types cannot be null or empty");
        }

        // 验证每个元素都不为空
        for (String value : values) {
            if (value == null || value.trim().isEmpty()) {
                throw new IllegalArgumentException("Clause type cannot be null or empty");
            }
        }

        // 创建不可变副本
        this.values = List.copyOf(values);
    }

    /**
     * 创建适用条款类型
     */
    public static ApplicableClauseTypes of(List<String> values) {
        return new ApplicableClauseTypes(values);
    }

    /**
     * 创建适用条款类型（数组参数）
     */
    public static ApplicableClauseTypes of(String... values) {
        if (values == null || values.length == 0) {
            throw new IllegalArgumentException("Applicable clause types cannot be null or empty");
        }
        return new ApplicableClauseTypes(Arrays.asList(values));
    }

    /**
     * 检查是否适用于指定条款类型
     */
    public boolean appliesTo(String clauseType) {
        if (clauseType == null || clauseType.trim().isEmpty()) {
            return false;
        }
        return values.contains(clauseType);
    }

    /**
     * 获取大小
     */
    public int size() {
        return values.size();
    }

    /**
     * 检查是否为空
     */
    public boolean isEmpty() {
        return values.isEmpty();
    }

    /**
     * 获取第一个值
     */
    public String getFirst() {
        return values.isEmpty() ? null : values.get(0);
    }

    /**
     * 检查是否包含指定类型
     */
    public boolean contains(String clauseType) {
        return clauseType != null && values.contains(clauseType);
    }

    @Override
    public String toString() {
        return "ApplicableClauseTypes{" + values + "}";
    }
}