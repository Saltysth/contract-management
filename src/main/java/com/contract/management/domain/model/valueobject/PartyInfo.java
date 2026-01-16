package com.contract.management.domain.model.valueobject;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

/**
 * 当事方信息值对象
 *
 * @author SaltyFish
 * @since 1.0.0
 */
@Getter
@EqualsAndHashCode
@ToString
public class PartyInfo {
    
    private final String name;
    private final String contact;
    private final String address;
    
    public PartyInfo(String name, String contact, String address) {
//        if (name == null || name.trim().isEmpty()) {
//            throw new IllegalArgumentException("当事方名称不能为空");
//        }
        
        this.name = name;
        this.contact = contact != null ? contact.trim() : null;
        this.address = address != null ? address.trim() : null;
    }
    
    /**
     * 创建当事方信息
     *
     * @param name 名称
     * @param contact 联系方式
     * @param address 地址
     * @return 当事方信息实例
     */
    public static PartyInfo of(String name, String contact, String address) {
        return new PartyInfo(name, contact, address);
    }
    
    /**
     * 创建只有名称的当事方信息
     *
     * @param name 名称
     * @return 当事方信息实例
     */
    public static PartyInfo ofName(String name) {
        return new PartyInfo(name, null, null);
    }
    
    /**
     * 检查是否有完整信息
     *
     * @return 是否有完整信息
     */
    public boolean hasCompleteInfo() {
        return name != null && !name.isEmpty() &&
               contact != null && !contact.isEmpty() &&
               address != null && !address.isEmpty();
    }
}