package com.contract.management.interfaces.dto;

import com.contract.management.domain.model.valueobject.ResourceType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 操作日志查询请求
 *
 * @author SaltyFish
 * @since 1.0.0
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OperationLogQueryRequest {

    /**
     * 合同ID（可选，与extractionId二选一）
     */
    private Long contractId;

    /**
     * 抽取ID（可选，与contractId二选一）
     */
    private Long extractionId;

    /**
     * 资源类型（可选，默认为条款抽取）
     */
    private String resourceType;

    /**
     * 验证请求参数的有效性
     */
    public boolean isValid() {
        // 验证contractId和extractionId二选一
        boolean validIds = (contractId != null && extractionId == null) ||
                          (contractId == null && extractionId != null);

        // 如果提供了resourceType，验证是否为有效的枚举值
        boolean validResourceType = true;
        if (resourceType != null && !resourceType.trim().isEmpty()) {
            validResourceType = ResourceType.fromDescription(resourceType) != null;
        }

        return validIds && validResourceType;
    }

    /**
     * 获取查询类型描述
     */
    public String getQueryType() {
        return contractId != null ? "CONTRACT_ID" : "EXTRACTION_ID";
    }

    /**
     * 获取资源类型枚举
     */
    public ResourceType getResourceTypeEnum() {
        if (resourceType == null || resourceType.trim().isEmpty()) {
            return null;
        }
        return ResourceType.fromDescription(resourceType);
    }
}