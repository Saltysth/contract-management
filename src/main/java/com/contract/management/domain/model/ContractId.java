package com.contract.management.domain.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;



/**
 * 合同ID值对象
 *
 * @author SaltyFish
 * @since 1.0.0
 */
@Getter
@EqualsAndHashCode
@ToString
public class ContractId {
    
    private final Long value;
    
    public ContractId(Long value) {
        if (value == null || value <= 0) {
            throw new IllegalArgumentException("合同ID必须是正数");
        }
        this.value = value;
    }
    

    
    /**
     * 从Long值创建ContractId
     *
     * @param value Long值
     * @return ContractId实例
     */
    public static ContractId of(Long value) {
        return new ContractId(value);
    }

    /**
     * JSON反序列化工厂方法
     * 用于Jackson从JSON数值创建ContractId实例
     *
     * @param value 数值（可能是Integer或Long）
     * @return ContractId实例
     */
    @JsonCreator
    public static ContractId fromValue(Number value) {
        return new ContractId(value != null ? value.longValue() : null);
    }

    /**
     * JSON序列化方法
     * 用于Jackson将ContractId序列化为JSON数值
     *
     * @return Long值
     */
    @JsonValue
    public Long toValue() {
        return value;
    }

    /**
     * 获取Long值
     *
     * @return Long值
     */
    public Long getValue() {
        return value;
    }
}