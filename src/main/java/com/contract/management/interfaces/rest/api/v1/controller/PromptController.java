package com.contract.management.interfaces.rest.api.v1.controller;

import com.contract.management.application.dto.PromptDTO;
import com.contract.management.application.dto.PromptQueryDTO;
import com.contract.management.application.service.PromptApplicationService;
import com.contract.management.interfaces.rest.api.v1.dto.request.CreatePromptRequest;
import com.contract.management.interfaces.rest.api.v1.dto.request.PromptQueryRequest;
import com.contract.management.interfaces.rest.api.v1.dto.request.UpdatePromptRequest;
import com.contract.management.interfaces.rest.api.v1.dto.response.PromptResponse;
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
import java.util.List;
import java.util.stream.Collectors;

/**
 * 提示词模板管理REST控制器
 *
 * @author SaltyFish
 * @since 1.0.0
 */
@Slf4j
@RestController
@RequestMapping("/api/v1/prompts")
@RequiredArgsConstructor
@Validated
@Tag(name = "提示词模板管理", description = "提示词模板CRUD接口")
public class PromptController {

    private final PromptApplicationService promptApplicationService;

    /**
     * 创建提示词模板
     *
     * @param request 创建请求
     * @return 创建的提示词模板信息
     */
    @RemotePreAuthorize("@ss.hasRole('admin')")
    @PostMapping
    @Operation(summary = "创建提示词模板", description = "创建新的提示词模板记录")
    public ResponseEntity<PromptResponse> createPrompt(
            @Valid @RequestBody CreatePromptRequest request) {

        log.info("创建提示词模板请求: {}", request.getPromptName());

        // 手动转换为应用层请求对象
        com.contract.management.application.dto.CreatePromptRequest appRequest =
            new com.contract.management.application.dto.CreatePromptRequest();
        appRequest.setPromptName(request.getPromptName());
        appRequest.setPromptType(request.getPromptType());
        appRequest.setPromptRole(request.getPromptRole());
        appRequest.setPromptContent(request.getPromptContent());
        appRequest.setEnabled(request.getEnabled());
        appRequest.setResponseFormat(request.getResponseFormat());
        appRequest.setRemark(request.getRemark());

        // 调用应用服务
        PromptDTO createdPrompt = promptApplicationService.createPrompt(appRequest);
        PromptResponse response = convertToResponse(createdPrompt);

        log.info("提示词模板创建成功，ID: {}", response.getId());
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    /**
     * 根据ID获取提示词模板详情
     *
     * @param id 提示词模板ID
     * @return 提示词模板详情
     */
    @RemotePreAuthorize("@ss.hasRole('admin')")
    @GetMapping("/{id}")
    @Operation(summary = "获取提示词模板详情", description = "根据ID获取提示词模板详细信息")
    public ResponseEntity<PromptResponse> getPromptById(
            @Parameter(description = "提示词模板ID", required = true)
            @PathVariable("id") @NotNull @Positive Long id) {

        log.info("查询提示词模板详情，ID: {}", id);

        PromptDTO promptDTO = promptApplicationService.getPromptById(id);
        PromptResponse response = convertToResponse(promptDTO);

        return ResponseEntity.ok(response);
    }

    /**
     * 更新提示词模板信息
     *
     * @param id      提示词模板ID
     * @param request 更新请求
     * @return 更新后的提示词模板信息
     */
    @RemotePreAuthorize("@ss.hasRole('admin')")
    @PutMapping("/{id}")
    @Operation(summary = "更新提示词模板", description = "更新指定ID的提示词模板信息")
    public ResponseEntity<PromptResponse> updatePrompt(
            @Parameter(description = "提示词模板ID", required = true)
            @PathVariable("id") @NotNull @Positive Long id,
            @Valid @RequestBody UpdatePromptRequest request) {

        log.info("更新提示词模板请求，ID: {}, 名称: {}", id, request.getPromptName());

        // 手动转换为应用层请求对象
        com.contract.management.application.dto.UpdatePromptRequest appRequest =
            new com.contract.management.application.dto.UpdatePromptRequest();
        appRequest.setPromptName(request.getPromptName());
        appRequest.setPromptContent(request.getPromptContent());
        appRequest.setEnabled(request.getEnabled());
        appRequest.setResponseFormat(request.getResponseFormat());
        appRequest.setRemark(request.getRemark());

        PromptDTO updatedPrompt = promptApplicationService.updatePrompt(id, appRequest);
        PromptResponse response = convertToResponse(updatedPrompt);

        log.info("提示词模板更新成功，ID: {}", id);
        return ResponseEntity.ok(response);
    }

    /**
     * 删除提示词模板
     *
     * @param id 提示词模板ID
     * @return 删除结果
     */
    @RemotePreAuthorize("@ss.hasRole('admin')")
    @DeleteMapping("/{id}")
    @Operation(summary = "删除提示词模板", description = "删除指定ID的提示词模板")
    public ResponseEntity<Boolean> deletePrompt(
            @Parameter(description = "提示词模板ID", required = true)
            @PathVariable("id") @NotNull @Positive Long id) {

        log.info("删除提示词模板请求，ID: {}", id);

        Boolean result = promptApplicationService.deletePrompt(id);

        if (result) {
            log.info("提示词模板删除成功，ID: {}", id);
        } else {
            log.info("提示词模板删除失败，ID: {}", id);
        }

        return ResponseEntity.ok(result);
    }

    /**
     * 启用提示词模板
     *
     * @param id 提示词模板ID
     * @return 启用后的提示词模板信息
     */
    @RemotePreAuthorize("@ss.hasRole('admin')")
    @PutMapping("/{id}/enable")
    @Operation(summary = "启用提示词模板", description = "启用指定ID的提示词模板")
    public ResponseEntity<PromptResponse> enablePrompt(
            @Parameter(description = "提示词模板ID", required = true)
            @PathVariable("id") @NotNull @Positive Long id) {

        log.info("启用提示词模板，ID: {}", id);

        PromptDTO promptDTO = promptApplicationService.enablePrompt(id);
        PromptResponse response = convertToResponse(promptDTO);

        return ResponseEntity.ok(response);
    }

    /**
     * 禁用提示词模板
     *
     * @param id 提示词模板ID
     * @return 禁用后的提示词模板信息
     */
    @RemotePreAuthorize("@ss.hasRole('admin')")
    @PutMapping("/{id}/disable")
    @Operation(summary = "禁用提示词模板", description = "禁用指定ID的提示词模板")
    public ResponseEntity<PromptResponse> disablePrompt(
            @Parameter(description = "提示词模板ID", required = true)
            @PathVariable("id") @NotNull @Positive Long id) {

        log.info("禁用提示词模板，ID: {}", id);

        PromptDTO promptDTO = promptApplicationService.disablePrompt(id);
        PromptResponse response = convertToResponse(promptDTO);

        return ResponseEntity.ok(response);
    }

    /**
     * 分页查询提示词模板
     *
     * @param request 查询条件
     * @param pageNum  页码
     * @param pageSize 每页大小
     * @return 分页查询结果
     */
    @RemotePreAuthorize("@ss.hasRole('admin')")
    @PostMapping("/search")
    @Operation(summary = "分页查询提示词模板", description = "根据条件分页查询提示词模板")
    public ResponseEntity<Page<PromptResponse>> searchPrompts(
            @Valid @RequestBody PromptQueryRequest request,
            @Parameter(description = "页码", example = "1")
            @RequestParam(defaultValue = "1") Integer pageNum,
            @Parameter(description = "每页大小", example = "20")
            @RequestParam(defaultValue = "20") Integer pageSize) {

        log.info("分页查询提示词模板，条件: {}, 页码: {}, 每页: {}", request, pageNum, pageSize);

        // 转换查询请求
        PromptQueryDTO queryDTO = convertToQueryDTO(request);

        // 执行分页查询
        Page<PromptDTO> promptPage = promptApplicationService.findPrompts(queryDTO, pageNum, pageSize);

        // 转换响应结果
        Page<PromptResponse> responsePage = promptPage.map(this::convertToResponse);

        log.info("查询到提示词模板数量: {}", responsePage.getTotalElements());
        return ResponseEntity.ok(responsePage);
    }

    /**
     * 查询提示词模板列表
     *
     * @param request 查询条件
     * @param pageNum  页码
     * @param pageSize 每页大小
     * @return 分页查询结果
     */
    @RemotePreAuthorize("@ss.hasRole('admin')")
    @GetMapping("/list")
    @Operation(summary = "查询提示词模板列表", description = "根据条件分页查询提示词模板列表")
    public ResponseEntity<Page<PromptResponse>> queryPrompts(
            @Parameter(description = "查询条件")
            @ModelAttribute PromptQueryRequest request,
            @Parameter(description = "页码", example = "1")
            @RequestParam(defaultValue = "1") Integer pageNum,
            @Parameter(description = "每页大小", example = "20")
            @RequestParam(defaultValue = "20") Integer pageSize) {

        log.info("分页查询提示词模板列表，条件: {}, 页码: {}, 每页: {}", request, pageNum, pageSize);

        // 转换查询请求
        PromptQueryDTO queryDTO = convertToQueryDTO(request);

        // 执行分页查询
        Page<PromptDTO> promptPage = promptApplicationService.findPrompts(queryDTO, pageNum, pageSize);

        // 转换响应结果
        Page<PromptResponse> responsePage = promptPage.map(this::convertToResponse);

        log.info("查询到提示词模板数量: {}", responsePage.getTotalElements());
        return ResponseEntity.ok(responsePage);
    }

    /**
     * 查询所有启用的提示词模板
     *
     * @param pageNum  页码
     * @param pageSize 每页大小
     * @return 分页查询结果
     */
    @RemotePreAuthorize("@ss.hasRole('admin')")
    @GetMapping("/enabled")
    @Operation(summary = "查询所有启用的提示词模板", description = "分页获取所有启用的提示词模板列表")
    public ResponseEntity<Page<PromptResponse>> findEnabledPrompts(
            @Parameter(description = "页码", example = "1")
            @RequestParam(defaultValue = "1") Integer pageNum,
            @Parameter(description = "每页大小", example = "20")
            @RequestParam(defaultValue = "20") Integer pageSize) {

        log.info("分页查询所有启用的提示词模板，页码: {}, 每页: {}", pageNum, pageSize);

        // 创建查询条件，只查询启用的提示词模板
        PromptQueryDTO queryDTO = new PromptQueryDTO();
        queryDTO.setEnabled(true);

        // 执行分页查询
        Page<PromptDTO> promptPage = promptApplicationService.findPrompts(queryDTO, pageNum, pageSize);

        // 转换响应结果
        Page<PromptResponse> responsePage = promptPage.map(this::convertToResponse);

        log.info("查询到启用的提示词模板数量: {}", responsePage.getTotalElements());
        return ResponseEntity.ok(responsePage);
    }

    /**
     * 根据类型查询启用的提示词模板
     *
     * @param type     提示词模板类型
     * @param pageNum  页码
     * @param pageSize 每页大小
     * @return 分页查询结果
     */
    @RemotePreAuthorize("@ss.hasRole('admin')")
    @GetMapping("/enabled/{type}")
    @Operation(summary = "根据类型查询启用的提示词模板", description = "根据指定类型分页查询启用的提示词模板")
    public ResponseEntity<Page<PromptResponse>> findEnabledPromptsByType(
            @Parameter(description = "提示词模板类型", required = true)
            @PathVariable("type") String type,
            @Parameter(description = "页码", example = "1")
            @RequestParam(defaultValue = "1") Integer pageNum,
            @Parameter(description = "每页大小", example = "20")
            @RequestParam(defaultValue = "20") Integer pageSize) {

        log.info("根据类型分页查询启用的提示词模板，类型: {}, 页码: {}, 每页: {}", type, pageNum, pageSize);

        try {
            com.contract.management.domain.model.valueobject.PromptType promptType =
                com.contract.management.domain.model.valueobject.PromptType.fromCode(type);

            // 创建查询条件，只查询指定类型的启用提示词模板
            PromptQueryDTO queryDTO = new PromptQueryDTO();
            queryDTO.setEnabled(true);
            queryDTO.setPromptTypes(List.of(promptType));

            // 执行分页查询
            Page<PromptDTO> promptPage = promptApplicationService.findPrompts(queryDTO, pageNum, pageSize);

            // 转换响应结果
            Page<PromptResponse> responsePage = promptPage.map(this::convertToResponse);

            log.info("查询到启用的提示词模板数量: {}", responsePage.getTotalElements());
            return ResponseEntity.ok(responsePage);
        } catch (Exception e) {
            log.error("无效的提示词模板类型: {}", type, e);
            return ResponseEntity.badRequest().build();
        }
    }

    // ==================== 私有转换方法 ====================

    /**
     * 应用DTO转换为响应DTO
     */
    private PromptResponse convertToResponse(PromptDTO promptDTO) {
        PromptResponse response = new PromptResponse();
        response.setId(promptDTO.getId());
        response.setPromptName(promptDTO.getPromptName());
        response.setPromptType(promptDTO.getPromptType());
        response.setPromptRole(promptDTO.getPromptRole());
        response.setPromptContent(promptDTO.getPromptContent());
        response.setEnabled(promptDTO.getEnabled());
        response.setResponseFormat(promptDTO.getResponseFormat());
        response.setRemark(promptDTO.getRemark());
        response.setCreatedBy(promptDTO.getCreatedBy());
        response.setUpdatedBy(promptDTO.getUpdatedBy());
        response.setCreatedTime(promptDTO.getCreatedTime());
        response.setUpdatedTime(promptDTO.getUpdatedTime());
        response.setObjectVersionNumber(promptDTO.getObjectVersionNumber());
        return response;
    }

    /**
     * REST查询请求转换为应用查询DTO
     */
    private PromptQueryDTO convertToQueryDTO(PromptQueryRequest request) {
        PromptQueryDTO queryDTO = new PromptQueryDTO();
        queryDTO.setPromptName(request.getPromptName());
        queryDTO.setPromptTypes(request.getPromptTypes());
        queryDTO.setPromptRoles(request.getPromptRoles());
        queryDTO.setEnabled(request.getEnabled());
        queryDTO.setResponseFormat(request.getResponseFormat());
        queryDTO.setCreatedBy(request.getCreatedBy());
        queryDTO.setCreatedTimeStart(request.getCreatedTimeStart());
        queryDTO.setCreatedTimeEnd(request.getCreatedTimeEnd());
        queryDTO.setUpdatedTimeStart(request.getUpdatedTimeStart());
        queryDTO.setUpdatedTimeEnd(request.getUpdatedTimeEnd());
        queryDTO.setRemark(request.getRemark());
        queryDTO.setKeyword(request.getKeyword());
        return queryDTO;
    }
}