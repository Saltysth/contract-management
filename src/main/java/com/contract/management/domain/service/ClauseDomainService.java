package com.contract.management.domain.service;

import com.contract.management.domain.model.Clause;
import com.contract.management.domain.model.ClauseId;
import com.contract.management.domain.model.valueobject.ClauseContent;
import com.contract.management.domain.model.valueobject.ClauseTitle;
import com.contract.management.domain.repository.ClauseFilters;
import com.contract.management.domain.repository.ClauseRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

/**
 * 条款领域服务
 * 负责条款的业务逻辑处理
 *
 * @author SaltyFish
 * @since 1.0.0
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class ClauseDomainService {

    private final ClauseRepository clauseRepository;

    /**
     * 创建条款
     *
     * @param clause 条款
     * @return 创建后的条款
     */
    @Transactional
    public Clause createClause(Clause clause) {
        log.info("创建条款: extractionTaskId={}, contractId={}, type={}",
                clause.getExtractionTaskId(), clause.getContractId(), clause.getClauseType());

        // 领域模型验证已在构造函数中完成
        Clause savedClause = clauseRepository.save(clause);

        log.info("条款创建成功: id={}", savedClause.getId().getValue());
        return savedClause;
    }

    /**
     * 更新条款
     *
     * @param clause 条款
     * @return 更新后的条款
     */
    @Transactional
    public Clause updateClause(Clause clause) {
        log.info("更新条款: id={}", clause.getId().getValue());

        // 检查条款是否存在
        Optional<Clause> existingClause = clauseRepository.findById(clause.getId());
        if (existingClause.isEmpty()) {
            throw new ClauseDomainException("条款不存在，ID: " + clause.getId().getValue());
        }

        // 验证更新操作
        clause.validateForCreation();

        Clause updatedClause = clauseRepository.update(clause);

        log.info("条款更新成功: id={}", updatedClause.getId().getValue());
        return updatedClause;
    }

    /**
     * 根据ID查找条款
     *
     * @param id 条款ID
     * @return 条款
     */
    public Optional<Clause> findById(ClauseId id) {
        if (id == null) {
            return Optional.empty();
        }
        return clauseRepository.findById(id);
    }

    /**
     * 删除条款
     *
     * @param id 条款ID
     */
    @Transactional
    public void deleteClause(ClauseId id) {
        log.info("删除条款: id={}", id.getValue());

        if (!clauseRepository.existsById(id)) {
            throw new ClauseDomainException("条款不存在，ID: " + id.getValue());
        }

        clauseRepository.delete(id);
        log.info("条款删除成功: id={}", id.getValue());
    }

    /**
     * 根据合同ID查找条款
     *
     * @param contractId 合同ID
     * @return 条款列表
     */
    public List<Clause> findByContractId(Long contractId) {
        return clauseRepository.findByContractId(contractId);
    }

    /**
     * 根据过滤条件分页查询条款
     *
     * @param filters 过滤条件
     * @param pageable 分页参数
     * @return 分页结果
     */
    public Page<Clause> findByFilters(ClauseFilters filters, Pageable pageable) {
        return clauseRepository.findByFilters(filters, pageable);
    }

    /**
     * 更新条款分析结果
     *
     * @param clauseId 条款ID
     * @param confidenceScore 置信度分数
     * @param extractedEntities 提取的实体
     * @param riskLevel 风险等级
     * @param riskFactors 风险因子
     * @return 更新后的条款
     */
    @Transactional
    public Clause updateAnalysisResult(ClauseId clauseId,
                                      java.math.BigDecimal confidenceScore,
                                      String extractedEntities,
                                      com.contract.management.domain.model.RiskLevel riskLevel,
                                      String riskFactors) {
        log.info("更新条款分析结果: id={}, confidenceScore={}, riskLevel={}",
                clauseId.getValue(), confidenceScore, riskLevel);

        Optional<Clause> existingClause = clauseRepository.findById(clauseId);
        if (existingClause.isEmpty()) {
            throw new ClauseDomainException("条款不存在，ID: " + clauseId.getValue());
        }

        Clause clause = existingClause.get();
        clause.updateAnalysisResult(confidenceScore, extractedEntities, riskLevel, riskFactors);

        return clauseRepository.update(clause);
    }

    /**
     * 批量保存条款
     *
     * @param clauses 条款列表
     * @return 保存后的条款列表
     */
    @Transactional
    public List<Clause> saveAllClauses(List<Clause> clauses) {
        if (clauses == null || clauses.isEmpty()) {
            return List.of();
        }

        log.info("批量保存条款: 数量={}", clauses.size());
        List<Clause> savedClauses = clauseRepository.saveAll(clauses);
        log.info("批量保存条款成功: 数量={}", savedClauses.size());

        return savedClauses;
    }


    /**
     * 根据抽取任务ID删除所有条款
     *
     * @param extractionTaskId 抽取任务ID
     */
    @Transactional
    public void deleteAllByExtractionTaskId(Long extractionTaskId) {
        log.info("根据抽取任务ID删除所有条款: extractionTaskId={}", extractionTaskId);
        clauseRepository.deleteAllByExtractionTaskId(extractionTaskId);
        log.info("根据抽取任务ID删除所有条款成功: extractionTaskId={}", extractionTaskId);
    }

    /**
     * 根据合同ID删除所有条款
     *
     * @param contractId 合同ID
     */
    @Transactional
    public void deleteAllByContractId(Long contractId) {
        log.info("根据合同ID删除所有条款: contractId={}", contractId);
        clauseRepository.deleteAllByContractId(contractId);
        log.info("根据合同ID删除所有条款成功: contractId={}", contractId);
    }

    public List<Clause> findByExtractionTaskId(Long extractionTaskId) {
        return clauseRepository.findByExtractionTaskId(extractionTaskId);
    }
}