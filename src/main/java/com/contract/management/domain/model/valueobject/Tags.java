package com.contract.management.domain.model.valueobject;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * 标签集合值对象
 *
 * @author SaltyFish
 * @since 1.0.0
 */
@Getter
@EqualsAndHashCode
@ToString
public class Tags {
    
    private static final int MAX_TAG_LENGTH = 50;
    private static final int MAX_TAGS_COUNT = 20;
    
    private final Set<String> values;
    
    public Tags(String tagsString) {
        if (tagsString == null || tagsString.trim().isEmpty()) {
            this.values = new HashSet<>();
        } else {
            this.values = Arrays.stream(tagsString.split(","))
                    .map(String::trim)
                    .filter(tag -> !tag.isEmpty())
                    .map(this::validateTag)
                    .collect(Collectors.toSet());
        }
        
        validateTagsCount();
    }
    
    public Tags(Set<String> tags) {
        if (tags == null) {
            this.values = new HashSet<>();
        } else {
            this.values = tags.stream()
                    .filter(Objects::nonNull)
                    .map(String::trim)
                    .filter(tag -> !tag.isEmpty())
                    .map(this::validateTag)
                    .collect(Collectors.toSet());
        }
        
        validateTagsCount();
    }
    
    /**
     * 验证单个标签
     */
    private String validateTag(String tag) {
        if (tag.length() > MAX_TAG_LENGTH) {
            throw new IllegalArgumentException("标签长度不能超过" + MAX_TAG_LENGTH + "个字符: " + tag);
        }
        
        // 检查标签是否包含非法字符
        if (tag.contains(",") || tag.contains(";")) {
            throw new IllegalArgumentException("标签不能包含逗号或分号: " + tag);
        }
        
        return tag;
    }
    
    /**
     * 验证标签数量
     */
    private void validateTagsCount() {
        if (values.size() > MAX_TAGS_COUNT) {
            throw new IllegalArgumentException("标签数量不能超过" + MAX_TAGS_COUNT + "个");
        }
    }
    
    /**
     * 创建标签集合
     *
     * @param tagsString 标签字符串
     * @return 标签集合实例
     */
    public static Tags of(String tagsString) {
        return new Tags(tagsString);
    }
    
    /**
     * 创建标签集合
     *
     * @param tags 标签集合
     * @return 标签集合实例
     */
    public static Tags of(Set<String> tags) {
        return new Tags(tags);
    }
    
    /**
     * 创建空标签集合
     *
     * @return 空标签集合实例
     */
    public static Tags empty() {
        return new Tags(new HashSet<>());
    }
    
    /**
     * 添加标签
     *
     * @param tag 标签
     * @return 新的标签集合
     */
    public Tags addTag(String tag) {
        if (tag == null || tag.trim().isEmpty()) {
            return this;
        }
        
        String trimmedTag = tag.trim();
        if (values.contains(trimmedTag)) {
            return this;
        }
        
        Set<String> newTags = new HashSet<>(this.values);
        newTags.add(trimmedTag);
        return new Tags(newTags);
    }
    
    /**
     * 移除标签
     *
     * @param tag 标签
     * @return 新的标签集合
     */
    public Tags removeTag(String tag) {
        if (tag == null || !values.contains(tag.trim())) {
            return this;
        }
        
        Set<String> newTags = new HashSet<>(this.values);
        newTags.remove(tag.trim());
        return new Tags(newTags);
    }
    
    /**
     * 检查是否包含标签
     *
     * @param tag 标签
     * @return 是否包含
     */
    public boolean contains(String tag) {
        return tag != null && values.contains(tag.trim());
    }
    
    /**
     * 检查是否为空
     *
     * @return 是否为空
     */
    public boolean isEmpty() {
        return values.isEmpty();
    }
    
    /**
     * 获取标签数量
     *
     * @return 标签数量
     */
    public int size() {
        return values.size();
    }
    
    /**
     * 转换为逗号分隔的字符串
     *
     * @return 逗号分隔的字符串
     */
    public String toCommaSeparatedString() {
        return String.join(",", values);
    }
    
    /**
     * 获取标签值的副本
     *
     * @return 标签值集合
     */
    public Set<String> getValues() {
        return new HashSet<>(values);
    }
}