package com.contract.management.domain.model.valueobject;

import lombok.EqualsAndHashCode;
import lombok.Getter;

/**
 * 条款内容值对象
 *
 * @author SaltyFish
 * @since 1.0.0
 */
@Getter
@EqualsAndHashCode
public class ClauseContent {

    private final String value;

    public ClauseContent(String value) {
        if (value == null || value.trim().isEmpty()) {
            throw new IllegalArgumentException("条款内容不能为空");
        }
        this.value = value.trim();
    }

    /**
     * 创建新的条款内容
     *
     * @param value 内容值
     * @return 条款内容实例
     */
    public static ClauseContent of(String value) {
        return new ClauseContent(value);
    }

    /**
     * 获取内容长度
     *
     * @return 内容长度
     */
    public int getLength() {
        return value != null ? value.length() : 0;
    }

    /**
     * 检查内容是否包含指定文本
     *
     * @param text 搜索文本
     * @return 是否包含
     */
    public boolean contains(String text) {
        return value != null && text != null && value.contains(text);
    }

    @Override
    public String toString() {
        return "ClauseContent{" +
                "value='" + (value != null && value.length() > 50 ?
                    value.substring(0, 50) + "..." : value) + '\'' +
                '}';
    }
}