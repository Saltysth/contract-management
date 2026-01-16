package com.contract.management.application.service;

import com.contract.management.domain.model.ClauseExtraction;
import com.contract.management.domain.model.valueobject.ExtractionId;
import com.contract.management.domain.model.valueobject.ExtractionStatus;
import com.contract.management.domain.service.ClauseExtractionDomainService;
import com.contract.management.infrastructure.service.ClauseExtractionService;
import com.contract.management.interfaces.dto.ClauseExtractionStatusDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Optional;

/**
 * 条款抽取应用服务
 *
 * @author SaltyFish
 * @since 1.0.0
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class ClauseExtractionApplicationService {

    private final ClauseExtractionDomainService clauseExtractionDomainService;
    private final ClauseExtractionService clauseExtractionService;
    private final ClauseExtractionDtoConvertor dtoConvertor;

    /**
     * 根据抽取ID查询条款抽取状态
     */
    public Optional<ClauseExtractionStatusDTO> getExtractionStatusById(Long extractionId) {
        try {
            log.debug("查询条款抽取状态: extractionId={}", extractionId);

            ClauseExtraction extraction = clauseExtractionDomainService.getExtractionStatus(
                ExtractionId.of(extractionId)
            );

            if (extraction == null) {
                log.debug("条款抽取任务不存在: extractionId={}", extractionId);
                return Optional.empty();
            }

            ClauseExtractionStatusDTO dto = dtoConvertor.toStatusDTO(extraction);
            log.debug("查询条款抽取状态成功: extractionId={}, status={}",
                    extractionId, dto.getStatus());

            return Optional.of(dto);

        } catch (Exception e) {
            log.error("查询条款抽取状态失败: extractionId={}", extractionId, e);
            throw new RuntimeException("查询条款抽取状态失败: " + e.getMessage(), e);
        }
    }

    /**
     * 根据合同ID查询条款抽取状态
     */
    public Optional<ClauseExtractionStatusDTO> getExtractionStatusByContractId(Long contractId) {
        try {
            log.debug("查询合同条款抽取状态: contractId={}", contractId);

            ClauseExtraction extraction = clauseExtractionDomainService
                .getExtractionStatusByContractId(contractId);

            if (extraction == null) {
                log.debug("合同无条款抽取任务: contractId={}", contractId);
                return Optional.empty();
            }

            ClauseExtractionStatusDTO dto = dtoConvertor.toStatusDTO(extraction);
            log.debug("查询合同条款抽取状态成功: contractId={}, status={}",
                    contractId, dto.getStatus());

            return Optional.of(dto);

        } catch (Exception e) {
            log.error("查询合同条款抽取状态失败: contractId={}", contractId, e);
            throw new RuntimeException("查询合同条款抽取状态失败: " + e.getMessage(), e);
        }
    }

    /**
     * 检查合同是否有活跃的抽取任务
     */
    public boolean hasActiveExtraction(Long contractId) {
        try {
            log.debug("检查合同是否有活跃的抽取任务: contractId={}", contractId);

            Optional<ClauseExtractionStatusDTO> statusOpt = getExtractionStatusByContractId(contractId);
            if (statusOpt.isEmpty()) {
                return false;
            }

            ClauseExtractionStatusDTO status = statusOpt.get();
            boolean isActive = ExtractionStatus.PENDING.name().equals(status.getStatus()) ||
                             ExtractionStatus.PROCESSING.name().equals(status.getStatus());

            log.debug("检查合同活跃抽取任务结果: contractId={}, isActive={}", contractId, isActive);
            return isActive;

        } catch (Exception e) {
            log.error("检查合同活跃抽取任务失败: contractId={}", contractId, e);
            return false;
        }
    }

    /**
     * 取消条款抽取任务
     */
    public boolean cancelExtraction(Long extractionId) {
        try {
            log.debug("取消条款抽取任务: extractionId={}", extractionId);

            ClauseExtraction extraction = clauseExtractionDomainService.getExtractionStatus(
                ExtractionId.of(extractionId)
            );

            if (extraction == null) {
                log.warn("条款抽取任务不存在，无法取消: extractionId={}", extractionId);
                return false;
            }

            if (extraction.isInFinalState()) {
                log.warn("条款抽取任务已处于终态，无法取消: extractionId={}, status={}",
                        extractionId, extraction.getStatus());
                return false;
            }

            clauseExtractionDomainService.cancelExtraction(ExtractionId.of(extractionId));
            log.info("条款抽取任务已取消: extractionId={}", extractionId);
            return true;

        } catch (Exception e) {
            log.error("取消条款抽取任务失败: extractionId={}", extractionId, e);
            throw new RuntimeException("取消条款抽取任务失败: " + e.getMessage(), e);
        }
    }

    /**
     * 触发条款抽取任务
     * 这个方法用于在应用层编排领域服务和基础设施服务
     */
    public void triggerExtraction(Long contractId, Long createdBy, String fileUuid) {
        log.info("应用层触发条款抽取: contractId={}, fileUuid={}", contractId, fileUuid);

        // 1. 尝试获取现有抽取任务
        ClauseExtraction existingExtraction = clauseExtractionDomainService.getExtractionStatusByContractId(contractId);

        if (existingExtraction != null) {
            // 如果存在任务且可以重试，则重新触发
            if (existingExtraction.canRetry()) {
                log.info("发现可重试的抽取任务，重新触发: contractId={}, extractionId={}, status={}",
                        contractId, existingExtraction.getId(), existingExtraction.getStatus());

                // 重置任务状态为PENDING
                clauseExtractionDomainService.resetExtractionForRetry(existingExtraction.getId());

                // 触发异步抽取
                clauseExtractionService.extractClausesAsync(existingExtraction.getId(), contractId, fileUuid);
                log.info("条款抽取任务重试已触发: extractionId={}", existingExtraction.getId());
                return;
            } else if (existingExtraction.isInProgress()) {
                log.warn("抽取任务正在进行中，跳过触发: contractId={}, extractionId={}, status={}",
                        contractId, existingExtraction.getId(), existingExtraction.getStatus());
                throw new IllegalStateException("抽取任务正在进行中，请等待完成");
            }
        }

        // 2. 创建新的抽取任务（领域服务）
        clauseExtractionDomainService.performExtractionAsync(contractId, createdBy, fileUuid);

        // 3. 获取刚创建的抽取任务ID
        ClauseExtraction newExtraction = clauseExtractionDomainService.getExtractionStatusByContractId(contractId);
        if (newExtraction != null && newExtraction.getId() != null) {
            // 4. 触发异步抽取（基础设施服务）
            clauseExtractionService.extractClausesAsync(newExtraction.getId(), contractId, fileUuid);
            log.info("条款抽取异步任务已触发: extractionId={}", newExtraction.getId());
        }
    }

    /**
     * 触发条款抽取任务并返回任务信息
     * 按照业务逻辑：1. 检查是否存在未删除的抽取任务 2. 返回最新的任务ID或创建新任务
     */
    public ClauseExtractionStatusDTO triggerExtractionWithResponse(Long contractId, Long createdBy, String fileUuid) {
        log.info("触发条款抽取任务并返回响应: contractId={}, fileUuid={}", contractId, fileUuid);

        // 1. 尝试获取现有的抽取任务（未被删除的）
        ClauseExtraction existingExtraction = clauseExtractionDomainService.getExtractionStatusByContractId(contractId);

        if (existingExtraction != null) {
            log.info("找到现有的抽取任务: contractId={}, extractionId={}, status={}",
                    contractId, existingExtraction.getId(), existingExtraction.getStatus());

            // 如果任务已完成或失败但可以重试，重新触发
            if (existingExtraction.canRetry()) {
                log.info("重新触发可重试的抽取任务: extractionId={}", existingExtraction.getId());
                clauseExtractionDomainService.resetExtractionForRetry(existingExtraction.getId());
                clauseExtractionService.extractClausesAsync(existingExtraction.getId(), contractId, fileUuid);
            } else if (existingExtraction.isInProgress()) {
                log.info("抽取任务正在进行中，直接返回: extractionId={}", existingExtraction.getId());
                // 任务正在进行中，直接返回
            } else {
                log.info("抽取任务已完成，直接返回: extractionId={}", existingExtraction.getId());
                // 任务已完成，直接返回
            }

            return dtoConvertor.toStatusDTO(existingExtraction);
        }

        // 2. 没有找到现有任务，创建新的抽取任务
        log.info("未找到现有抽取任务，创建新任务: contractId={}", contractId);
        clauseExtractionDomainService.performExtractionAsync(contractId, createdBy, fileUuid);

        // 3. 获取刚创建的抽取任务
        ClauseExtraction newExtraction = clauseExtractionDomainService.getExtractionStatusByContractId(contractId);
        if (newExtraction != null) {
            clauseExtractionService.extractClausesAsync(newExtraction.getId(), contractId, fileUuid);
            return dtoConvertor.toStatusDTO(newExtraction);
        }

        throw new RuntimeException("创建条款抽取任务失败");
    }

    /**
     * 触发条款抽取任务并返回Feign响应信息
     * 按照业务逻辑：1. 检查是否存在未删除的抽取任务 2. 返回最新的任务ID或创建新任务
     */
    public ClauseExtraction triggerExtractionForFeign(Long contractId, Long createdBy, String fileUuid) {
        log.info("Feign触发条款抽取任务: contractId={}, fileUuid={}", contractId, fileUuid);

        // 1. 尝试获取现有的抽取任务（未被删除的）
        ClauseExtraction existingExtraction = clauseExtractionDomainService.getExtractionStatusByContractId(contractId);

        if (existingExtraction != null) {
            log.info("找到现有的抽取任务: contractId={}, extractionId={}, status={}",
                    contractId, existingExtraction.getId(), existingExtraction.getStatus());

//            // 如果任务已完成或失败但可以重试，重新触发
//            if (existingExtraction.canRetry()) {
//                log.info("重新触发可重试的抽取任务: extractionId={}", existingExtraction.getId());
//                clauseExtractionDomainService.resetExtractionForRetry(existingExtraction.getId());
//                clauseExtractionService.extractClausesAsync(existingExtraction.getId(), contractId, fileUuid);
//                return existingExtraction;
//            } else
            if (existingExtraction.isInProgress()) {
                log.info("抽取任务正在进行中，直接返回: extractionId={}", existingExtraction.getId());
                return existingExtraction;
            } else {
                log.info("抽取任务已完成，直接返回: extractionId={}", existingExtraction.getId());
                return existingExtraction;
            }
        }

        // 2. 没有找到现有任务，创建新的抽取任务
        log.info("未找到现有抽取任务，创建新任务: contractId={}", contractId);
        clauseExtractionDomainService.performExtractionAsync(contractId, createdBy, fileUuid);

        // 3. 获取刚创建的抽取任务
        ClauseExtraction newExtraction = clauseExtractionDomainService.getExtractionStatusByContractId(contractId);
        if (newExtraction != null) {
            clauseExtractionService.extractClausesAsync(newExtraction.getId(), contractId, fileUuid);
            return newExtraction;
        }

        throw new RuntimeException("创建条款抽取任务失败");
    }

    /**
     * 根据合同ID删除条款抽取和对应的条款（软删除）
     */
    public void deleteByContractId(Long contractId) {
        try {
            log.debug("删除合同相关的条款抽取和条款: contractId={}", contractId);

            clauseExtractionDomainService.deleteByContractId(contractId);

            log.info("成功删除合同相关的条款抽取和条款: contractId={}", contractId);

        } catch (Exception e) {
            log.error("删除合同相关的条款抽取和条款失败: contractId={}", contractId, e);
            throw new RuntimeException("删除合同相关的条款抽取和条款失败: " + e.getMessage(), e);
        }
    }
}