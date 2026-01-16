package com.contract.management.interfaces.rest.api.v1.controller;

import com.contract.management.application.dto.ClauseDTO;
import com.contract.management.application.service.ClauseApplicationService;
import com.contract.management.application.service.ClauseExtractionApplicationService;
import com.contract.management.domain.model.ClauseExtraction;
import com.contract.management.domain.model.Contract;
import com.contract.management.domain.model.ContractId;
import com.contract.management.domain.repository.ClauseExtractionRepository;
import com.contract.management.domain.repository.ContractRepository;
import com.contract.management.interfaces.dto.ClauseExtractionStatusDTO;
import com.contract.management.interfaces.dto.TriggerClauseExtractionResponse;
import com.contract.management.interfaces.rest.api.v1.convertor.ComprehensiveClauseExtractionConvertor;
import com.contract.management.interfaces.rest.api.v1.dto.common.ApiResponse;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ruoyi.feign.annotation.RemotePreAuthorize;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * 条款抽取控制器
 * 提供条款抽取状态查询和管理接口
 *
 * @author SaltyFish
 * @since 1.0.0
 */
@Slf4j
@RestController
@RequestMapping("/api/v1/clause-extractions")
@RequiredArgsConstructor
@Tag(name = "条款抽取管理", description = "合同条款抽取状态查询和管理接口")
public class ClauseExtractionController {

    private final ClauseExtractionApplicationService clauseExtractionApplicationService;
    private final ClauseExtractionRepository clauseExtractionRepository;
    private final ContractRepository contractRepository;
    private final ComprehensiveClauseExtractionConvertor comprehensiveConvertor;
    private final ObjectMapper objectMapper;
    private final ClauseApplicationService clauseApplicationService;

