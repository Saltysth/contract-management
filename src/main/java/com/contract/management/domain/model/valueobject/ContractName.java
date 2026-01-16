package com.contract.management.domain.model.valueobject;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

/**
 * 合同名称值对象
 *
 * @author SaltyFish
 * @since 1.0.0
 */
@Getter
@EqualsAndHashCode
@ToString
public class ContractName {
    
    private static final int MAX_LENGTH = 255;
    
    private final String value;
    
    public ContractName(String value) {
        if (value == null || value.trim().isEmpty()) {
            throw new IllegalArgumentException("合同名称不能为空");
        }
        
        String trimmedValue = value.trim();
        if (trimmedValue.length() > MAX_LENGTH) {
            throw new IllegalArgumentException("合同名称长度不能超过" + MAX_LENGTH + "个字符");
        }
        
        this.value = trimmedValue;
    }
    
    /**
     * 创建合同名称
     *
     * @param value 名称值
     * @return 合同名称实例
     */
    public static ContractName of(String value) {
        return new ContractName(value);
    }
}