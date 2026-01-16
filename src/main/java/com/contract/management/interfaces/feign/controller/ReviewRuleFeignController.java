package com.contract.management.interfaces.feign.controller;

import com.contract.common.feign.dto.ReviewRuleFeignDTO;
import com.contract.common.feign.dto.ReviewRulePageResultFeignDTO;
import com.contract.common.feign.dto.ReviewRuleQueryFeignDTO;
import com.contract.management.application.dto.ReviewRuleDTO;
import com.contract.management.application.dto.ReviewRuleQueryRequest;
import com.contract.management.application.service.ReviewRuleApplicationService;
import com.contract.management.interfaces.rest.api.v1.convertor.ReviewRuleRestConvertor;
import com.contract.management.interfaces.rest.api.v1.dto.common.ApiResponse;
import com.contract.management.interfaces.rest.api.v1.dto.request.ReviewRuleSearchRequest;
import com.contract.management.interfaces.rest.api.v1.dto.response.ReviewRuleResponse;
import com.ruoyi.feign.annotation.RemotePreAuthorize;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.List;
import java.util.Optional;

/**
 * 审查规则Feign控制器
 * 提供与ReviewRuleController完全一致的接口，供其他微服务调用
 */
@Slf4j
@RestController
@RequestMapping("/feign/v1/review-rules")
@RequiredArgsConstructor
@Tag(name = "审查规则Feign接口", description = "合同审查规则管理Feign接口，与ReviewRuleController接口完全一致")
public class ReviewRuleFeignController {

    private final ReviewRuleApplicationService reviewRuleApplicationService;
    private final ReviewRuleRestConvertor reviewRuleRestConvertor;

