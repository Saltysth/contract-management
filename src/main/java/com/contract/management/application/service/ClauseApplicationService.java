package com.contract.management.application.service;

import com.contract.management.application.convertor.ClauseApplicationConvertor;
import com.contract.management.application.dto.ClauseDTO;
import com.contract.management.application.dto.ClauseQueryDTO;
import com.contract.management.domain.model.Clause;
import com.contract.management.domain.model.ClauseId;
import com.contract.management.domain.model.ClauseType;
import com.contract.management.domain.repository.ClauseFilters;
import com.contract.management.domain.service.ClauseDomainService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

/**
 * 条款应用服务
 * 负责条款的业务流程编排和DTO转换
 *
 * @author SaltyFish
 * @since 1.0.0
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class ClauseApplicationService {

    private final ClauseDomainService clauseDomainService;
    private final ClauseApplicationConvertor clauseApplicationConvertor;


    /**
     * 更新条款
     *
     * @param clauseDTO 条款DTO
     * @return 更新后的条款DTO
     */
    @Transactional
    public ClauseDTO updateClause(ClauseDTO clauseDTO) {
        log.info("更新条款: id={}", clauseDTO.getId());

        // DTO -> 领域模型
        Clause clause = clauseApplicationConvertor.toDomainForUpdate(clauseDTO);

        // 委托给领域服务处理业务逻辑
        Clause updatedClause = clauseDomainService.updateClause(clause);

        // 领域模型 -> DTO
        ClauseDTO result = clauseApplicationConvertor.toDTO(updatedClause);
        log.info("条款更新成功: id={}", result.getId());
        return result;
    }

    /**
     * 根据ID查找条款
     *
     * @param id 条款ID
     * @return 条款DTO
     */
    public Optional<ClauseDTO> findById(Long id) {
        if (id == null) {
            return Optional.empty();
        }

        Optional<Clause> clause = clauseDomainService.findById(ClauseId.of(id));
        return clause.map(clauseApplicationConvertor::toDTO);
    }

    /**
     * 删除条款
     *
     * @param id 条款ID
     */
    @Transactional
    public void deleteClause(Long id) {
        log.info("删除条款: id={}", id);
        clauseDomainService.deleteClause(ClauseId.of(id));
        log.info("条款删除成功: id={}", id);
    }

    /**
     * 根据抽取任务ID查找条款
     *
     * @param extractionTaskId 抽取任务ID
     * @return 条款DTO列表
     */
    public List<ClauseDTO> findByExtractionTaskId(Long extractionTaskId) {
        List<Clause> clauses = clauseDomainService.findByExtractionTaskId(extractionTaskId);
        return clauseApplicationConvertor.toDTOList(clauses);
    }

    /**
     * 根据合同ID查找条款
     *
     * @param contractId 合同ID
     * @return 条款DTO列表
     */
    public List<ClauseDTO> findByContractId(Long contractId) {
        List<Clause> clauses = clauseDomainService.findByContractId(contractId);
        return clauseApplicationConvertor.toDTOList(clauses);
    }

    /**
     * 分页查询条款
     *
     * @param queryDTO 查询条件
     * @return 分页结果
     */
    public Page<ClauseDTO> findByFilters(ClauseQueryDTO queryDTO) {
        // 转换查询条件
        ClauseFilters filters = clauseApplicationConvertor.toFilters(queryDTO);

        // 构建分页参数
        Sort sort = Sort.by(Sort.Direction.fromString(queryDTO.getDirection()), queryDTO.getSort());
        Pageable pageable = PageRequest.of(queryDTO.getPage(), queryDTO.getSize(), sort);

        // 执行查询
        Page<Clause> clausePage = clauseDomainService.findByFilters(filters, pageable);

        // 转换为DTO
        List<ClauseDTO> clauseDTOs = clauseApplicationConvertor.toDTOList(clausePage.getContent());

        return new org.springframework.data.domain.PageImpl<>(
                clauseDTOs,
                pageable,
                clausePage.getTotalElements()
        );
    }

    /**
     * 更新条款分析结果
     *
     * @param id 条款ID
     * @param confidenceScore 置信度分数
     * @param extractedEntities 提取的实体
     * @param riskLevel 风险等级
     * @param riskFactors 风险因子
     * @return 更新后的条款DTO
     */
    @Transactional
    public ClauseDTO updateAnalysisResult(Long id, BigDecimal confidenceScore,
                                         String extractedEntities, com.contract.management.domain.model.RiskLevel riskLevel,
                                         String riskFactors) {
        log.info("更新条款分析结果: id={}, confidenceScore={}, riskLevel={}",
                id, confidenceScore, riskLevel);

        Clause updatedClause = clauseDomainService.updateAnalysisResult(
                ClauseId.of(id), confidenceScore, extractedEntities, riskLevel, riskFactors
        );

        ClauseDTO result = clauseApplicationConvertor.toDTO(updatedClause);
        log.info("条款分析结果更新成功: id={}", result.getId());
        return result;
    }

    /**
     * 批量创建条款
     *
     * @param clauseDTOs 条款DTO列表
     * @return 创建后的条款DTO列表
     */
    @Transactional
    public List<ClauseDTO> createClauses(List<ClauseDTO> clauseDTOs) {
        if (clauseDTOs == null || clauseDTOs.isEmpty()) {
            return List.of();
        }

        log.info("批量创建条款: 数量={}", clauseDTOs.size());

        List<Clause> clauses = clauseDTOs.stream()
                .map(clauseApplicationConvertor::toDomainForCreate)
                .toList();

        List<Clause> createdClauses = clauseDomainService.saveAllClauses(clauses);
        List<ClauseDTO> result = clauseApplicationConvertor.toDTOList(createdClauses);

        log.info("批量创建条款成功: 数量={}", result.size());
        return result;
    }

    /**
     * 根据抽取任务ID删除所有条款
     *
     * @param extractionTaskId 抽取任务ID
     */
    @Transactional
    public void deleteAllByExtractionTaskId(Long extractionTaskId) {
        log.info("根据抽取任务ID删除所有条款: extractionTaskId={}", extractionTaskId);
        clauseDomainService.deleteAllByExtractionTaskId(extractionTaskId);
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
        clauseDomainService.deleteAllByContractId(contractId);
        log.info("根据合同ID删除所有条款成功: contractId={}", contractId);
    }
}