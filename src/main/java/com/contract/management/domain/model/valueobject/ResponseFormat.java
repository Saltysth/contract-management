package com.contract.management.domain.model.valueobject;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

/**
 * 响应格式值对象
 *
 * @author SaltyFish
 * @since 1.0.0
 */
@Getter
@EqualsAndHashCode
@ToString
public class ResponseFormat {

    private static final int MAX_LENGTH = 30;

    private final String value;

    public ResponseFormat(String value) {
        if (value != null) {
            String trimmedValue = value.trim();
            if (trimmedValue.length() > MAX_LENGTH) {
                throw new IllegalArgumentException("响应格式长度不能超过" + MAX_LENGTH + "个字符");
            }
            this.value = trimmedValue;
        } else {
            this.value = null;
        }
    }

    /**
     * 创建响应格式
     *
     * @param value 格式值
     * @return ResponseFormat实例
     */
    public static ResponseFormat of(String value) {
        return new ResponseFormat(value);
    }

    /**
     * 检查是否为空
     *
     * @return 是否为空
     */
    public boolean isEmpty() {
        return value == null || value.trim().isEmpty();
    }

    /**
     * 获取默认的markdown格式
     *
     * @return ResponseFormat实例
     */
    public static ResponseFormat markdown() {
        return new ResponseFormat("markdown");
    }

    /**
     * 获取JSON格式
     *
     * @return ResponseFormat实例
     */
    public static ResponseFormat json() {
        return new ResponseFormat("json");
    }

    /**
     * 获取纯文本格式
     *
     * @return ResponseFormat实例
     */
    public static ResponseFormat plainText() {
        return new ResponseFormat("text");
    }
}