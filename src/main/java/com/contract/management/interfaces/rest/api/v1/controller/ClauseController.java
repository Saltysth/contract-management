package com.contract.management.interfaces.rest.api.v1.controller;

import com.contract.management.application.dto.ClauseDTO;
import com.contract.management.application.dto.ClauseQueryDTO;
import com.contract.management.application.service.ClauseApplicationService;
import com.contract.management.domain.model.ClauseType;
import com.contract.management.interfaces.rest.api.v1.convertor.ClauseRestConvertor;
import com.contract.management.interfaces.rest.api.v1.dto.request.ClauseQueryRequest;
import com.contract.management.interfaces.rest.api.v1.dto.request.CreateClauseRequest;
import com.contract.management.interfaces.rest.api.v1.dto.request.UpdateClauseRequest;
import com.contract.management.interfaces.rest.api.v1.dto.response.ClauseResponse;
import com.ruoyi.feign.annotation.RemotePreAuthorize;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 条款管理REST控制器
 *
 * @author SaltyFish
 * @since 1.0.0
 */
@Slf4j
@RestController
@RequestMapping("/api/v1/clauses")
@RequiredArgsConstructor
@Validated
@Tag(name = "条款管理", description = "条款CRUD接口")
public class ClauseController {

    private final ClauseApplicationService clauseApplicationService;
    private final ClauseRestConvertor clauseRestConvertor;

    /**
     * 根据ID获取条款详情
     *
     * @param id 条款ID
     * @return 条款详情
     */
    @RemotePreAuthorize("@ss.hasAnyRoles('admin,common,guest')")
    @GetMapping("/{id}")
    @Operation(summary = "获取条款详情", description = "根据ID获取条款详细信息")
    public ResponseEntity<ClauseResponse> getClauseById(
            @Parameter(description = "条款ID", required = true)
            @PathVariable("id") @NotNull @Positive Long id) {

        log.info("查询条款详情，ID: {}", id);

        return clauseApplicationService.findById(id)
                .map(clauseDTO -> {
                    ClauseResponse response = clauseRestConvertor.toResponse(clauseDTO);
                    log.info("条款查询成功，ID: {}", id);
                    return ResponseEntity.ok(response);
                })
                .orElse(ResponseEntity.notFound().build());
    }

    /**
     * 更新条款
     *
     * @param id 条款ID
     * @param request 更新请求
     * @return 更新后的条款信息
     */
    @RemotePreAuthorize("@ss.hasAnyRoles('admin,common')")
    @PutMapping("/{id}")
    @Operation(summary = "更新条款", description = "更新指定ID的条款信息")
    public ResponseEntity<ClauseResponse> updateClause(
            @Parameter(description = "条款ID", required = true)
            @PathVariable("id") @NotNull @Positive Long id,
            @Valid @RequestBody UpdateClauseRequest request) {

        log.info("更新条款请求，ID: {}", id);

        ClauseDTO clauseDTO = clauseRestConvertor.toApplicationDTO(request);
        clauseDTO.setId(id);

        ClauseDTO updatedClause = clauseApplicationService.updateClause(clauseDTO);
        ClauseResponse response = clauseRestConvertor.toResponse(updatedClause);

        log.info("条款更新成功，ID: {}", response.getId());
        return ResponseEntity.ok(response);
    }

    /**
     * 删除条款
     *
     * @param id 条款ID
     * @return 删除结果
     */
    @RemotePreAuthorize("@ss.hasAnyRoles('admin,common')")
    @DeleteMapping("/{id}")
    @Operation(summary = "删除条款", description = "删除指定ID的条款")
    public ResponseEntity<Void> deleteClause(
            @Parameter(description = "条款ID", required = true)
            @PathVariable("id") @NotNull @Positive Long id) {

        log.info("删除条款请求，ID: {}", id);
        clauseApplicationService.deleteClause(id);
        log.info("条款删除成功，ID: {}", id);

        return ResponseEntity.noContent().build();
    }

    /**
     * 根据抽取任务ID查询条款列表
     *
     * @param extractionTaskId 抽取任务ID
     * @return 条款列表
     */
    @RemotePreAuthorize("@ss.hasAnyRoles('admin,common,guest')")
    @GetMapping("/extraction-task/{extractionTaskId}")
    @Operation(summary = "根据抽取任务ID查询条款", description = "查询指定抽取任务ID的所有条款")
    public ResponseEntity<List<ClauseResponse>> getClausesByExtractionTaskId(
            @Parameter(description = "抽取任务ID", required = true)
            @PathVariable("extractionTaskId") @NotNull @Positive Long extractionTaskId) {

        log.info("根据抽取任务ID查询条款，extractionTaskId: {}", extractionTaskId);

        List<ClauseDTO> clauses = clauseApplicationService.findByExtractionTaskId(extractionTaskId);
        List<ClauseResponse> responses = clauseRestConvertor.toResponseList(clauses);

        log.info("根据抽取任务ID查询条款成功，extractionTaskId: {}, 数量: {}", extractionTaskId, responses.size());
        return ResponseEntity.ok(responses);
    }

