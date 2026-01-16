package com.contract.management.domain.model.valueobject;

import com.contract.common.constant.Currency;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

import java.math.BigDecimal;
import java.math.RoundingMode;

/**
 * 金额值对象
 *
 * @author SaltyFish
 * @since 1.0.0
 */
@Getter
@EqualsAndHashCode
@ToString
public class Money {
    
    private final BigDecimal amount;
    private final Currency currency;
    
    public Money(BigDecimal amount, Currency currency) {
        if (amount != null) {
            if (amount.compareTo(BigDecimal.ZERO) < 0) {
                throw new IllegalArgumentException("金额不能为负数");
            }
            this.amount = amount.setScale(2, RoundingMode.HALF_UP);
        } else {
            this.amount = null;
        }
        this.currency = currency != null ? currency : Currency.CNY;
    }
    
    /**
     * 创建金额
     *
     * @param amount 金额
     * @param currency 币种
     * @return 金额实例
     */
    public static Money of(BigDecimal amount, Currency currency) {
        return new Money(amount, currency);
    }
    
    /**
     * 创建人民币金额
     *
     * @param amount 金额
     * @return 金额实例
     */
    public static Money ofCNY(BigDecimal amount) {
        return new Money(amount, Currency.CNY);
    }
    
    /**
     * 创建美元金额
     *
     * @param amount 金额
     * @return 金额实例
     */
    public static Money ofUSD(BigDecimal amount) {
        return new Money(amount, Currency.USD);
    }
    
    /**
     * 金额相加
     *
     * @param other 另一个金额
     * @return 相加后的金额
     */
    public Money add(Money other) {
        if (!this.currency.equals(other.currency)) {
            throw new IllegalArgumentException("不同币种不能直接相加");
        }
        if (this.amount == null || other.amount == null) {
            throw new IllegalArgumentException("金额为null时不能进行运算");
        }
        return new Money(this.amount.add(other.amount), this.currency);
    }
    
    /**
     * 金额相减
     *
     * @param other 另一个金额
     * @return 相减后的金额
     */
    public Money subtract(Money other) {
        if (!this.currency.equals(other.currency)) {
            throw new IllegalArgumentException("不同币种不能直接相减");
        }
        if (this.amount == null || other.amount == null) {
            throw new IllegalArgumentException("金额为null时不能进行运算");
        }
        BigDecimal result = this.amount.subtract(other.amount);
        if (result.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("金额不能为负数");
        }
        return new Money(result, this.currency);
    }
    
    /**
     * 金额乘法
     *
     * @param multiplier 乘数
     * @return 乘法后的金额
     */
    public Money multiply(BigDecimal multiplier) {
        if (multiplier == null || multiplier.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("乘数不能为null或负数");
        }
        if (this.amount == null) {
            throw new IllegalArgumentException("金额为null时不能进行运算");
        }
        return new Money(this.amount.multiply(multiplier), this.currency);
    }
    
    /**
     * 比较金额大小
     *
     * @param other 另一个金额
     * @return 比较结果
     */
    public int compareTo(Money other) {
        if (!this.currency.equals(other.currency)) {
            throw new IllegalArgumentException("不同币种不能直接比较");
        }
        if (this.amount == null || other.amount == null) {
            throw new IllegalArgumentException("金额为null时不能进行比较");
        }
        return this.amount.compareTo(other.amount);
    }
    
    /**
     * 检查是否为零
     *
     * @return 是否为零
     */
    public boolean isZero() {
        return amount != null && amount.compareTo(BigDecimal.ZERO) == 0;
    }

    /**
     * 检查是否大于零
     *
     * @return 是否大于零
     */
    public boolean isPositive() {
        return amount != null && amount.compareTo(BigDecimal.ZERO) > 0;
    }
}