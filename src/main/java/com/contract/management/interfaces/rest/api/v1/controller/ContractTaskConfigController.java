package com.contract.management.interfaces.rest.api.v1.controller;

import com.contract.management.application.service.ContractTaskConfigService;
import com.contract.management.interfaces.rest.api.v1.dto.response.ContractTaskConfigResponse;
import com.ruoyi.feign.annotation.RemotePreAuthorize;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
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
 * 合同任务配置REST控制器
 *
 * @author SaltyFish
 * @since 1.0.0
 */
@Slf4j
@RestController
@RequestMapping("/api/v1/contract-task-config")
@RequiredArgsConstructor
@Validated
@Tag(name = "合同任务配置", description = "合同任务配置信息查询接口")
public class ContractTaskConfigController {

    private final ContractTaskConfigService contractTaskConfigService;

    /**
     * 获取合同任务配置信息
     *
     * @param contractTaskId 合同任务ID
     * @return 配置信息和耗时
     */
    @RemotePreAuthorize("@ss.hasAnyRoles('admin,common,guest')")
    @GetMapping("/{contractTaskId}")
    @Operation(summary = "获取合同任务配置信息", description = "根据合同任务ID查询配置参数和预计耗时")
    public ResponseEntity<ContractTaskConfigResponse> getContractTaskConfig(
            @Parameter(description = "合同任务ID", required = true)
            @PathVariable
            @NotNull(message = "合同任务ID不能为空")
            @Positive(message = "合同任务ID必须为正数")
            Long contractTaskId) {

        log.info("获取合同任务配置信息, contractTaskId: {}", contractTaskId);

        try {
            ContractTaskConfigResponse response = contractTaskConfigService.getContractTaskConfig(contractTaskId);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("获取合同任务配置信息失败, contractTaskId: {}", contractTaskId, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}