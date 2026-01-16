package com.contract.management.domain.service;

/**
 * 条款领域异常
 *
 * @author SaltyFish
 * @since 1.0.0
 */
public class ClauseDomainException extends RuntimeException {

    public ClauseDomainException(String message) {
        super(message);
    }

    public ClauseDomainException(String message, Throwable cause) {
        super(message, cause);
    }
}