    /**
     * 根据抽取ID查询条款抽取状态
     */
    @RemotePreAuthorize("@ss.hasAnyRoles('admin,common,guest')")
    @GetMapping("/{extractionId}")
    @Operation(summary = "查询条款抽取状态", description = "根据抽取任务ID查询条款抽取的详细状态和结果")
    public ResponseEntity<ApiResponse<ClauseExtractionStatusDTO>> getExtractionStatus(
            @Parameter(description = "抽取任务ID", required = true)
            @PathVariable Long extractionId) {

        log.info("查询条款抽取状态: extractionId={}", extractionId);

        Optional<ClauseExtractionStatusDTO> statusOpt = clauseExtractionApplicationService
            .getExtractionStatusById(extractionId);

        if (statusOpt.isEmpty()) {
            log.warn("条款抽取任务不存在: extractionId={}", extractionId);
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok(ApiResponse.success(statusOpt.get()));
    }

    /**
     * 根据合同ID查询条款抽取状态
     */
    @RemotePreAuthorize("@ss.hasAnyRoles('admin,common,guest')")
    @GetMapping("/contract/{contractId}")
    @Operation(summary = "查询合同条款抽取状态", description = "根据合同ID查询该合同的条款抽取状态")
    public ResponseEntity<ApiResponse<ClauseExtractionStatusDTO>> getExtractionStatusByContractId(
            @Parameter(description = "合同ID", required = true)
            @PathVariable Long contractId) {

        log.info("查询合同条款抽取状态: contractId={}", contractId);

        Optional<ClauseExtractionStatusDTO> statusOpt = clauseExtractionApplicationService
            .getExtractionStatusByContractId(contractId);

        if (statusOpt.isEmpty()) {
            log.warn("合同无条款抽取任务: contractId={}", contractId);
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok(ApiResponse.success(statusOpt.get()));
    }


    /**
     * 取消条款抽取任务
     */
    @RemotePreAuthorize("@ss.hasRole('admin')")
    @PutMapping("/{extractionId}/cancel")
    @Operation(summary = "取消条款抽取任务", description = "取消正在进行的条款抽取任务")
    public ResponseEntity<ApiResponse<Map<String, Object>>> cancelExtraction(
            @Parameter(description = "抽取任务ID", required = true)
            @PathVariable Long extractionId) {

        log.info("取消条款抽取任务: extractionId={}", extractionId);
        // FIXME 这里只是标记状态取消，如果有任务正在进行并不会响应这个取消请求，就会导致实际上任务还在进行中，只是看起来取消了
        boolean cancelled = clauseExtractionApplicationService.cancelExtraction(extractionId);

        Map<String, Object> result = Map.of(
            "extractionId", extractionId,
            "cancelled", cancelled
        );

        if (!cancelled) {
            log.warn("条款抽取任务取消失败: extractionId={}", extractionId);
            return ResponseEntity.badRequest().body(ApiResponse.error(
                400, "条款抽取任务取消失败，可能不存在或已处于终态", result
            ));
        }

        return ResponseEntity.ok(ApiResponse.success(result));
    }

    /**
     * 手动触发条款抽取任务
     */
    @RemotePreAuthorize("@ss.hasRole('admin')")
    @PostMapping("/contract/{contractId}/trigger")
    @Operation(summary = "手动触发条款抽取", description = "为指定合同手动触发条款抽取任务")
    public ResponseEntity<ApiResponse<TriggerClauseExtractionResponse>> triggerClauseExtraction(
            @Parameter(description = "合同ID", required = true)
            @PathVariable Long contractId) {

        log.info("手动触发条款抽取: contractId={}", contractId);

        try {
            // 1. 检查合同是否存在
            Contract contract = contractRepository.findById(ContractId.of(contractId)).orElse(null);
            if (contract == null) {
                log.warn("合同不存在: contractId={}", contractId);
                return ResponseEntity.notFound().build();
            }

            // 2. 检查合同是否有附件
            if (contract.getAttachmentUuid() == null || contract.getAttachmentUuid().trim().isEmpty()) {
                log.warn("合同无附件，无法触发条款抽取: contractId={}", contractId);
                return ResponseEntity.badRequest().body(ApiResponse.error(
                    400, "合同无附件，无法进行条款抽取", null
                ));
            }

            // 3. 触发条款抽取任务并获取响应（应用服务内部会处理重试逻辑）
            ClauseExtraction extraction = clauseExtractionApplicationService
                .triggerExtractionForFeign(contractId, 1L, contract.getAttachmentUuid());

            if (extraction == null) {
                log.error("触发条款抽取失败: contractId={}", contractId);
                return ResponseEntity.internalServerError().body(ApiResponse.error(
                    500, "触发条款抽取失败", null
                ));
            }

            // 4. 构建响应对象
            TriggerClauseExtractionResponse response = TriggerClauseExtractionResponse.fromDomain(
                contractId, contract.getAttachmentUuid(), extraction);
            
            List<ClauseDTO> clauses = clauseApplicationService.findByExtractionTaskId(extraction.getId().getValue());
            response.setExtractedClauseNumber(clauses.size());
            
            log.info("条款抽取任务手动触发成功: contractId={}, extractionId={}", contractId, response.getExtractionId());
            return ResponseEntity.ok(ApiResponse.success(response));

        } catch (Exception e) {
            log.error("手动触发条款抽取失败: contractId={}", contractId, e);
            return ResponseEntity.internalServerError().body(ApiResponse.error(
                500, "手动触发条款抽取失败: " + e.getMessage(), null
            ));
        }
    }


    /**
     * 根据合同ID删除条款抽取和对应的条款
     */
    @RemotePreAuthorize("@ss.hasAnyRoles('admin,common')")
    @DeleteMapping("/contract/{contractId}")
    @Operation(summary = "删除合同条款抽取", description = "根据合同ID删除对应的条款抽取记录和所有相关条款（软删除）")
    public ResponseEntity<ApiResponse<Map<String, Object>>> deleteExtractionByContractId(
            @Parameter(description = "合同ID", required = true)
            @PathVariable Long contractId) {

        log.info("删除合同条款抽取: contractId={}", contractId);

        try {
            clauseExtractionApplicationService.deleteByContractId(contractId);

            Map<String, Object> result = Map.of(
                "contractId", contractId,
                "deleted", true
            );

            log.info("合同条款抽取删除成功: contractId={}", contractId);
            return ResponseEntity.ok(ApiResponse.success(result));

        } catch (Exception e) {
            log.error("删除合同条款抽取失败: contractId={}", contractId, e);
            return ResponseEntity.internalServerError().body(ApiResponse.error(
                500, "删除合同条款抽取失败: " + e.getMessage(), null
            ));
        }
    }


}