package com.contract.management.interfaces.feign.controller;

import com.contract.common.dto.DeleteClauseExtractionResponse;
import com.contract.common.dto.TriggerClauseExtractionResponse;
import com.contract.management.application.dto.ClauseDTO;
import com.contract.management.application.service.ClauseApplicationService;
import com.contract.management.application.service.ClauseExtractionApplicationService;
import com.contract.management.domain.model.ClauseExtraction;
import com.contract.management.domain.model.Contract;
import com.contract.management.domain.model.ContractId;
import com.contract.management.domain.repository.ContractRepository;
import com.ruoyi.feign.annotation.RemotePreAuthorize;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * 条款抽取Feign控制器
 * 处理来自其他微服务的条款抽取请求
 *
 * @author SaltyFish
 * @since 1.0.0
 */
@Slf4j
@RestController
@RequestMapping("/feign/v1/clause-extractions")
@RequiredArgsConstructor
public class ClauseExtractionFeignController {

    private final ClauseExtractionApplicationService clauseExtractionApplicationService;
    private final ClauseApplicationService clauseApplicationService;
    private final ContractRepository contractRepository;

    public TriggerClauseExtractionResponse fromDomain(Long contractId, String attachmentUuid,
        ClauseExtraction extraction) {
        TriggerClauseExtractionResponse response = new TriggerClauseExtractionResponse();
        response.setContractId(contractId);
        response.setExtractionId(extraction.getId().getValue());
        response.setAttachmentUuid(attachmentUuid);
        response.setExtractionStatus(extraction.getStatus().name());
        response.setErrorMessage(extraction.getErrorMessage());

        List<ClauseDTO> clauses = clauseApplicationService.findByExtractionTaskId(extraction.getId().getValue());
        response.setExtractedClauseNumber(clauses.size());
        return response;
    }

    /**
     * 触发条款抽取任务
     * Feign接口，提供给其他微服务调用
     */
    @RemotePreAuthorize("@ss.hasAnyRoles('admin,common')")
    @PostMapping("/contract/{contractId}/trigger")
    @Operation(summary = "触发条款抽取任务", description = "为指定合同触发条款抽取任务，按照业务逻辑检查现有任务或创建新任务")
    public TriggerClauseExtractionResponse triggerClauseExtraction(
        @Parameter(description = "合同ID", required = true)
        @PathVariable @NotNull @Positive Long contractId) {

        log.debug("Feign调用：触发条款抽取, contractId={}", contractId);

        try {
            // 1. 检查合同是否存在
            Contract contract = contractRepository.findById(ContractId.of(contractId)).orElse(null);
            if (contract == null) {
                log.warn("合同不存在: contractId={}", contractId);
                return TriggerClauseExtractionResponse.error(contractId, "合同不存在");
            }

            // 2. 检查合同是否有附件
            if (contract.getAttachmentUuid() == null || contract.getAttachmentUuid().trim().isEmpty()) {
                log.warn("合同无附件，无法触发条款抽取: contractId={}", contractId);
                return TriggerClauseExtractionResponse.error(contractId, "合同无附件，无法进行条款抽取");
            }

            // 3. 触发条款抽取任务并获取响应（应用服务内部会处理重试逻辑和业务逻辑检查）
            ClauseExtraction extraction = clauseExtractionApplicationService
                .triggerExtractionForFeign(contractId, 1L, contract.getAttachmentUuid());

            if (extraction == null) {
                log.error("触发条款抽取失败，未返回状态: contractId={}", contractId);
                return TriggerClauseExtractionResponse.error(contractId, "触发条款抽取失败");
            }

            // 4. 构建响应对象（直接从领域模型转换）
            return fromDomain(contractId, contract.getAttachmentUuid(), extraction);

        } catch (Exception e) {
            log.error("Feign触发条款抽取失败: contractId={}", contractId, e);
            return TriggerClauseExtractionResponse.error(contractId, "触发条款抽取失败: " + e.getMessage());
        }
    }

    /**
     * 删除条款抽取任务和对应的条款
     * Feign接口，提供给其他微服务调用
     */
    @RemotePreAuthorize("@ss.hasAnyRoles('admin,common')")
    @DeleteMapping("/contract/{contractId}")
    @Operation(summary = "删除条款抽取任务", description = "根据合同ID删除对应的条款抽取记录和所有相关条款（软删除）")
    public DeleteClauseExtractionResponse deleteExtractionByContractId(
        @Parameter(description = "合同ID", required = true)
        @PathVariable @NotNull @Positive Long contractId) {

        log.debug("Feign调用：删除条款抽取, contractId={}", contractId);

        try {
            // 1. 检查合同是否存在
            Contract contract = contractRepository.findById(ContractId.of(contractId)).orElse(null);
            if (contract == null) {
                log.warn("合同不存在: contractId={}", contractId);
                return DeleteClauseExtractionResponse.error(contractId, "合同不存在");
            }

            // 2. 执行删除操作
            clauseExtractionApplicationService.deleteByContractId(contractId);

            log.info("Feign删除条款抽取成功: contractId={}", contractId);
            return DeleteClauseExtractionResponse.success(contractId);

        } catch (Exception e) {
            log.error("Feign删除条款抽取失败: contractId={}", contractId, e);
            return DeleteClauseExtractionResponse.error(contractId, "删除失败: " + e.getMessage());
        }
    }
}