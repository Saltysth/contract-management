package com.contract.management.interfaces.rest.api.v1.controller;

import com.contract.management.application.service.OperationLogApplicationService;
import com.contract.management.domain.model.OperationLog;
import com.contract.management.interfaces.dto.OperationLogQueryRequest;
import com.contract.management.interfaces.dto.OperationLogDTO;
import com.contract.management.interfaces.mapper.OperationLogMapper;
import com.contract.management.interfaces.rest.api.v1.dto.common.ApiResponse;
import com.ruoyi.feign.annotation.RemotePreAuthorize;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.List;

/**
 * 操作日志控制器
 * 提供操作日志查询接口
 *
 * @author SaltyFish
 * @since 1.0.0
 */
@Slf4j
@RestController
@RequestMapping("/api/v1/operation-logs")
@RequiredArgsConstructor
@Validated
@Tag(name = "操作日志管理", description = "操作日志查询接口")
public class OperationLogController {

    private final OperationLogApplicationService operationLogApplicationService;
    private final OperationLogMapper operationLogMapper;

    /**
     * 查询操作日志
     * 支持通过合同ID或抽取ID查询
     */
    @RemotePreAuthorize("@ss.hasAnyRoles('admin,common,guest')")
    @PostMapping("/clause-extraction/query")
    @Operation(summary = "查询操作日志",
               description = "根据合同ID或抽取ID查询操作日志。如果提供合同ID，会查询该合同最新的相关日志；如果提供抽取ID，会直接查询该抽取任务的日志。资源类型可选，不传递时查询该ID下的所有日志。结果按创建时间正序排列。")
    public ResponseEntity<ApiResponse<List<OperationLogDTO>>> queryOperationLogs(
            @Parameter(description = "查询参数", required = true)
            @Valid @RequestBody OperationLogQueryRequest request) {

        log.info("查询操作日志: request={}", request);

        // 验证参数
        if (!request.isValid()) {
            log.warn("无效的查询参数: {}", request);
            return ResponseEntity.badRequest().body(ApiResponse.error(
                400, "查询参数无效，必须提供且仅提供 contractId 或 extractionId 中的一个", null
            ));
        }

        try {
            // 获取资源类型描述（可能为null）
            String resourceTypeDescription = request.getResourceType() != null ?
                request.getResourceTypeEnum().getDescription() : null;

            // 查询日志
            List<OperationLog> logs = operationLogApplicationService.getOperationLogs(
                request.getContractId(), request.getExtractionId(), resourceTypeDescription
            );

            // 转换为DTO
            List<OperationLogDTO> dtoList = operationLogMapper.toDtoList(logs);

            log.info("查询操作日志完成: queryType={}, resourceType={}, count={}",
                    request.getQueryType(), resourceTypeDescription, dtoList.size());

            return ResponseEntity.ok(ApiResponse.success(dtoList));

        } catch (Exception e) {
            log.error("查询操作日志失败: request={}", request, e);
            return ResponseEntity.internalServerError().body(ApiResponse.error(
                500, "查询操作日志失败: " + e.getMessage(), null
            ));
        }
    }
}