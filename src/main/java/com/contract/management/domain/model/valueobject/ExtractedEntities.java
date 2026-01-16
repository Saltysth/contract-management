package com.contract.management.domain.model.valueobject;

import lombok.EqualsAndHashCode;
import lombok.Getter;

import java.util.Collections;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

/**
 * 提取的实体值对象
 * 用于安全地处理从合同文本中提取的实体信息
 * 替换原来的Map<String, String>实现，提供类型安全和业务语义
 *
 * @author SaltyFish
 * @since 1.0.0
 */
@Getter
@EqualsAndHashCode
public class ExtractedEntities {

    private final Map<String, String> entities;

    public ExtractedEntities(Map<String, String> entities) {
        if (entities == null) {
            this.entities = Collections.emptyMap();
        } else {
            // 创建不可变副本
            this.entities = Map.copyOf(entities);
        }
    }

    /**
     * 创建提取实体
     */
    public static ExtractedEntities of(Map<String, String> entities) {
        return new ExtractedEntities(entities);
    }

    /**
     * 创建空的提取实体
     */
    public static ExtractedEntities empty() {
        return new ExtractedEntities(Collections.emptyMap());
    }

    /**
     * 创建包含单个实体的提取实体
     */
    public static ExtractedEntities of(String key, String value) {
        return new ExtractedEntities(Map.of(key, value));
    }

    /**
     * 获取实体值
     */
    public String get(String key) {
        return entities.get(key);
    }

    /**
     * 获取实体值，如果不存在则返回默认值
     */
    public String getOrDefault(String key, String defaultValue) {
        return entities.getOrDefault(key, defaultValue);
    }

    /**
     * 检查是否包含指定实体
     */
    public boolean containsKey(String key) {
        return entities.containsKey(key);
    }

    /**
     * 检查是否包含指定值
     */
    public boolean containsValue(String value) {
        return entities.containsValue(value);
    }

    /**
     * 获取所有实体键
     */
    public Set<String> keySet() {
        return entities.keySet();
    }

    /**
     * 获取所有实体值
     */
    public java.util.Collection<String> values() {
        return entities.values();
    }

    /**
     * 获取所有实体条目
     */
    public Set<Map.Entry<String, String>> entrySet() {
        return entities.entrySet();
    }

    /**
     * 检查是否为空
     */
    public boolean isEmpty() {
        return entities.isEmpty();
    }

    /**
     * 获取实体数量
     */
    public int size() {
        return entities.size();
    }

    /**
     * 获取底层的Map（用于转换器使用）
     */
    public Map<String, String> toMap() {
        return entities;
    }

    @Override
    public String toString() {
        return "ExtractedEntities{" + entities + "}";
    }
}