package com.contract.management.domain.exception;

/**
 * 文件处理异常
 *
 * @author SaltyFish
 * @since 1.0.0
 */
public class FileProcessingException extends RuntimeException {

    public FileProcessingException(String message) {
        super(message);
    }

    public FileProcessingException(String message, Throwable cause) {
        super(message, cause);
    }
}