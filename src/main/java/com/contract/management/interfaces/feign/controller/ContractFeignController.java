package com.contract.management.interfaces.feign.controller;

import static com.contract.management.interfaces.feign.convertor.ContractFeignConvertor.convertToApplicationQuery;
import static com.contract.management.interfaces.feign.convertor.ContractFeignConvertor.convertToFeignDTO;
import static com.contract.management.interfaces.feign.convertor.ContractFeignConvertor.convertToFeignPageResult;
import static com.contract.management.interfaces.feign.convertor.ContractFeignConvertor.convertToUpdateRequest;

import com.contract.common.feign.dto.ContractFeignDTO;
import com.contract.common.feign.dto.ContractPageResultFeignDTO;
import com.contract.common.feign.dto.ContractQueryFeignDTO;
import com.contract.common.feign.dto.UpdateContractFeignRequest;
import com.contract.management.application.dto.ContractDTO;
import com.contract.management.application.dto.ContractQueryDTO;
import com.contract.management.application.service.ContractApplicationService;
import com.contract.management.interfaces.rest.api.v1.convertor.ContractRestConvertor;
import com.contract.management.interfaces.rest.api.v1.dto.request.CreateContractRequest;
import com.contract.management.interfaces.rest.api.v1.dto.response.ContractResponse;
import com.ruoyi.feign.annotation.RemotePreAuthorize;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

/**
 * 合同Feign控制器
 * 处理来自其他微服务的合同查询请求
 * 只包含查询接口，不包含写操作
 *
 * @author SaltyFish
 * @since 1.0.0
 */
@Slf4j
@RestController
@RequestMapping("/feign/v1/contracts")
@RequiredArgsConstructor
public class ContractFeignController {

    private final ContractApplicationService contractApplicationService;

    /**
     * 创建合同
     *
     * @param uuid 文件uuid
     * @return 创建的合同信息
     */
    @RemotePreAuthorize("@ss.hasAnyRoles('admin,common')")
    @PostMapping("/{uuid}")
    @Operation(summary = "创建合同", description = "创建新的合同记录")
    public ContractFeignDTO createContract(@PathVariable("uuid") String uuid) {
        CreateContractRequest request = new CreateContractRequest();
        request.setAttachmentUuid(uuid);
        log.info("创建合同请求: {}", request.getContractName());

        ContractDTO contractDTO = ContractRestConvertor.INSTANCE.toApplicationDTO(request);
        ContractDTO createdContract = contractApplicationService.saveOrUpdateContract(contractDTO);
        ContractResponse response = ContractRestConvertor.INSTANCE.toResponse(createdContract);

        log.info("合同创建成功，ID: {}", response.getId());
        return convertToFeignDTO(createdContract);
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
    public ContractFeignDTO updateContract(
        @Parameter(description = "合同ID", required = true)
        @PathVariable("id") @NotNull @Positive Long id,
        @Valid @RequestBody UpdateContractFeignRequest request) {

        log.info("更新合同请求，ID: {}, 名称: {}", id, request.getContractName());

        ContractDTO contractDTO = ContractRestConvertor.INSTANCE.toApplicationDTO(convertToUpdateRequest(request));
        contractDTO.setId(id);

        ContractDTO updatedContract = contractApplicationService.updateContract(contractDTO);
        ContractResponse response = ContractRestConvertor.INSTANCE.toResponse(updatedContract);

        log.info("合同更新成功，ID: {}", id);
        return convertToFeignDTO(updatedContract);
    }




    /**
     * 根据ID查询合同详情
     *
     * @param id 合同ID
     * @return 合同详情DTO
     */
    @RemotePreAuthorize("@ss.hasAnyRoles('admin,common,guest')")
    @GetMapping("/{id}")
    public ContractFeignDTO getContractById(@PathVariable("id") Long id) {
        log.debug("Feign调用：查询合同详情，ID: {}", id);

        ContractDTO contractDTO = contractApplicationService.getContractById(id);
        return convertToFeignDTO(contractDTO);
    }

    /**
     * 根据条件查询合同列表
     *
     * @param queryDTO 查询条件
     * @return 分页查询结果
     */
    @PostMapping("/search")
    public ContractPageResultFeignDTO searchContracts(@RequestBody ContractQueryFeignDTO queryDTO) {
        log.debug("Feign调用：查询合同列表，条件: {}", queryDTO);

        // 转换查询DTO
        ContractQueryDTO query = convertToApplicationQuery(queryDTO);

        // 执行查询 - 使用ContractApplicationService的分页查询方法
        int pageNum = queryDTO.getPageNum() != null ? queryDTO.getPageNum() : 1;
        int pageSize = queryDTO.getPageSize() != null ? queryDTO.getPageSize() : 20;

        Page<ContractDTO> pageResult = contractApplicationService.findContracts(query, pageNum, pageSize);

        // 转换结果
        return convertToFeignPageResult(pageResult);
    }

    @RemotePreAuthorize("@ss.hasAnyRoles('admin,common')")
    @DeleteMapping("/{id}")
    @Operation(summary = "删除合同", description = "删除指定ID的合同")
    public Boolean deleteContract(
        @Parameter(description = "合同ID", required = true)
        @PathVariable("id") @NotNull @Positive Long id) {

        log.info("删除合同请求，ID: {}", id);

        Boolean res = contractApplicationService.deleteContract(id);

        if (res) {
            log.info("合同删除成功，ID: {}", id);
        } else {
            log.info("合同删除失败，ID: {}", id);
        }
        return res;
    }
}