package com.contract.management.domain.model.valueobject;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

/**
 * 合同期限值对象
 *
 * @author SaltyFish
 * @since 1.0.0
 */
@Getter
@EqualsAndHashCode
@ToString
public class ContractPeriod {
    
    private final LocalDate signDate;
    private final LocalDate effectiveDate;
    private final LocalDate expiryDate;
    
    public ContractPeriod(LocalDate signDate, LocalDate effectiveDate, LocalDate expiryDate) {
        validateDates(signDate, effectiveDate, expiryDate);
        
        this.signDate = signDate;
        this.effectiveDate = effectiveDate;
        this.expiryDate = expiryDate;
    }
    
    /**
     * 验证日期的合法性
     */
    private void validateDates(LocalDate signDate, LocalDate effectiveDate, LocalDate expiryDate) {
        if (effectiveDate != null && signDate != null && effectiveDate.isBefore(signDate)) {
            throw new IllegalArgumentException("生效日期不能早于签署日期");
        }
        
        if (expiryDate != null && effectiveDate != null && expiryDate.isBefore(effectiveDate)) {
            throw new IllegalArgumentException("到期日期不能早于生效日期");
        }
        
        if (expiryDate != null && signDate != null && expiryDate.isBefore(signDate)) {
            throw new IllegalArgumentException("到期日期不能早于签署日期");
        }
    }
    
    /**
     * 创建合同期限
     *
     * @param signDate 签署日期
     * @param effectiveDate 生效日期
     * @param expiryDate 到期日期
     * @return 合同期限实例
     */
    public static ContractPeriod of(LocalDate signDate, LocalDate effectiveDate, LocalDate expiryDate) {
        return new ContractPeriod(signDate, effectiveDate, expiryDate);
    }
    
    /**
     * 创建只有签署日期的合同期限
     *
     * @param signDate 签署日期
     * @return 合同期限实例
     */
    public static ContractPeriod ofSignDate(LocalDate signDate) {
        return new ContractPeriod(signDate, null, null);
    }
    
    /**
     * 检查合同是否已过期
     *
     * @return 是否已过期
     */
    public boolean isExpired() {
        return expiryDate != null && LocalDate.now().isAfter(expiryDate);
    }
    
    /**
     * 检查合同是否已生效
     *
     * @return 是否已生效
     */
    public boolean isActive() {
        LocalDate now = LocalDate.now();
        return (effectiveDate == null || !now.isBefore(effectiveDate)) &&
               (expiryDate == null || !now.isAfter(expiryDate));
    }
    
    /**
     * 检查合同是否即将到期
     *
     * @param days 天数
     * @return 是否即将到期
     */
    public boolean isExpiringWithin(int days) {
        if (expiryDate == null) {
            return false;
        }
        
        LocalDate now = LocalDate.now();
        LocalDate targetDate = now.plusDays(days);
        
        return !expiryDate.isBefore(now) && !expiryDate.isAfter(targetDate);
    }
    
    /**
     * 获取合同剩余天数
     *
     * @return 剩余天数，如果已过期返回负数，如果没有到期日期返回null
     */
    public Long getRemainingDays() {
        if (expiryDate == null) {
            return null;
        }
        
        return ChronoUnit.DAYS.between(LocalDate.now(), expiryDate);
    }
    
    /**
     * 获取合同总期限天数
     *
     * @return 总期限天数，如果没有完整日期返回null
     */
    public Long getTotalDays() {
        if (effectiveDate == null || expiryDate == null) {
            return null;
        }
        
        return ChronoUnit.DAYS.between(effectiveDate, expiryDate);
    }
}