package com.contract.management.interfaces.feign.controller;

import static com.contract.management.interfaces.feign.convertor.ClauseFeignConvertor.convertToApplicationQuery;
import static com.contract.management.interfaces.feign.convertor.ClauseFeignConvertor.convertToFeignDTO;
import static com.contract.management.interfaces.feign.convertor.ClauseFeignConvertor.convertToFeignPageResult;
import static com.contract.management.interfaces.feign.convertor.ClauseFeignConvertor.convertToFeignDTOList;

import com.contract.common.feign.dto.ClauseFeignDTO;
import com.contract.common.feign.dto.ClausePageResultFeignDTO;
import com.contract.common.feign.dto.ClauseQueryFeignDTO;
import com.contract.management.application.dto.ClauseDTO;
import com.contract.management.application.dto.ClauseQueryDTO;
import com.contract.management.application.service.ClauseApplicationService;
import com.ruoyi.feign.annotation.RemotePreAuthorize;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * 条款Feign控制器
 * 处理来自其他微服务的条款查询请求
 * 只包含查询接口，不包含写操作
 *
 * @author SaltyFish
 * @since 1.0.0
 */
@Slf4j
@RestController
@RequestMapping("/feign/v1/clauses")
@RequiredArgsConstructor
public class ClauseFeignController {

    private final ClauseApplicationService clauseApplicationService;

    /**
     * 根据ID获取条款详情
     *
     * @param id 条款ID
     * @return 条款详情
     */
    @RemotePreAuthorize("@ss.hasAnyRoles('admin,common,guest')")
    @GetMapping("/{id}")
    @Operation(summary = "根据ID获取条款详情", description = "通过Feign接口获取指定ID的条款详细信息")
    public ClauseFeignDTO getClauseById(
            @Parameter(description = "条款ID", required = true)
            @PathVariable("id") @NotNull @Positive Long id) {

        log.debug("Feign调用：查询条款详情，ID: {}", id);

        return clauseApplicationService.findById(id)
                .map(clauseDTO -> {
                    log.debug("条款查询成功，ID: {}", id);
                    return convertToFeignDTO(clauseDTO);
                })
                .orElse(null);
    }

    /**
     * 根据抽取任务ID查询条款列表
     *
     * @param extractionTaskId 抽取任务ID
     * @return 条款列表
     */
    @RemotePreAuthorize("@ss.hasAnyRoles('admin,common,guest')")
    @GetMapping("/extraction-task/{extractionTaskId}")
    @Operation(summary = "根据抽取任务ID查询条款", description = "通过Feign接口查询指定抽取任务ID的所有条款")
    public List<ClauseFeignDTO> getClausesByExtractionTaskId(
            @Parameter(description = "抽取任务ID", required = true)
            @PathVariable("extractionTaskId") @NotNull @Positive Long extractionTaskId) {

        log.debug("Feign调用：根据抽取任务ID查询条款，extractionTaskId: {}", extractionTaskId);

        List<ClauseDTO> clauses = clauseApplicationService.findByExtractionTaskId(extractionTaskId);
        List<ClauseFeignDTO> responses = convertToFeignDTOList(clauses);

        log.debug("根据抽取任务ID查询条款成功，extractionTaskId: {}, 数量: {}", extractionTaskId, responses.size());
        return responses;
    }

    /**
     * 根据合同ID查询条款列表
     *
     * @param contractId 合同ID
     * @return 条款列表
     */
    @RemotePreAuthorize("@ss.hasAnyRoles('admin,common,guest')")
    @GetMapping("/contract/{contractId}")
    @Operation(summary = "根据合同ID查询条款", description = "通过Feign接口查询指定合同ID的所有条款")
    public List<ClauseFeignDTO> getClausesByContractId(
            @Parameter(description = "合同ID", required = true)
            @PathVariable("contractId") @NotNull @Positive Long contractId) {

        log.debug("Feign调用：根据合同ID查询条款，contractId: {}", contractId);

        List<ClauseDTO> clauses = clauseApplicationService.findByContractId(contractId);
        List<ClauseFeignDTO> responses = convertToFeignDTOList(clauses);

        log.debug("根据合同ID查询条款成功，contractId: {}, 数量: {}", contractId, responses.size());
        return responses;
    }

    /**
     * 根据条件分页查询条款
     *
     * @param queryDTO 查询条件
     * @return 分页查询结果
     */
    @RemotePreAuthorize("@ss.hasAnyRoles('admin,common,guest')")
    @PostMapping("/search")
    @Operation(summary = "分页查询条款", description = "通过Feign接口根据条件分页查询条款")
    public ClausePageResultFeignDTO searchClauses(@RequestBody ClauseQueryFeignDTO queryDTO) {
        log.debug("Feign调用：分页查询条款，条件: {}", queryDTO);

        // 转换查询DTO
        ClauseQueryDTO query = convertToApplicationQuery(queryDTO);

        // 执行查询
        Page<ClauseDTO> pageResult = clauseApplicationService.findByFilters(query);

        // 转换结果
        ClausePageResultFeignDTO result = convertToFeignPageResult(pageResult);

        log.debug("分页查询条款成功，总数: {}", result.getTotal());
        return result;
    }
}