package com.contract.management.interfaces.rest.api.v1.controller;

import com.contract.management.application.dto.ContractClassificationDTO;
import com.contract.management.application.service.ContractClassificationApplicationService;
import com.contract.management.interfaces.rest.api.v1.dto.response.ContractClassificationResponse;
import com.ruoyi.feign.annotation.RemotePreAuthorize;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

/**
 * 合同分类REST控制器
 * 提供合同分类相关的API接口
 *
 * @author SaltyFish
 * @since 1.0.0
 */
@Slf4j
@RestController
@RequestMapping("/api/v1/contracts/classification")
@RequiredArgsConstructor
@Validated
@Tag(name = "合同分类", description = "合同智能分类接口")
public class ContractClassificationController {

    private final ContractClassificationApplicationService classificationApplicationService;

    /**
     * 通过合同ID获取合同分类信息
     *
     * @param contractId 合同ID
     * @return 合同分类信息
     */
    @RemotePreAuthorize("@ss.hasAnyRoles('admin,common,guest')")
    @GetMapping("/{contractId}")
    @Operation(
        summary = "获取合同分类信息",
        description = "通过合同ID获取合同分类信息，包括AI智能分析结果"
    )
    public ResponseEntity<ContractClassificationResponse> classifyContract(
            @Parameter(description = "合同ID", required = true, example = "1")
            @PathVariable
            @NotNull(message = "合同ID不能为空")
            @Positive(message = "合同ID必须为正数")
            Long contractId) {

        try {
            log.info("收到合同分类请求，contractId: {}", contractId);

            ContractClassificationDTO result = classificationApplicationService.classifyContract(contractId);
            ContractClassificationResponse response = ContractClassificationResponse.from(result);

            if (!result.getSuccess()) {
                log.warn("合同分类失败，contractId: {}, error: {}", contractId, result.getErrorMessage());
                // 即使分类失败，也返回200状态码，但响应中包含错误信息
            } else {
                log.info("合同分类成功，contractId: {}, type: {}, confidence: {}",
                         contractId, result.getContractType(), result.getConfidence());
            }

            return ResponseEntity.ok(response);

        } catch (IllegalArgumentException e) {
            log.warn("参数验证失败，contractId: {}, error: {}", contractId, e.getMessage());
            throw e; // 由全局异常处理器处理

        } catch (Exception e) {
            log.error("合同分类过程中发生未知错误，contractId: {}", contractId, e);
            // 构建错误响应
            ContractClassificationDTO errorDTO = ContractClassificationDTO.builder()
                .contractId(contractId)
                .success(false)
                .errorMessage("系统内部错误: " + e.getMessage())
                .fileProcessingStatus(ContractClassificationDTO.FileProcessingStatus.FAILED)
                .build();
            ContractClassificationResponse errorResponse = ContractClassificationResponse.from(errorDTO);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }
}