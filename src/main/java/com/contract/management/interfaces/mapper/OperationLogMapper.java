package com.contract.management.interfaces.mapper;

import com.contract.management.domain.model.OperationLog;
import com.contract.management.interfaces.dto.OperationLogDTO;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 操作日志DTO映射器
 *
 * @author SaltyFish
 * @since 1.0.0
 */
@Component("OperationLogMapping")
public class OperationLogMapper {

    /**
     * 将领域模型转换为DTO
     */
    public OperationLogDTO toDto(OperationLog domain) {
        if (domain == null) {
            return null;
        }

        return OperationLogDTO.builder()
                .resourceType(domain.getResourceType() != null ? domain.getResourceType().getDescription() : null)
                .description(domain.getOperationDetails() != null ? domain.getOperationDetails().getDescription() : null)
                .result(domain.getOperationDetails() != null ? domain.getOperationDetails().getResult() : null)
                .errorMessage(domain.getOperationDetails() != null ? domain.getOperationDetails().getErrorMessage() : null)
                .executionTimeMs(domain.getExecutionTimeMs())
                .createdTime(domain.getCreatedTime())
                .build();
    }

    /**
     * 将领域模型列表转换为DTO列表
     */
    public List<OperationLogDTO> toDtoList(List<OperationLog> domains) {
        if (domains == null || domains.isEmpty()) {
            return List.of();
        }

        return domains.stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }
}