package com.contract.management.interfaces.rest.api.v1.controller;

import com.contract.common.enums.ReviewTypeDetail;
import com.contract.management.application.dto.ReviewRuleDTO;
import com.contract.management.application.dto.ReviewRuleQueryRequest;
import com.contract.management.application.service.ReviewRuleApplicationService;
import com.contract.management.interfaces.rest.api.v1.convertor.ReviewRuleRestConvertor;
import com.contract.management.interfaces.rest.api.v1.dto.common.ApiResponse;
import com.contract.management.interfaces.rest.api.v1.dto.request.CreateReviewRuleRequest;
import com.contract.management.interfaces.rest.api.v1.dto.request.ReviewRuleSearchRequest;
import com.contract.management.interfaces.rest.api.v1.dto.request.UpdateReviewRuleRequest;
import com.contract.management.interfaces.rest.api.v1.dto.response.ReviewRuleResponse;
import com.contract.management.interfaces.rest.api.v1.dto.response.ReviewTypeDetailResponse;
import com.ruoyi.feign.annotation.RemotePreAuthorize;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

/**
 * 审查规则控制器
 * 提供审查规则的CRUD操作和查询接口
 * 参考条款控制器的设计模式
 *
 * @author SaltyFish
 * @since 1.0.0
 */
@Slf4j
@RestController
@RequestMapping("/api/v1/review-rules")
@RequiredArgsConstructor
@Tag(name = "审查规则管理", description = "合同审查规则的增删改查和查询接口")
public class ReviewRuleController {

    private final ReviewRuleApplicationService reviewRuleApplicationService;
    private final ReviewRuleRestConvertor reviewRuleRestConvertor;

