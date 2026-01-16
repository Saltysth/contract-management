package com.contract.management.domain.service;

import com.contract.management.domain.exception.ClauseExtractionException;
import com.contract.management.domain.model.ClauseExtraction;
import com.contract.management.domain.model.valueobject.ExtractionId;
import com.contract.management.domain.repository.ClauseExtractionRepository;
import com.contract.management.domain.repository.ClauseRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 条款抽取领域服务
 *
 * @author SaltyFish
 * @since 1.0.0
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class ClauseExtractionDomainService {

    private final ClauseExtractionRepository clauseExtractionRepository;
    private final ClauseRepository clauseRepository;

    /**
     * 创建条款抽取任务
     */
    @Transactional
    public ClauseExtraction createExtraction(Long contractId, Long createdBy) {
        // 检查是否已存在活跃的抽取任务
        if (clauseExtractionRepository.existsActiveExtraction(contractId)) {
            throw new ClauseExtractionException("该合同已存在活跃的条款抽取任务");
        }

        // 创建条款抽取领域对象（ID由数据库生成）
        ClauseExtraction clauseExtraction = new ClauseExtraction(contractId, createdBy);

        // 保存到数据库
        return clauseExtractionRepository.save(clauseExtraction);
    }

    /**
     * 开始处理条款抽取
     */
    @Transactional
    public ClauseExtraction startExtraction(ExtractionId extractionId) {
        ClauseExtraction extraction = clauseExtractionRepository.findById(extractionId);
        if (extraction == null) {
            throw new ClauseExtractionException("条款抽取任务不存在: " + extractionId);
        }

        extraction.startProcessing();
        return clauseExtractionRepository.update(extraction);
    }

    
    /**
     * 取消条款抽取
     */
    @Transactional
    public ClauseExtraction cancelExtraction(ExtractionId extractionId) {
        ClauseExtraction extraction = clauseExtractionRepository.findById(extractionId);
        if (extraction == null) {
            throw new ClauseExtractionException("条款抽取任务不存在: " + extractionId);
        }

        extraction.cancel();
        return clauseExtractionRepository.update(extraction);
    }

    /**
     * 重置条款抽取任务以便重试
     */
    @Transactional
    public ClauseExtraction resetExtractionForRetry(ExtractionId extractionId) {
        ClauseExtraction extraction = clauseExtractionRepository.findById(extractionId);
        if (extraction == null) {
            throw new ClauseExtractionException("条款抽取任务不存在: " + extractionId);
        }

        if (!extraction.canRetry()) {
            throw new ClauseExtractionException("当前状态不允许重试: " + extraction.getStatus());
        }

        extraction.resetForRetry();
        return clauseExtractionRepository.update(extraction);
    }

    /**
     * 查询条款抽取状态
     */
    public ClauseExtraction getExtractionStatus(ExtractionId extractionId) {
        return clauseExtractionRepository.findById(extractionId);
    }

    /**
     * 根据合同ID查询条款抽取状态
     */
    public ClauseExtraction getExtractionStatusByContractId(Long contractId) {
        return clauseExtractionRepository.findByContractId(contractId);
    }

    /**
     * 异步执行条款抽取
     */
    @Transactional
    public void performExtractionAsync(Long contractId, Long createdBy, String fileUuid) {
        try {
            log.info("开始异步执行条款抽取，合同ID: {}, 文件UUID: {}", contractId, fileUuid);

            // 创建抽取任务
            ClauseExtraction extraction = createExtraction(contractId, createdBy);
            ExtractionId extractionId = extraction.getId();

            // 开始处理
            startExtraction(extractionId);

            // 异步执行将由应用层或事件监听器来处理
            log.info("条款抽取任务已创建，等待异步处理: extractionId={}, contractId={}", extractionId, contractId);

        } catch (Exception e) {
            log.error("创建条款抽取任务失败，合同ID: {}", contractId, e);
            throw new ClauseExtractionException("创建条款抽取任务失败: " + e.getMessage(), e);
        }
    }

    /**
     * 处理超时的抽取任务
     */
    @Transactional
    public void handleTimeoutExtractions() {
        // 这里可以添加定时任务逻辑，处理超时的抽取任务
        // 例如，将处理时间超过30分钟的任务标记为失败
        log.debug("检查超时的条款抽取任务");
    }

    /**
     * 根据合同ID删除条款抽取和对应的条款（软删除）
     */
    @Transactional
    public void deleteByContractId(Long contractId) {
        log.info("删除合同相关的条款抽取和条款: contractId={}", contractId);

        // 1. 先删除相关的条款
        clauseRepository.deleteAllByContractId(contractId);

        // 2. 再删除条款抽取记录
        clauseExtractionRepository.deleteByContractId(contractId);

        log.info("成功删除合同相关的条款抽取和条款: contractId={}", contractId);
    }
}