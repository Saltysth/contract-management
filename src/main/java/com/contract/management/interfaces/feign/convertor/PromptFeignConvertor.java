package com.contract.management.interfaces.feign.convertor;

import com.contract.common.feign.dto.PromptFeignDTO;
import com.contract.common.feign.dto.PromptPageResultFeignDTO;
import com.contract.common.feign.dto.PromptQueryFeignDTO;
import com.contract.management.application.dto.PromptDTO;
import com.contract.management.application.dto.PromptQueryDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * 提示词Feign转换器
 * 用于应用层DTO与Feign DTO之间的转换
 */
@Slf4j
public class PromptFeignConvertor {

    /**
     * 应用层DTO转Feign DTO
     *
     * @param promptDTO 应用层提示词DTO
     * @return Feign提示词DTO
     */
    public static PromptFeignDTO convertToFeignDTO(PromptDTO promptDTO) {
        if (promptDTO == null) {
            return null;
        }

        PromptFeignDTO feignDTO = new PromptFeignDTO();
        feignDTO.setId(promptDTO.getId());
        feignDTO.setPromptName(promptDTO.getPromptName());
        feignDTO.setPromptType(promptDTO.getPromptType() != null ? promptDTO.getPromptType().name() : null);
        feignDTO.setPromptRole(promptDTO.getPromptRole() != null ? promptDTO.getPromptRole().name() : null);
        feignDTO.setPromptContent(promptDTO.getPromptContent());
        feignDTO.setEnabled(promptDTO.getEnabled());
        feignDTO.setResponseFormat(promptDTO.getResponseFormat());
        feignDTO.setRemark(promptDTO.getRemark());
        feignDTO.setCreatedBy(promptDTO.getCreatedBy());
        feignDTO.setUpdatedBy(promptDTO.getUpdatedBy());
        feignDTO.setCreatedTime(promptDTO.getCreatedTime());
        feignDTO.setUpdatedTime(promptDTO.getUpdatedTime());
        feignDTO.setObjectVersionNumber(promptDTO.getObjectVersionNumber());

        log.debug("应用层DTO转FeignDTO完成，提示词ID: {}, 名称: {}, 类型: {}",
            feignDTO.getId(), feignDTO.getPromptName(), feignDTO.getPromptType());

        return feignDTO;
    }

    /**
     * Feign查询DTO转应用查询DTO
     *
     * @param feignQuery Feign查询DTO
     * @return 应用查询DTO
     */
    public static PromptQueryDTO convertToApplicationQuery(PromptQueryFeignDTO feignQuery) {
        if (feignQuery == null) {
            return new PromptQueryDTO();
        }

        PromptQueryDTO query = new PromptQueryDTO();
        query.setPromptName(feignQuery.getPromptName());

        // 处理类型列表转换（暂时跳过枚举转换，只处理基本查询）
        // PromptType和PromptRole枚举转换需要映射器支持，这里暂时不处理

        // 处理角色列表转换（暂时跳过枚举转换）
        // PromptRole枚举转换需要映射器支持，这里暂时不处理

        query.setEnabled(feignQuery.getEnabled());
        query.setKeyword(feignQuery.getKeyword());

        // 处理时间范围
        query.setCreatedTimeStart(feignQuery.getCreatedTimeStart());
        query.setCreatedTimeEnd(feignQuery.getCreatedTimeEnd());
        query.setUpdatedTimeStart(feignQuery.getUpdatedTimeStart());
        query.setUpdatedTimeEnd(feignQuery.getUpdatedTimeEnd());

        // 处理创建者列表
        if (feignQuery.getCreatedByList() != null && !feignQuery.getCreatedByList().isEmpty()) {
            // 应用层可能只支持单个创建者查询，取第一个
            query.setCreatedBy(feignQuery.getCreatedByList().get(0));
        }

        // 处理启用状态查询
        if (Boolean.TRUE.equals(feignQuery.getOnlyEnabled())) {
            query.setEnabled(true);
        }

        log.debug("Feign查询DTO转应用查询DTO完成，查询条件: {}", query);

        return query;
    }

