package com.contract.management.domain.exception;

/**
 * 合同未找到异常
 *
 * @author SaltyFish
 * @since 1.0.0
 */
public class ContractNotFoundException extends RuntimeException {

    public ContractNotFoundException(String message) {
        super(message);
    }

    public ContractNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }
}