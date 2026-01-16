package com.contract.management.domain.model;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

/**
 * 提示词模板ID值对象
 *
 * @author SaltyFish
 * @since 1.0.0
 */
@Getter
@EqualsAndHashCode
@ToString
public class PromptId {

    private final Long value;

    public PromptId(Long value) {
        if (value == null || value <= 0) {
            throw new IllegalArgumentException("提示词模板ID必须是正数");
        }
        this.value = value;
    }

    /**
     * 从Long值创建PromptId
     *
     * @param value Long值
     * @return PromptId实例
     */
    public static PromptId of(Long value) {
        return new PromptId(value);
    }
}