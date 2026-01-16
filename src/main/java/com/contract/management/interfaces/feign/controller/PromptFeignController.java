package com.contract.management.interfaces.feign.controller;

import com.contract.common.feign.dto.PromptFeignDTO;
import com.contract.common.feign.dto.PromptPageResultFeignDTO;
import com.contract.common.feign.dto.PromptQueryFeignDTO;
import com.contract.management.application.dto.PromptDTO;
import com.contract.management.application.dto.PromptQueryDTO;
import com.contract.management.application.service.PromptApplicationService;
import com.contract.management.interfaces.rest.api.v1.dto.request.PromptQueryRequest;
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
 * 提示词Feign控制器
 * 提供与PromptController完全一致的接口，供其他微服务调用
 */
@Slf4j
@RestController
@RequestMapping("/feign/v1/prompts")
@RequiredArgsConstructor
@Validated
@Tag(name = "提示词Feign接口", description = "提示词模板管理Feign接口，与PromptController接口完全一致")
public class PromptFeignController {

    private final PromptApplicationService promptApplicationService;

    /**
     * 根据ID获取提示词模板详情
     * 与PromptController.getPromptById接口完全一致
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

        log.info("Feign调用：查询提示词模板详情，ID: {}", id);

        PromptDTO promptDTO = promptApplicationService.getPromptById(id);
        PromptResponse response = convertToResponse(promptDTO);

        return ResponseEntity.ok(response);
    }

    /**
     * 启用提示词模板
     * 与PromptController.enablePrompt接口完全一致
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

        log.info("Feign调用：启用提示词模板，ID: {}", id);

        PromptDTO promptDTO = promptApplicationService.enablePrompt(id);
        PromptResponse response = convertToResponse(promptDTO);

        return ResponseEntity.ok(response);
    }

    /**
     * 禁用提示词模板
     * 与PromptController.disablePrompt接口完全一致
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

        log.info("Feign调用：禁用提示词模板，ID: {}", id);

        PromptDTO promptDTO = promptApplicationService.disablePrompt(id);
        PromptResponse response = convertToResponse(promptDTO);

        return ResponseEntity.ok(response);
    }

    /**
     * 分页查询提示词模板
     * 与PromptFeignClient.searchPrompts接口完全一致
     * 使用PromptQueryFeignDTO参数并返回PromptPageResultFeignDTO
     *
     * @param queryDTO 查询条件
     * @return 分页查询结果
     */
    @RemotePreAuthorize("@ss.hasRole('admin')")
    @PostMapping("/search")
    @Operation(summary = "分页查询提示词模板", description = "根据条件分页查询提示词模板")
    public PromptPageResultFeignDTO searchPrompts(
            @Valid @RequestBody PromptQueryFeignDTO queryDTO) {

        log.info("Feign调用：分页查询提示词模板，条件: {}", queryDTO);

        // 转换查询请求：从FeignDTO到应用层DTO
        PromptQueryDTO applicationQueryDTO = convertFeignDTOToApplicationDTO(queryDTO);

        // 执行分页查询
        Page<PromptDTO> promptPage = promptApplicationService.findPrompts(
                applicationQueryDTO,
                queryDTO.getPageNum() != null ? queryDTO.getPageNum() : 1,
                queryDTO.getPageSize() != null ? queryDTO.getPageSize() : 20);

        // 转换响应结果为FeignDTO
        List<PromptFeignDTO> promptFeignDTOs = promptPage.getContent().stream()
                .map(this::convertToFeignDTO)
                .collect(java.util.stream.Collectors.toList());

        // 构建分页结果
        PromptPageResultFeignDTO result = PromptPageResultFeignDTO.Builder.of(
                promptFeignDTOs,
                promptPage.getTotalElements(),
                promptPage.getNumber() + 1, // Spring的Page从0开始，Feign从1开始
                promptPage.getSize()
        );

        log.info("Feign调用查询到提示词模板数量: {}", result.getTotal());
        return result;
    }

