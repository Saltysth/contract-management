package com.contract.management.domain.exception;

/**
 * 条款抽取异常
 *
 * @author SaltyFish
 * @since 1.0.0
 */
public class ClauseExtractionException extends RuntimeException {

    public ClauseExtractionException(String message) {
        super(message);
    }

    public ClauseExtractionException(String message, Throwable cause) {
        super(message, cause);
    }
}