    /**
     * 根据ID查询审查规则
     * 与ReviewRuleController.getRuleById接口完全一致
     */
    @RemotePreAuthorize("@ss.hasRole('admin')")
    @GetMapping("/{id}")
    @Operation(summary = "查询审查规则", description = "根据规则ID查询审查规则详情")
    public ResponseEntity<ApiResponse<ReviewRuleResponse>> getRuleById(
            @Parameter(description = "规则ID", required = true)
            @PathVariable Long id) {

        log.info("Feign调用：Finding review rule by ID: {}", id);

        Optional<ReviewRuleDTO> ruleOpt = reviewRuleApplicationService.getRuleById(id);

        if (ruleOpt.isEmpty()) {
            log.warn("Feign调用：Review rule not found: {}", id);
            return ResponseEntity.notFound().build();
        }

        ReviewRuleResponse response = reviewRuleRestConvertor.toResponse(ruleOpt.get());
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    /**
     * 启用审查规则
     * 与ReviewRuleController.enableRule接口完全一致
     */
    @RemotePreAuthorize("@ss.hasRole('admin')")
    @PutMapping("/{id}/enable")
    @Operation(summary = "启用审查规则", description = "启用指定的审查规则")
    public ResponseEntity<ApiResponse<ReviewRuleResponse>> enableRule(
            @Parameter(description = "规则ID", required = true)
            @PathVariable Long id) {

        log.info("Feign调用：Enabling review rule: {}", id);

        Optional<ReviewRuleDTO> ruleOpt = reviewRuleApplicationService.enableRule(id);

        if (ruleOpt.isEmpty()) {
            log.warn("Feign调用：Review rule not found: {}", id);
            return ResponseEntity.notFound().build();
        }

        ReviewRuleResponse response = reviewRuleRestConvertor.toResponse(ruleOpt.get());
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    /**
     * 禁用审查规则
     * 与ReviewRuleController.disableRule接口完全一致
     */
    @RemotePreAuthorize("@ss.hasRole('admin')")
    @PutMapping("/{id}/disable")
    @Operation(summary = "禁用审查规则", description = "禁用指定的审查规则")
    public ResponseEntity<ApiResponse<ReviewRuleResponse>> disableRule(
            @Parameter(description = "规则ID", required = true)
            @PathVariable Long id) {

        log.info("Feign调用：Disabling review rule: {}", id);

        Optional<ReviewRuleDTO> ruleOpt = reviewRuleApplicationService.disableRule(id);

        if (ruleOpt.isEmpty()) {
            log.warn("Feign调用：Review rule not found: {}", id);
            return ResponseEntity.notFound().build();
        }

        ReviewRuleResponse response = reviewRuleRestConvertor.toResponse(ruleOpt.get());
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    /**
     * 分页搜索审查规则
     * 与ReviewRuleFeignClient.searchReviewRules接口完全一致
     */
    @RemotePreAuthorize("@ss.hasRole('admin')")
    @PostMapping("/search")
    @Operation(summary = "搜索审查规则", description = "根据条件分页搜索审查规则")
    public ReviewRulePageResultFeignDTO searchRules(
            @Valid @RequestBody ReviewRuleQueryFeignDTO queryDTO) {

        log.info("Feign调用：Searching review rules with conditions: {}", queryDTO);

        // 1. 转换查询请求：从FeignDTO到应用层DTO
        ReviewRuleQueryRequest queryRequest = convertFeignDTOToQueryRequest(queryDTO);

        // 2. 调用应用服务搜索
        Page<ReviewRuleDTO> rulePage = reviewRuleApplicationService.searchRules(queryRequest);

        // 3. 转换响应结果为FeignDTO
        List<ReviewRuleFeignDTO> ruleFeignDTOs = rulePage.getContent().stream()
                .map(this::convertToFeignDTO)
                .collect(java.util.stream.Collectors.toList());

        // 4. 构建分页结果
        ReviewRulePageResultFeignDTO result = ReviewRulePageResultFeignDTO.Builder.of(
                ruleFeignDTOs,
                rulePage.getTotalElements(),
                rulePage.getNumber() + 1, // Spring的Page从0开始，Feign从1开始
                rulePage.getSize()
        );

        log.info("Feign调用查询到审查规则数量: {}", result.getTotal());
        return result;
    }

    /**
     * 获取所有审查规则
     * 与ReviewRuleController.getAllRules接口完全一致
     */
    @RemotePreAuthorize("@ss.hasRole('admin')")
    @GetMapping
    @Operation(summary = "获取所有审查规则", description = "获取所有审查规则列表")
    public ResponseEntity<ApiResponse<List<ReviewRuleResponse>>> getAllRules() {

        log.info("Feign调用：Getting all review rules");

        List<ReviewRuleDTO> rules = reviewRuleApplicationService.getAllRules();
        List<ReviewRuleResponse> responses = rules.stream()
                .map(reviewRuleRestConvertor::toResponse)
                .toList();

        return ResponseEntity.ok(ApiResponse.success(responses));
    }

    /**
     * 根据规则类型查询规则
     * 与ReviewRuleController.getRulesByType接口完全一致
     */
    @RemotePreAuthorize("@ss.hasRole('admin')")
    @GetMapping("/by-type/{ruleType}")
    @Operation(summary = "按类型查询规则", description = "根据规则类型查询审查规则")
    public ResponseEntity<ApiResponse<List<ReviewRuleResponse>>> getRulesByType(
            @Parameter(description = "规则类型", required = true, example = "specific")
            @PathVariable String ruleType) {

        log.info("Feign调用：Getting review rules by type: {}", ruleType);

        List<ReviewRuleDTO> rules = reviewRuleApplicationService.getRulesByType(ruleType);
        List<ReviewRuleResponse> responses = rules.stream()
                .map(reviewRuleRestConvertor::toResponse)
                .toList();

        return ResponseEntity.ok(ApiResponse.success(responses));
    }

    /**
     * 根据启用状态查询规则
     * 与ReviewRuleController.getRulesByEnabled接口完全一致
     */
    @RemotePreAuthorize("@ss.hasRole('admin')")
    @GetMapping("/by-enabled/{enabled}")
    @Operation(summary = "按启用状态查询规则", description = "根据启用状态查询审查规则")
    public ResponseEntity<ApiResponse<List<ReviewRuleResponse>>> getRulesByEnabled(
            @Parameter(description = "启用状态", required = true, example = "true")
            @PathVariable Boolean enabled) {

        log.info("Feign调用：Getting review rules by enabled status: {}", enabled);

        List<ReviewRuleDTO> rules = reviewRuleApplicationService.getRulesByEnabled(enabled);
        List<ReviewRuleResponse> responses = rules.stream()
                .map(reviewRuleRestConvertor::toResponse)
                .toList();

        return ResponseEntity.ok(ApiResponse.success(responses));
    }

    /**
     * 检查规则名称是否可用
     * 与ReviewRuleController.checkRuleName接口完全一致
     */
    @RemotePreAuthorize("@ss.hasRole('admin')")
    @GetMapping("/check-name")
    @Operation(summary = "检查规则名称", description = "检查规则名称是否可用")
    public ResponseEntity<ApiResponse<Boolean>> checkRuleName(
            @Parameter(description = "规则名称", required = true)
            @RequestParam String ruleName) {

        log.info("Feign调用：Checking rule name availability: {}", ruleName);

        boolean available = reviewRuleApplicationService.isRuleNameAvailable(ruleName);

        return ResponseEntity.ok(ApiResponse.success(available));
    }

    /**
     * 统计规则数量
     * 与ReviewRuleController.countRules接口完全一致
     */
    @RemotePreAuthorize("@ss.hasRole('admin')")
    @GetMapping("/count")
    @Operation(summary = "统计规则数量", description = "统计审查规则数量")
    public ResponseEntity<ApiResponse<Long>> countRules(
            @Parameter(description = "启用状态")
            @RequestParam(required = false) Boolean enabled) {

        log.info("Feign调用：Counting review rules with enabled status: {}", enabled);

        long count = reviewRuleApplicationService.countRules(enabled);

        return ResponseEntity.ok(ApiResponse.success(count));
    }

    /**
     * 根据合同类型和条款类型查询适用的规则
     * 与ReviewRuleController.getApplicableRules接口完全一致
     */
    @RemotePreAuthorize("@ss.hasRole('admin')")
    @GetMapping("/applicable")
    @Operation(summary = "查询适用规则", description = "根据合同类型和条款类型查询适用的审查规则")
    public ResponseEntity<ApiResponse<List<ReviewRuleResponse>>> getApplicableRules(
            @Parameter(description = "合同类型", required = true)
            @RequestParam String contractType,
            @Parameter(description = "条款类型", required = true)
            @RequestParam String clauseType) {

        log.info("Feign调用：Getting applicable rules for contract: {}, clause: {}", contractType, clauseType);

        List<ReviewRuleDTO> rules = reviewRuleApplicationService.getApplicableRules(contractType, clauseType);
        List<ReviewRuleResponse> responses = rules.stream()
                .map(reviewRuleRestConvertor::toResponse)
                .toList();

        return ResponseEntity.ok(ApiResponse.success(responses));
    }

    /**
     * 将Feign查询DTO转换为应用层查询请求
     *
     * @param queryDTO Feign查询DTO
     * @return 应用层查询请求
     */
    private ReviewRuleQueryRequest convertFeignDTOToQueryRequest(ReviewRuleQueryFeignDTO queryDTO) {
        if (queryDTO == null) {
            return new ReviewRuleQueryRequest();
        }

        ReviewRuleQueryRequest queryRequest = new ReviewRuleQueryRequest();
        queryRequest.setRuleName(queryDTO.getRuleName());

        // 处理ruleType列表 - 取第一个或null
        if (queryDTO.getRuleTypeList() != null && !queryDTO.getRuleTypeList().isEmpty()) {
            queryRequest.setRuleType(queryDTO.getRuleTypeList().get(0));
        }

        // 处理applicableContractTypes - 设置为合同类型
        if (queryDTO.getApplicableContractTypes() != null && !queryDTO.getApplicableContractTypes().isEmpty()) {
            queryRequest.setContractType(queryDTO.getApplicableContractTypes().get(0));
        }

        // 处理applicableClauseTypes - 设置为条款类型
        if (queryDTO.getApplicableClauseTypes() != null && !queryDTO.getApplicableClauseTypes().isEmpty()) {
            queryRequest.setClauseType(queryDTO.getApplicableClauseTypes().get(0));
        }

        queryRequest.setEnabled(queryDTO.getEnabled());
        queryRequest.setPage(queryDTO.getPageNum() != null ? queryDTO.getPageNum() : 0);
        queryRequest.setSize(queryDTO.getPageSize() != null ? queryDTO.getPageSize() : 20);
        queryRequest.setSort(queryDTO.getOrderBy() != null ? queryDTO.getOrderBy() : "createTime");
        queryRequest.setDirection(queryDTO.getOrderDirection() != null ? queryDTO.getOrderDirection() : "DESC");

        return queryRequest;
    }

    /**
     * 将应用层DTO转换为FeignDTO
     *
     * @param ruleDTO 应用层DTO
     * @return FeignDTO
     */
    private ReviewRuleFeignDTO convertToFeignDTO(ReviewRuleDTO ruleDTO) {
        if (ruleDTO == null) {
            return null;
        }

        return ReviewRuleFeignDTO.builder()
                .id(ruleDTO.getId())
                .ruleName(ruleDTO.getRuleName())
                .ruleTypeDescription(ruleDTO.getRuleTypeDescription())
                .applicableContractTypes(ruleDTO.getApplicableContractType())
                .applicableClauseTypes(ruleDTO.getApplicableClauseType())
                .ruleContent(ruleDTO.getRuleContent())
                .promptModeCode(ruleDTO.getPromptModeCode())
                .promptModeDescription(ruleDTO.getPromptModeDescription())
                .enabled(ruleDTO.getEnabled())
                .createTime(ruleDTO.getCreateTime())
                .updateTime(ruleDTO.getUpdateTime())
                .build();
    }
}