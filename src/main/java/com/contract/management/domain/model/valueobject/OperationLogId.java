package com.contract.management.domain.model.valueobject;

import lombok.EqualsAndHashCode;
import lombok.Getter;

/**
 * 操作日志标识值对象
 *
 * @author SaltyFish
 * @since 1.0.0
 */
@Getter
@EqualsAndHashCode
public class OperationLogId {

    private final Long value;

    public OperationLogId(Long value) {
        if (value == null || value <= 0) {
            throw new IllegalArgumentException("操作日志ID必须是正数");
        }
        this.value = value;
    }

    /**
     * 静态工厂方法
     */
    public static OperationLogId of(Long value) {
        return new OperationLogId(value);
    }

    @Override
    public String toString() {
        return "OperationLogId{" + value + "}";
    }
}