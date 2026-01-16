package com.contract.management.domain.model.valueobject;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.EqualsAndHashCode;
import lombok.Getter;

/**
 * 条款位置值对象
 * 用于描述条款在合同文档中的位置信息
 *
 * @author SaltyFish
 * @since 1.0.0
 */
@Getter
@EqualsAndHashCode
@JsonIgnoreProperties(ignoreUnknown = true)
public class ClausePosition {

    /**
     * 页码
     */
    private final Integer page;

    /**
     * 起始坐标 x (左侧位置)
     */
    private final Double startX;

    /**
     * 起始坐标 y (顶部位置)
     */
    private final Double startY;

    /**
     * 结束坐标 x (右侧位置)
     */
    private final Double endX;

    /**
     * 结束坐标 y (底部位置)
     */
    private final Double endY;

    @JsonCreator
    public ClausePosition(@JsonProperty("page") Integer page,
                         @JsonProperty("startX") Double startX,
                         @JsonProperty("startY") Double startY,
                         @JsonProperty("endX") Double endX,
                         @JsonProperty("endY") Double endY) {
        if (page == null || page < 1) {
            throw new IllegalArgumentException("页码必须大于0");
        }
        if (startX == null || startY == null || endX == null || endY == null) {
            throw new IllegalArgumentException("坐标不能为空");
        }
        if (startX < 0 || startY < 0 || endX < 0 || endY < 0) {
            throw new IllegalArgumentException("坐标不能为负数");
        }
        if (startX >= endX || startY >= endY) {
            throw new IllegalArgumentException("起始坐标必须小于结束坐标");
        }

        this.page = page;
        this.startX = startX;
        this.startY = startY;
        this.endX = endX;
        this.endY = endY;
    }

    /**
     * 创建条款位置
     *
     * @param page   页码
     * @param startX 起始X坐标
     * @param startY 起始Y坐标
     * @param endX   结束X坐标
     * @param endY   结束Y坐标
     * @return ClausePosition实例
     */
    public static ClausePosition of(Integer page, Double startX, Double startY, Double endX, Double endY) {
        return new ClausePosition(page, startX, startY, endX, endY);
    }

    /**
     * 计算区域面积
     *
     * @return 面积
     */
    public Double getArea() {
        return (endX - startX) * (endY - startY);
    }

    /**
     * 计算区域宽度
     *
     * @return 宽度
     */
    public Double getWidth() {
        return endX - startX;
    }

    /**
     * 计算区域高度
     *
     * @return 高度
     */
    public Double getHeight() {
        return endY - startY;
    }

    @Override
    public String toString() {
        return String.format("ClausePosition{page=%d, (%.2f,%.2f)-(%.2f,%.2f)}",
            page, startX, startY, endX, endY);
    }
}