    /**
     * 创建审查规则
     */
    @RemotePreAuthorize("@ss.hasRole('admin')")
    @PostMapping
    @Operation(summary = "创建审查规则", description = "创建新的合同审查规则")
    public ResponseEntity<ApiResponse<ReviewRuleResponse>> createRule(
            @Valid @RequestBody CreateReviewRuleRequest request) {

        log.info("Creating review rule: {}", request.getRuleName());

        // 1. 请求DTO -> 应用DTO
        var command = reviewRuleRestConvertor.toApplicationDTO(request);

        // 2. 调用应用服务
        ReviewRuleDTO createdRule = reviewRuleApplicationService.createRule(command);

        // 3. 应用DTO -> 响应DTO
        ReviewRuleResponse response = reviewRuleRestConvertor.toResponse(createdRule);

        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.success(response));
    }

    /**
     * 根据ID查询审查规则
     */
    @RemotePreAuthorize("@ss.hasRole('admin')")
    @GetMapping("/{id}")
    @Operation(summary = "查询审查规则", description = "根据规则ID查询审查规则详情")
    public ResponseEntity<ApiResponse<ReviewRuleResponse>> getRuleById(
            @Parameter(description = "规则ID", required = true)
            @PathVariable Long id) {

        log.info("Finding review rule by ID: {}", id);

        Optional<ReviewRuleDTO> ruleOpt = reviewRuleApplicationService.getRuleById(id);

        if (ruleOpt.isEmpty()) {
            log.warn("Review rule not found: {}", id);
            return ResponseEntity.notFound().build();
        }

        ReviewRuleResponse response = reviewRuleRestConvertor.toResponse(ruleOpt.get());
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    /**
     * 更新审查规则
     */
    @RemotePreAuthorize("@ss.hasRole('admin')")
    @PutMapping("/{id}")
    @Operation(summary = "更新审查规则", description = "更新指定的审查规则")
    public ResponseEntity<ApiResponse<ReviewRuleResponse>> updateRule(
            @Parameter(description = "规则ID", required = true)
            @PathVariable Long id,
            @Valid @RequestBody UpdateReviewRuleRequest request) {

        log.info("Updating review rule: {}", id);

        // 1. 请求DTO -> 应用DTO
        var command = reviewRuleRestConvertor.toApplicationDTO(request);

        // 2. 调用应用服务
        ReviewRuleDTO updatedRule = reviewRuleApplicationService.updateRule(id, command);

        // 3. 应用DTO -> 响应DTO
        ReviewRuleResponse response = reviewRuleRestConvertor.toResponse(updatedRule);

        return ResponseEntity.ok(ApiResponse.success(response));
    }

    /**
     * 删除审查规则
     */
    @RemotePreAuthorize("@ss.hasRole('admin')")
    @DeleteMapping("/{id}")
    @Operation(summary = "删除审查规则", description = "删除指定的审查规则")
    public ResponseEntity<ApiResponse<Void>> deleteRule(
            @Parameter(description = "规则ID", required = true)
            @PathVariable Long id) {

        log.info("Deleting review rule: {}", id);

        reviewRuleApplicationService.deleteRule(id);

        return ResponseEntity.noContent().build();
    }

    /**
     * 启用审查规则
     */
    @RemotePreAuthorize("@ss.hasRole('admin')")
    @PutMapping("/{id}/enable")
    @Operation(summary = "启用审查规则", description = "启用指定的审查规则")
    public ResponseEntity<ApiResponse<ReviewRuleResponse>> enableRule(
            @Parameter(description = "规则ID", required = true)
            @PathVariable Long id) {

        log.info("Enabling review rule: {}", id);

        Optional<ReviewRuleDTO> ruleOpt = reviewRuleApplicationService.enableRule(id);

        if (ruleOpt.isEmpty()) {
            log.warn("Review rule not found: {}", id);
            return ResponseEntity.notFound().build();
        }

        ReviewRuleResponse response = reviewRuleRestConvertor.toResponse(ruleOpt.get());
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    /**
     * 禁用审查规则
     */
    @RemotePreAuthorize("@ss.hasRole('admin')")
    @PutMapping("/{id}/disable")
    @Operation(summary = "禁用审查规则", description = "禁用指定的审查规则")
    public ResponseEntity<ApiResponse<ReviewRuleResponse>> disableRule(
            @Parameter(description = "规则ID", required = true)
            @PathVariable Long id) {

        log.info("Disabling review rule: {}", id);

        Optional<ReviewRuleDTO> ruleOpt = reviewRuleApplicationService.disableRule(id);

        if (ruleOpt.isEmpty()) {
            log.warn("Review rule not found: {}", id);
            return ResponseEntity.notFound().build();
        }

        ReviewRuleResponse response = reviewRuleRestConvertor.toResponse(ruleOpt.get());
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    /**
     * 分页搜索审查规则
     * 参考条款搜索接口的实现模式
     */
    @RemotePreAuthorize("@ss.hasRole('admin')")
    @PostMapping("/search")
    @Operation(summary = "搜索审查规则", description = "根据条件分页搜索审查规则")
    public ResponseEntity<ApiResponse<Page<ReviewRuleResponse>>> searchRules(
            @Valid @RequestBody ReviewRuleSearchRequest searchRequest) {

        log.info("Searching review rules with conditions: {}", searchRequest);

        // 1. 搜索请求 -> 查询请求
        ReviewRuleQueryRequest queryRequest = reviewRuleRestConvertor.toQueryRequest(searchRequest);

        // 2. 调用应用服务搜索
        Page<ReviewRuleDTO> rulePage = reviewRuleApplicationService.searchRules(queryRequest);

        // 3. 转换响应
        Page<ReviewRuleResponse> responsePage = rulePage.map(reviewRuleRestConvertor::toResponse);

        return ResponseEntity.ok(ApiResponse.success(responsePage));
    }

    /**
     * 获取所有审查规则
     */
    @RemotePreAuthorize("@ss.hasRole('admin')")
    @GetMapping
    @Operation(summary = "获取所有审查规则", description = "获取所有审查规则列表")
    public ResponseEntity<ApiResponse<List<ReviewRuleResponse>>> getAllRules() {

        log.info("Getting all review rules");

        List<ReviewRuleDTO> rules = reviewRuleApplicationService.getAllRules();
        List<ReviewRuleResponse> responses = rules.stream()
                .map(reviewRuleRestConvertor::toResponse)
                .toList();

        return ResponseEntity.ok(ApiResponse.success(responses));
    }

    /**
     * 根据规则类型查询规则
     */
    @RemotePreAuthorize("@ss.hasRole('admin')")
    @GetMapping("/by-type/{ruleType}")
    @Operation(summary = "按类型查询规则", description = "根据规则类型查询审查规则")
    public ResponseEntity<ApiResponse<List<ReviewRuleResponse>>> getRulesByType(
            @Parameter(description = "规则类型", required = true, example = "specific")
            @PathVariable String ruleType) {

        log.info("Getting review rules by type: {}", ruleType);

        List<ReviewRuleDTO> rules = reviewRuleApplicationService.getRulesByType(ruleType);
        List<ReviewRuleResponse> responses = rules.stream()
                .map(reviewRuleRestConvertor::toResponse)
                .toList();

        return ResponseEntity.ok(ApiResponse.success(responses));
    }

    /**
     * 根据启用状态查询规则
     */
    @RemotePreAuthorize("@ss.hasRole('admin')")
    @GetMapping("/by-enabled/{enabled}")
    @Operation(summary = "按启用状态查询规则", description = "根据启用状态查询审查规则")
    public ResponseEntity<ApiResponse<List<ReviewRuleResponse>>> getRulesByEnabled(
            @Parameter(description = "启用状态", required = true, example = "true")
            @PathVariable Boolean enabled) {

        log.info("Getting review rules by enabled status: {}", enabled);

        List<ReviewRuleDTO> rules = reviewRuleApplicationService.getRulesByEnabled(enabled);
        List<ReviewRuleResponse> responses = rules.stream()
                .map(reviewRuleRestConvertor::toResponse)
                .toList();

        return ResponseEntity.ok(ApiResponse.success(responses));
    }

    /**
     * 检查规则名称是否可用
     */
    @RemotePreAuthorize("@ss.hasRole('admin')")
    @GetMapping("/check-name")
    @Operation(summary = "检查规则名称", description = "检查规则名称是否可用")
    public ResponseEntity<ApiResponse<Boolean>> checkRuleName(
            @Parameter(description = "规则名称", required = true)
            @RequestParam String ruleName) {

        log.info("Checking rule name availability: {}", ruleName);

        boolean available = reviewRuleApplicationService.isRuleNameAvailable(ruleName);

        return ResponseEntity.ok(ApiResponse.success(available));
    }

    /**
     * 统计规则数量
     */
    @RemotePreAuthorize("@ss.hasRole('admin')")
    @GetMapping("/count")
    @Operation(summary = "统计规则数量", description = "统计审查规则数量")
    public ResponseEntity<ApiResponse<Long>> countRules(
            @Parameter(description = "启用状态")
            @RequestParam(required = false) Boolean enabled) {

        log.info("Counting review rules with enabled status: {}", enabled);

        long count = reviewRuleApplicationService.countRules(enabled);

        return ResponseEntity.ok(ApiResponse.success(count));
    }

    /**
     * 根据合同类型和条款类型查询适用的规则
     */
    @RemotePreAuthorize("@ss.hasRole('admin')")
    @GetMapping("/applicable")
    @Operation(summary = "查询适用规则", description = "根据合同类型和条款类型查询适用的审查规则")
    public ResponseEntity<ApiResponse<List<ReviewRuleResponse>>> getApplicableRules(
            @Parameter(description = "合同类型", required = true)
            @RequestParam String contractType,
            @Parameter(description = "条款类型", required = true)
            @RequestParam String clauseType) {

        log.info("Getting applicable rules for contract: {}, clause: {}", contractType, clauseType);

        List<ReviewRuleDTO> rules = reviewRuleApplicationService.getApplicableRules(contractType, clauseType);
        List<ReviewRuleResponse> responses = rules.stream()
                .map(reviewRuleRestConvertor::toResponse)
                .toList();

        return ResponseEntity.ok(ApiResponse.success(responses));
    }

    /**
     * 根据分类列表查询审查规则
     */
    @RemotePreAuthorize("@ss.hasRole('admin')")
    @PostMapping("/by-categories")
    @Operation(summary = "按分类列表查询规则", description = "根据分类列表查询符合条件的审查规则")
    public ResponseEntity<ApiResponse<List<ReviewRuleResponse>>> getRulesByCategories(
            @Parameter(description = "分类列表", required = true)
            @RequestBody List<String> categories) {

        log.info("Getting review rules by categories: {}", categories);

        List<ReviewRuleDTO> rules = reviewRuleApplicationService.getRulesByCategories(categories);
        List<ReviewRuleResponse> responses = rules.stream()
                .map(reviewRuleRestConvertor::toResponse)
                .toList();

        return ResponseEntity.ok(ApiResponse.success(responses));
    }

    /**
     * 获取所有可用的审查规则分类
     */
    @RemotePreAuthorize("@ss.hasAnyRoles('admin,common,guest')")
    @GetMapping("/categories")
    @Operation(summary = "获取所有分类", description = "获取所有可用的审查规则分类列表")
    public ResponseEntity<ApiResponse<List<ReviewTypeDetailResponse>>> getAllCategories() {
        log.info("Getting all available review rule categories");

        List<ReviewTypeDetailResponse> categories = Arrays.stream(ReviewTypeDetail.values())
                .map(type -> {
                    ReviewTypeDetailResponse response = new ReviewTypeDetailResponse();
                    response.setName(type.name());
                    response.setDisplayName(type.getDisplayName());
                    response.setDescription(type.getDescription());
                    return response;
                })
                .toList();

        return ResponseEntity.ok(ApiResponse.success(categories));
    }
}