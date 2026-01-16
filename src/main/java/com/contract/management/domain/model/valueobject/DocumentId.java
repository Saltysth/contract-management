package com.contract.management.domain.model.valueobject;

import lombok.EqualsAndHashCode;
import lombok.Getter;

/**
 * 文档ID值对象
 *
 * @author SaltyFish
 * @since 1.0.0
 */
@Getter
@EqualsAndHashCode
public class DocumentId {
    private final Long value;

    public DocumentId(Long value) {
        if (value == null) {
            throw new IllegalArgumentException("Document ID cannot be null");
        }
        this.value = value;
    }

    public static DocumentId of(Long value) {
        return new DocumentId(value);
    }

    @Override
    public String toString() {
        return value.toString();
    }
}