    /**
     * 应用分页结果转Feign分页结果
     *
     * @param pageResult 应用层分页结果
     * @return Feign分页结果
     */
    public static PromptPageResultFeignDTO convertToFeignPageResult(Page<PromptDTO> pageResult) {
        if (pageResult == null) {
            return PromptPageResultFeignDTO.Builder.empty();
        }

        log.debug("转换提示词分页结果，总记录数: {}, 当前页: {}, 每页大小: {}",
            pageResult.getTotalElements(), pageResult.getNumber() + 1, pageResult.getSize());

        // 数据列表转换
        List<PromptFeignDTO> records = new ArrayList<>();
        pageResult.getContent().forEach(promptDTO -> {
            PromptFeignDTO feignDTO = convertToFeignDTO(promptDTO);
            if (feignDTO != null) {
                records.add(feignDTO);
            }
        });

        // 构建分页结果（注意页码转换：Spring Data从0开始，Feign从1开始）
        PromptPageResultFeignDTO result = PromptPageResultFeignDTO.Builder.of(
            records,
            pageResult.getTotalElements(),
            pageResult.getNumber() + 1,
            pageResult.getSize()
        );

        log.debug("提示词分页结果转换完成，返回记录数: {}, 总页数: {}",
            records.size(), result.getPages());

        return result;
    }

    /**
     * 应用DTO列表转Feign DTO列表
     *
     * @param promptDTOS 应用层DTO列表
     * @return Feign DTO列表
     */
    public static List<PromptFeignDTO> convertToFeignList(List<PromptDTO> promptDTOS) {
        if (promptDTOS == null || promptDTOS.isEmpty()) {
            return new ArrayList<>();
        }

        List<PromptFeignDTO> feignDTOS = new ArrayList<>();
        promptDTOS.forEach(promptDTO -> {
            PromptFeignDTO feignDTO = convertToFeignDTO(promptDTO);
            if (feignDTO != null) {
                feignDTOS.add(feignDTO);
            }
        });

        log.debug("应用DTO列表转Feign DTO列表完成，转换记录数: {}", feignDTOS.size());

        return feignDTOS;
    }

    /**
     * 按类型过滤启用的提示词
     *
     * @param promptDTOS 应用层DTO列表
     * @param type       提示词类型
     * @return 指定类型的启用提示词列表
     */
    public static List<PromptFeignDTO> filterEnabledPromptsByType(List<PromptDTO> promptDTOS, String type) {
        if (promptDTOS == null || promptDTOS.isEmpty()) {
            return new ArrayList<>();
        }

        List<PromptFeignDTO> filteredPrompts = new ArrayList<>();
        promptDTOS.forEach(promptDTO -> {
            if (Boolean.TRUE.equals(promptDTO.getEnabled()) &&
                promptDTO.getPromptType() != null &&
                promptDTO.getPromptType().name().equals(type)) {

                PromptFeignDTO feignDTO = convertToFeignDTO(promptDTO);
                if (feignDTO != null) {
                    filteredPrompts.add(feignDTO);
                }
            }
        });

        log.debug("按类型过滤启用提示词完成，类型: {}, 过滤后记录数: {}", type, filteredPrompts.size());

        return filteredPrompts;
    }

    /**
     * 过滤启用的提示词
     *
     * @param promptDTOS 应用层DTO列表
     * @return 启用的提示词列表
     */
    public static List<PromptFeignDTO> filterEnabledPrompts(List<PromptDTO> promptDTOS) {
        if (promptDTOS == null || promptDTOS.isEmpty()) {
            return new ArrayList<>();
        }

        List<PromptFeignDTO> enabledPrompts = new ArrayList<>();
        promptDTOS.forEach(promptDTO -> {
            if (Boolean.TRUE.equals(promptDTO.getEnabled())) {
                PromptFeignDTO feignDTO = convertToFeignDTO(promptDTO);
                if (feignDTO != null) {
                    enabledPrompts.add(feignDTO);
                }
            }
        });

        log.debug("过滤启用提示词完成，启用记录数: {}", enabledPrompts.size());

        return enabledPrompts;
    }
}