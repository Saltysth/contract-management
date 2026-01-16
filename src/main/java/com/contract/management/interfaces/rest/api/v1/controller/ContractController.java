package com.contract.management.interfaces.rest.api.v1.controller;

import com.contract.common.constant.ContractType;
import com.contract.management.application.dto.ContractDTO;
import com.contract.management.application.dto.ContractQueryDTO;
import com.contract.management.application.service.ContractApplicationService;
import com.contract.management.interfaces.rest.api.v1.convertor.ContractRestConvertor;
import com.contract.management.interfaces.rest.api.v1.dto.request.ContractQueryRequest;
import com.contract.management.interfaces.rest.api.v1.dto.request.CreateContractRequest;
import com.contract.management.interfaces.rest.api.v1.dto.request.UpdateContractRequest;
import com.contract.management.interfaces.rest.api.v1.dto.response.ContractResponse;
import com.ruoyi.common.annotation.Anonymous;
import com.ruoyi.feign.annotation.RemotePreAuthorize;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 合同管理REST控制器
 *
 * @author SaltyFish
 * @since 1.0.0
 */
@Slf4j
@RestController
@RequestMapping("/api/v1/contracts")
@RequiredArgsConstructor
@Validated
@Tag(name = "合同管理", description = "合同CRUD接口")
public class ContractController {
    
    private final ContractApplicationService contractApplicationService;
    private final ContractRestConvertor contractRestConvertor;
    
    /**
     * 创建合同
     *
     * @param request 创建请求
     * @return 创建的合同信息
     */
    @RemotePreAuthorize("@ss.hasAnyRoles('admin,common')")
    @PostMapping
    @Operation(summary = "创建合同", description = "创建新的合同记录")
    public ResponseEntity<ContractResponse> createContract(
            @Valid @RequestBody CreateContractRequest request) {
        
        log.info("创建合同请求: {}", request.getContractName());
        
        ContractDTO contractDTO = contractRestConvertor.toApplicationDTO(request);
        ContractDTO createdContract = contractApplicationService.saveOrUpdateContract(contractDTO);
        ContractResponse response = contractRestConvertor.toResponse(createdContract);
        
        log.info("合同创建成功，ID: {}", response.getId());
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }


    
    /**
     * 根据ID获取合同详情
     *
     * @param id 合同ID
     * @return 合同详情
     */
    @RemotePreAuthorize("@ss.hasAnyRoles('admin,common,guest')")
    @GetMapping("/{id}")
    @Operation(summary = "获取合同详情", description = "根据ID获取合同详细信息")
    public ResponseEntity<ContractResponse> getContractById(
            @Parameter(description = "合同ID", required = true)
            @PathVariable("id") @NotNull @Positive Long id) {
        
        log.info("查询合同详情，ID: {}", id);
        
        ContractDTO contractDTO = contractApplicationService.getContractById(id);
        ContractResponse response = contractRestConvertor.toResponse(contractDTO);
        
        return ResponseEntity.ok(response);
    }
    
    /**
     * 更新合同信息
     *
     * @param id      合同ID
     * @param request 更新请求
     * @return 更新后的合同信息
     */
    @RemotePreAuthorize("@ss.hasAnyRoles('admin,common')")
    @PutMapping("/{id}")
    @Operation(summary = "更新合同", description = "更新指定ID的合同信息")
    public ResponseEntity<ContractResponse> updateContract(
            @Parameter(description = "合同ID", required = true)
            @PathVariable("id") @NotNull @Positive Long id,
            @Valid @RequestBody UpdateContractRequest request) {
        
        log.info("更新合同请求，ID: {}, 名称: {}", id, request.getContractName());
        
        ContractDTO contractDTO = contractRestConvertor.toApplicationDTO(request);
        contractDTO.setId(id);
        
        ContractDTO updatedContract = contractApplicationService.updateContract(contractDTO);
        ContractResponse response = contractRestConvertor.toResponse(updatedContract);
        
        log.info("合同更新成功，ID: {}", id);
        return ResponseEntity.ok(response);
    }
    
    /**
     * 删除合同
     *
     * @param id 合同ID
     * @return 删除结果
     */
    @RemotePreAuthorize("@ss.hasAnyRoles('admin,common')")
    @DeleteMapping("/{id}")
    @Operation(summary = "删除合同", description = "删除指定ID的合同")
    public ResponseEntity<Boolean> deleteContract(
            @Parameter(description = "合同ID", required = true)
            @PathVariable("id") @NotNull @Positive Long id) {
        
        log.info("删除合同请求，ID: {}", id);
        
        Boolean res = contractApplicationService.deleteContract(id);

        if (res) {
            log.info("合同删除成功，ID: {}", id);
        } else {
            log.info("合同删除失败，ID: {}", id);
        }
        return ResponseEntity.ok(res);
    }
    
    /**
     * 查询合同列表
     *
     * @param request 查询条件
     * @return 合同列表
     */
    @RemotePreAuthorize("@ss.hasAnyRoles('admin,common,guest')")
    @GetMapping("/list")
    @Operation(summary = "查询合同列表", description = "根据条件查询合同列表")
    public ResponseEntity<List<ContractResponse>> queryContracts(
            @Parameter(description = "查询条件")
            @ModelAttribute ContractQueryRequest request) {
        
        log.info("查询合同列表，条件: {}", request);
        
        ContractQueryDTO queryDTO = contractRestConvertor.toApplicationQueryDTO(request);
        List<ContractDTO> contracts = contractApplicationService.queryContracts(queryDTO);
        List<ContractResponse> responses = contractRestConvertor.toResponseList(contracts);
        
        log.info("查询到合同数量: {}", responses.size());
        return ResponseEntity.ok(responses);
    }

    /**
     * 获取合同类型列表
     *
     * @return 合同类型列表
     */
    @Anonymous
    @GetMapping("/contract-types")
    @Operation(summary = "获取合同类型列表", description = "获取所有支持的合同类型中文名称")
    public ResponseEntity<List<String>> getContractTypes() {

        log.info("获取合同类型列表");

        List<String> contractTypes = Arrays.stream(ContractType.values())
                .map(ContractType::getDisplayName)
                .collect(Collectors.toList());

        log.info("获取到合同类型数量: {}", contractTypes.size());
        return ResponseEntity.ok(contractTypes);
    }
}