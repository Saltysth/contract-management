package com.contract.management.domain.model.valueobject;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

/**
 * 提示词模板名称值对象
 *
 * @author SaltyFish
 * @since 1.0.0
 */
@Getter
@EqualsAndHashCode
@ToString
public class PromptName {

    private static final int MAX_LENGTH = 120;

    private final String value;

    public PromptName(String value) {
        if (value == null || value.trim().isEmpty()) {
            throw new IllegalArgumentException("提示词模板名称不能为空");
        }

        String trimmedValue = value.trim();
        if (trimmedValue.length() > MAX_LENGTH) {
            throw new IllegalArgumentException("提示词模板名称长度不能超过" + MAX_LENGTH + "个字符");
        }

        this.value = trimmedValue;
    }

    /**
     * 创建提示词模板名称
     *
     * @param value 名称值
     * @return PromptName实例
     */
    public static PromptName of(String value) {
        return new PromptName(value);
    }
}