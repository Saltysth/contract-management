package com.contract.management.domain.repository;

import com.contract.management.domain.model.ClassificationResult;
import com.contract.management.domain.model.ClassificationResultId;
import com.contract.management.domain.model.ContractId;

import java.util.List;
import java.util.Optional;

/**
 * 合同分类结果仓储接口
 *
 * @author SaltyFish
 * @since 1.0.0
 */
public interface ClassificationRepository {

    /**
     * 保存分类结果
     *
     * @param classificationResult 分类结果
     * @return 保存后的分类结果
     */
    ClassificationResult save(ClassificationResult classificationResult);

    /**
     * 根据ID查找分类结果
     *
     * @param id 分类结果ID
     * @return 分类结果
     */
    Optional<ClassificationResult> findById(ClassificationResultId id);

    /**
     * 查找合同的活跃分类结果
     *
     * @param contractId 合同ID
     * @return 活跃的分类结果
     */
    Optional<ClassificationResult> findActiveByContractId(ContractId contractId);

    /**
     * 查找合同的所有分类结果（包括历史记录）
     *
     * @param contractId 合同ID
     * @return 所有分类结果列表
     */
    List<ClassificationResult> findAllByContractId(ContractId contractId);

    /**
     * 停用合同的活跃分类结果
     *
     * @param contractId 合同ID
     * @param operatorId 操作人ID
     */
    void deactivateByContractId(ContractId contractId, Long operatorId);

    /**
     * 检查分类结果是否存在
     *
     * @param id 分类结果ID
     * @return 是否存在
     */
    boolean existsById(ClassificationResultId id);
}