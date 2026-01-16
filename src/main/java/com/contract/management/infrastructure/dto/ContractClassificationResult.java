package com.contract.management.infrastructure.dto;

import com.contract.common.constant.ContractType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

/**
 * 合同分类结果类
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ContractClassificationResult {
    private ContractType contractType;
    private Double confidence;
    private String classificationMethod;
    private String aiRawResponse;
    private Map<String, Object> extendedProperties;
    private Boolean success;
    private String errorMessage;
}