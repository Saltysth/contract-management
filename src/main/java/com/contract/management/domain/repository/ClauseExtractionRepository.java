package com.contract.management.domain.repository;

import com.contract.management.domain.model.ClauseExtraction;
import com.contract.management.domain.model.valueobject.ExtractionId;

/**
 * 条款抽取仓储接口
 *
 * @author SaltyFish
 * @since 1.0.0
 */
public interface ClauseExtractionRepository {

    /**
     * 保存条款抽取
     */
    ClauseExtraction save(ClauseExtraction clauseExtraction);

    /**
     * 根据ID查找条款抽取
     */
    ClauseExtraction findById(ExtractionId id);

    /**
     * 根据合同ID查找条款抽取
     */
    ClauseExtraction findByContractId(Long contractId);

    /**
     * 更新条款抽取
     */
    ClauseExtraction update(ClauseExtraction clauseExtraction);

    /**
     * 删除条款抽取
     */
    void delete(ExtractionId id);

    /**
     * 检查合同是否存在活跃的抽取任务
     */
    boolean existsActiveExtraction(Long contractId);

    /**
     * 根据合同ID删除条款抽取（软删除）
     */
    void deleteByContractId(Long contractId);
}