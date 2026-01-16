package com.contract.management.domain.service;

/**
 * 提示词模板领域异常
 *
 * @author SaltyFish
 * @since 1.0.0
 */
public class PromptDomainException extends RuntimeException {

    public PromptDomainException(String message) {
        super(message);
    }

    public PromptDomainException(String message, Throwable cause) {
        super(message, cause);
    }
}