    /**
     * 根据合同ID查询条款列表
     *
     * @param contractId 合同ID
     * @return 条款列表
     */
    @RemotePreAuthorize("@ss.hasAnyRoles('admin,common,guest')")
    @GetMapping("/contract/{contractId}")
    @Operation(summary = "根据合同ID查询条款", description = "查询指定合同ID的所有条款")
    public ResponseEntity<List<ClauseResponse>> getClausesByContractId(
            @Parameter(description = "合同ID", required = true)
            @PathVariable("contractId") @NotNull @Positive Long contractId) {

        log.info("根据合同ID查询条款，contractId: {}", contractId);

        List<ClauseDTO> clauses = clauseApplicationService.findByContractId(contractId);
        List<ClauseResponse> responses = clauseRestConvertor.toResponseList(clauses);

        log.info("根据合同ID查询条款成功，contractId: {}, 数量: {}", contractId, responses.size());
        return ResponseEntity.ok(responses);
    }

    /**
     * 分页查询条款
     *
     * @param request 查询条件
     * @return 分页结果
     */
    @RemotePreAuthorize("@ss.hasAnyRoles('admin,common,guest')")
    @GetMapping("/search")
    @Operation(summary = "分页查询条款", description = "根据条件分页查询条款")
    public ResponseEntity<Page<ClauseResponse>> searchClauses(
            @Valid @ModelAttribute ClauseQueryRequest request) {

        log.info("分页查询条款请求: {}", request);

        ClauseQueryDTO queryDTO = clauseRestConvertor.toApplicationDTO(request);
        Page<ClauseDTO> clausePage = clauseApplicationService.findByFilters(queryDTO);
        Page<ClauseResponse> responsePage = clausePage.map(clauseRestConvertor::toResponse);

        log.info("分页查询条款成功，总数: {}", responsePage.getTotalElements());
        return ResponseEntity.ok(responsePage);
    }

    /**
     * 更新条款分析结果
     *
     * @param id 条款ID
     * @param confidenceScore 置信度分数
     * @param extractedEntities 提取的实体
     * @param riskLevel 风险等级
     * @param riskFactors 风险因子
     * @return 更新后的条款信息
     */
    @RemotePreAuthorize("@ss.hasAnyRoles('admin,common')")
    @PutMapping("/{id}/analysis-result")
    @Operation(summary = "更新条款分析结果", description = "更新条款的AI分析结果")
    public ResponseEntity<ClauseResponse> updateAnalysisResult(
            @Parameter(description = "条款ID", required = true)
            @PathVariable("id") @NotNull @Positive Long id,
            @Parameter(description = "置信度分数", example = "95.50")
            @RequestParam(required = false) BigDecimal confidenceScore,
            @Parameter(description = "提取的实体")
            @RequestParam(required = false) String extractedEntities,
            @Parameter(description = "风险等级", example = "LOW")
            @RequestParam(required = false) com.contract.management.domain.model.RiskLevel riskLevel,
            @Parameter(description = "风险因子")
            @RequestParam(required = false) String riskFactors) {

        log.info("更新条款分析结果，ID: {}, confidenceScore: {}, riskLevel: {}",
                id, confidenceScore, riskLevel);

        ClauseDTO updatedClause = clauseApplicationService.updateAnalysisResult(
                id, confidenceScore, extractedEntities, riskLevel, riskFactors);
        ClauseResponse response = clauseRestConvertor.toResponse(updatedClause);

        log.info("条款分析结果更新成功，ID: {}", response.getId());
        return ResponseEntity.ok(response);
    }

    /**
     * 根据抽取任务ID删除所有条款
     *
     * @param extractionTaskId 抽取任务ID
     * @return 删除结果
     */
    @RemotePreAuthorize("@ss.hasAnyRoles('admin,common')")
    @DeleteMapping("/extraction-task/{extractionTaskId}")
    @Operation(summary = "根据抽取任务ID删除所有条款", description = "删除指定抽取任务ID的所有条款")
    public ResponseEntity<Void> deleteClausesByExtractionTaskId(
            @Parameter(description = "抽取任务ID", required = true)
            @PathVariable("extractionTaskId") @NotNull @Positive Long extractionTaskId) {

        log.info("根据抽取任务ID删除所有条款，extractionTaskId: {}", extractionTaskId);
        clauseApplicationService.deleteAllByExtractionTaskId(extractionTaskId);
        log.info("根据抽取任务ID删除所有条款成功，extractionTaskId: {}", extractionTaskId);

        return ResponseEntity.noContent().build();
    }

    /**
     * 根据合同ID删除所有条款
     *
     * @param contractId 合同ID
     * @return 删除结果
     */
    @RemotePreAuthorize("@ss.hasAnyRoles('admin,common')")
    @DeleteMapping("/contract/{contractId}")
    @Operation(summary = "根据合同ID删除所有条款", description = "删除指定合同ID的所有条款")
    public ResponseEntity<Void> deleteClausesByContractId(
            @Parameter(description = "合同ID", required = true)
            @PathVariable("contractId") @NotNull @Positive Long contractId) {

        log.info("根据合同ID删除所有条款，contractId: {}", contractId);
        clauseApplicationService.deleteAllByContractId(contractId);
        log.info("根据合同ID删除所有条款成功，contractId: {}", contractId);

        return ResponseEntity.noContent().build();
    }

    /**
     * 获取条款类型列表
     *
     * @return 条款类型列表
     */
    @RemotePreAuthorize("@ss.hasAnyRoles('admin,common,guest')")
    @GetMapping("/clause-types")
    @Operation(summary = "获取条款类型列表", description = "获取所有支持的条款类型中文名称")
    public ResponseEntity<List<String>> getClauseTypes() {

        log.info("获取条款类型列表");

        List<String> clauseTypes = Arrays.stream(ClauseType.values())
                .map(ClauseType::getDescription)
                .collect(Collectors.toList());

        log.info("获取到条款类型数量: {}", clauseTypes.size());
        return ResponseEntity.ok(clauseTypes);
    }
}