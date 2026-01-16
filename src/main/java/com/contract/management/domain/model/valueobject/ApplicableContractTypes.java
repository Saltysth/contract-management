package com.contract.management.domain.model.valueobject;

import lombok.EqualsAndHashCode;
import lombok.Getter;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

/**
 * 适用合同类型值对象
 * 用于PostgreSQL数组字段的安全类型处理
 *
 * @author SaltyFish
 * @since 1.0.0
 */
@Getter
@EqualsAndHashCode
public class ApplicableContractTypes {

    private final List<String> values;

    public ApplicableContractTypes(List<String> values) {
        if (values == null || values.isEmpty()) {
            throw new IllegalArgumentException("Applicable contract types cannot be null or empty");
        }

        // 验证每个元素都不为空
        for (String value : values) {
            if (value == null || value.trim().isEmpty()) {
                throw new IllegalArgumentException("Contract type cannot be null or empty");
            }
        }

        // 创建不可变副本
        this.values = List.copyOf(values);
    }

    /**
     * 创建适用合同类型
     */
    public static ApplicableContractTypes of(List<String> values) {
        return new ApplicableContractTypes(values);
    }

    /**
     * 创建适用合同类型（数组参数）
     */
    public static ApplicableContractTypes of(String... values) {
        if (values == null || values.length == 0) {
            throw new IllegalArgumentException("Applicable contract types cannot be null or empty");
        }
        return new ApplicableContractTypes(Arrays.asList(values));
    }

    /**
     * 创建兜底规则（适用于所有合同）
     */
    public static ApplicableContractTypes fallback() {
        return new ApplicableContractTypes(Collections.singletonList("all"));
    }

    /**
     * 创建空的适用合同类型（用于数据库查询结果为空的情况）
     */
    public static ApplicableContractTypes empty() {
        return new ApplicableContractTypes(Collections.singletonList("empty"));
    }

    /**
     * 检查是否为兜底规则
     */
    public boolean isFallback() {
        return values.size() == 1 && "all".equalsIgnoreCase(values.get(0));
    }

    /**
     * 检查是否适用于指定合同类型
     */
    public boolean appliesTo(String contractType) {
        if (contractType == null || contractType.trim().isEmpty()) {
            return false;
        }
        return isFallback() || values.contains(contractType);
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

    @Override
    public String toString() {
        return "ApplicableContractTypes{" + values + "}";
    }
}