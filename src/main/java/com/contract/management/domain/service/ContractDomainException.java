package com.contract.management.domain.service;

/**
 * 合同领域异常
 *
 * @author SaltyFish
 * @since 1.0.0
 */
public class ContractDomainException extends RuntimeException {
    
    private final String errorCode;
    
    public ContractDomainException(String errorCode, String message) {
        super(message);
        this.errorCode = errorCode;
    }
    
    public ContractDomainException(String errorCode, String message, Throwable cause) {
        super(message, cause);
        this.errorCode = errorCode;
    }
    
    public String getErrorCode() {
        return errorCode;
    }
}