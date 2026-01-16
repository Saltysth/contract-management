package com.contract.management.domain.model.valueobject;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

/**
 * 提示词内容值对象
 *
 * @author SaltyFish
 * @since 1.0.0
 */
@Getter
@EqualsAndHashCode
@ToString
public class PromptContent {

    private final String value;

    public PromptContent(String value) {
        if (value == null) {
            this.value = "";
        } else {
            this.value = value.trim();
        }
    }

    /**
     * 创建提示词内容
     *
     * @param value 内容值
     * @return PromptContent实例
     */
    public static PromptContent of(String value) {
        return new PromptContent(value);
    }

    /**
     * 检查内容是否为空
     *
     * @return 是否为空
     */
    public boolean isEmpty() {
        return value == null || value.trim().isEmpty();
    }

    /**
     * 获取内容长度
     *
     * @return 内容长度
     */
    public int length() {
        return value != null ? value.length() : 0;
    }
}