    /**
     * 查询提示词模板列表
     * 与PromptController.queryPrompts接口完全一致
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

        log.info("Feign调用：分页查询提示词模板列表，条件: {}, 页码: {}, 每页: {}", request, pageNum, pageSize);

        // 转换查询请求
        PromptQueryDTO queryDTO = convertToQueryDTO(request);

        // 执行分页查询
        Page<PromptDTO> promptPage = promptApplicationService.findPrompts(queryDTO, pageNum, pageSize);

        // 转换响应结果
        Page<PromptResponse> responsePage = promptPage.map(this::convertToResponse);

        log.info("Feign调用查询到提示词模板数量: {}", responsePage.getTotalElements());
        return ResponseEntity.ok(responsePage);
    }

    /**
     * 查询所有启用的提示词模板
     * 与PromptController.findEnabledPrompts接口完全一致
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

        log.info("Feign调用：分页查询所有启用的提示词模板，页码: {}, 每页: {}", pageNum, pageSize);

        // 创建查询条件，只查询启用的提示词模板
        PromptQueryDTO queryDTO = new PromptQueryDTO();
        queryDTO.setEnabled(true);

        // 执行分页查询
        Page<PromptDTO> promptPage = promptApplicationService.findPrompts(queryDTO, pageNum, pageSize);

        // 转换响应结果
        Page<PromptResponse> responsePage = promptPage.map(this::convertToResponse);

        log.info("Feign调用查询到启用的提示词模板数量: {}", responsePage.getTotalElements());
        return ResponseEntity.ok(responsePage);
    }

    /**
     * 根据类型查询启用的提示词模板
     * 与PromptController.findEnabledPromptsByType接口完全一致
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

        log.info("Feign调用：根据类型分页查询启用的提示词模板，类型: {}, 页码: {}, 每页: {}", type, pageNum, pageSize);

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

            log.info("Feign调用查询到启用的提示词模板数量: {}", responsePage.getTotalElements());
            return ResponseEntity.ok(responsePage);
        } catch (Exception e) {
            log.error("Feign调用无效的提示词模板类型: {}", type, e);
            return ResponseEntity.badRequest().build();
        }
    }

    // ==================== 私有转换方法 ====================

    /**
     * 应用DTO转换为响应DTO（与PromptController完全一致）
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
     * REST查询请求转换为应用查询DTO（与PromptController完全一致）
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

    /**
     * 将Feign查询DTO转换为应用层查询DTO
     *
     * @param feignDTO Feign查询DTO
     * @return 应用层查询DTO
     */
    private PromptQueryDTO convertFeignDTOToApplicationDTO(PromptQueryFeignDTO feignDTO) {
        if (feignDTO == null) {
            return new PromptQueryDTO();
        }

        PromptQueryDTO queryDTO = new PromptQueryDTO();
        queryDTO.setPromptName(feignDTO.getPromptName());

        // 转换promptTypeList
        if (feignDTO.getPromptTypeList() != null) {
            queryDTO.setPromptTypes(feignDTO.getPromptTypeList().stream()
                    .map(type -> com.contract.management.domain.model.valueobject.PromptType.valueOf(type))
                    .collect(java.util.stream.Collectors.toList()));
        }

        // 转换promptRoleList
        if (feignDTO.getPromptRoleList() != null) {
            queryDTO.setPromptRoles(feignDTO.getPromptRoleList().stream()
                    .map(role -> com.contract.management.domain.model.valueobject.PromptRole.valueOf(role))
                    .collect(java.util.stream.Collectors.toList()));
        }

        queryDTO.setEnabled(feignDTO.getEnabled());
        queryDTO.setResponseFormat(null); // FeignDTO中没有此字段
        queryDTO.setCreatedBy(feignDTO.getCreatedByList() != null && !feignDTO.getCreatedByList().isEmpty()
                ? feignDTO.getCreatedByList().get(0) : null);
        queryDTO.setCreatedTimeStart(feignDTO.getCreatedTimeStart());
        queryDTO.setCreatedTimeEnd(feignDTO.getCreatedTimeEnd());
        queryDTO.setUpdatedTimeStart(feignDTO.getUpdatedTimeStart());
        queryDTO.setUpdatedTimeEnd(feignDTO.getUpdatedTimeEnd());
        queryDTO.setRemark(null); // FeignDTO中没有此字段
        queryDTO.setKeyword(feignDTO.getKeyword());

        return queryDTO;
    }

    /**
     * 将应用层DTO转换为FeignDTO
     *
     * @param promptDTO 应用层DTO
     * @return FeignDTO
     */
    private PromptFeignDTO convertToFeignDTO(PromptDTO promptDTO) {
        if (promptDTO == null) {
            return null;
        }

        return PromptFeignDTO.builder()
                .id(promptDTO.getId())
                .promptName(promptDTO.getPromptName())
                .promptType(promptDTO.getPromptType() != null ? promptDTO.getPromptType().name() : null)
                .promptRole(promptDTO.getPromptRole() != null ? promptDTO.getPromptRole().name() : null)
                .promptContent(promptDTO.getPromptContent())
                .responseFormat(promptDTO.getResponseFormat())
                .enabled(promptDTO.getEnabled())
                .remark(promptDTO.getRemark())
                .createdBy(promptDTO.getCreatedBy())
                .updatedBy(promptDTO.getUpdatedBy())
                .createdTime(promptDTO.getCreatedTime())
                .updatedTime(promptDTO.getUpdatedTime())
                .build();
    }
}