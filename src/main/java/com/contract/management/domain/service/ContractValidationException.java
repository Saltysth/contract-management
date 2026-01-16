package com.contract.management.domain.service;

/**
 * 合同验证异常
 *
 * @author SaltyFish
 * @since 1.0.0
 */
public class ContractValidationException extends RuntimeException {
    
    public ContractValidationException(String message) {
        super(message);
    }
    
    public ContractValidationException(String message, Throwable cause) {
        super(message, cause);
    }
}