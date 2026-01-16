package com.contract.management.interfaces.feign.convertor;

import com.contract.common.feign.dto.ReviewRuleFeignDTO;
import com.contract.common.feign.dto.ReviewRulePageResultFeignDTO;
import com.contract.common.feign.dto.ReviewRuleQueryFeignDTO;
import com.contract.management.application.dto.ReviewRuleDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 审查规则Feign转换器
 * 用于应用层DTO与Feign DTO之间的转换
 */
@Slf4j
public class ReviewRuleFeignConvertor {

    /**
     * 应用层DTO转Feign DTO
     *
     * @param reviewRuleDTO 应用层审查规则DTO
     * @return Feign审查规则DTO
     */
    public static ReviewRuleFeignDTO convertToFeignDTO(ReviewRuleDTO reviewRuleDTO) {
        if (reviewRuleDTO == null) {
            return null;
        }

        ReviewRuleFeignDTO feignDTO = new ReviewRuleFeignDTO();
        feignDTO.setId(reviewRuleDTO.getId());
        feignDTO.setRuleName(reviewRuleDTO.getRuleName());
        feignDTO.setRuleType(reviewRuleDTO.getRuleType());
        feignDTO.setRuleTypeDescription(reviewRuleDTO.getRuleTypeDescription());
        feignDTO.setApplicableContractTypes(reviewRuleDTO.getApplicableContractType());
        feignDTO.setApplicableClauseTypes(reviewRuleDTO.getApplicableClauseType());
        feignDTO.setRuleContent(reviewRuleDTO.getRuleContent());
        feignDTO.setPromptModeCode(reviewRuleDTO.getPromptModeCode());
        feignDTO.setPromptModeDescription(reviewRuleDTO.getPromptModeDescription());
        feignDTO.setEnabled(reviewRuleDTO.getEnabled());
        feignDTO.setCreateTime(reviewRuleDTO.getCreateTime());
        feignDTO.setUpdateTime(reviewRuleDTO.getUpdateTime());
        feignDTO.setRemark(reviewRuleDTO.getRemark());
        // ReviewRuleDTO没有objectVersionNumber字段，暂时注释
        // feignDTO.setObjectVersionNumber(reviewRuleDTO.getObjectVersionNumber());

        log.debug("应用层DTO转FeignDTO完成，审查规则ID: {}, 名称: {}, 类型: {}",
            feignDTO.getId(), feignDTO.getRuleName(), feignDTO.getRuleType());

        return feignDTO;
    }

    
    /**
     * 应用分页结果转Feign分页结果
     *
     * @param pageResult 应用层分页结果
     * @return Feign分页结果
     */
    public static ReviewRulePageResultFeignDTO convertToFeignPageResult(Page<ReviewRuleDTO> pageResult) {
        if (pageResult == null) {
            return ReviewRulePageResultFeignDTO.Builder.empty();
        }

        log.debug("转换审查规则分页结果，总记录数: {}, 当前页: {}, 每页大小: {}",
            pageResult.getTotalElements(), pageResult.getNumber() + 1, pageResult.getSize());

        // 数据列表转换
        List<ReviewRuleFeignDTO> records = new ArrayList<>();
        pageResult.getContent().forEach(reviewRuleDTO -> {
            ReviewRuleFeignDTO feignDTO = convertToFeignDTO(reviewRuleDTO);
            if (feignDTO != null) {
                records.add(feignDTO);
            }
        });

        // 构建分页结果（注意页码转换：Spring Data从0开始，Feign从1开始）
        ReviewRulePageResultFeignDTO result = ReviewRulePageResultFeignDTO.Builder.of(
            records,
            pageResult.getTotalElements(),
            pageResult.getNumber() + 1,
            pageResult.getSize()
        );

        log.debug("审查规则分页结果转换完成，返回记录数: {}, 总页数: {}",
            records.size(), result.getPages());

        return result;
    }

    /**
     * 应用DTO列表转Feign DTO列表
     *
     * @param reviewRuleDTOS 应用层DTO列表
     * @return Feign DTO列表
     */
    public static List<ReviewRuleFeignDTO> convertToFeignList(List<ReviewRuleDTO> reviewRuleDTOS) {
        if (reviewRuleDTOS == null || reviewRuleDTOS.isEmpty()) {
            return new ArrayList<>();
        }

        List<ReviewRuleFeignDTO> feignDTOS = new ArrayList<>();
        reviewRuleDTOS.forEach(reviewRuleDTO -> {
            ReviewRuleFeignDTO feignDTO = convertToFeignDTO(reviewRuleDTO);
            if (feignDTO != null) {
                feignDTOS.add(feignDTO);
            }
        });

        log.debug("应用DTO列表转Feign DTO列表完成，转换记录数: {}", feignDTOS.size());

        return feignDTOS;
    }

    /**
     * 过滤启用的审查规则
     *
     * @param reviewRuleDTOS 应用层DTO列表
     * @return 启用的审查规则列表
     */
    public static List<ReviewRuleFeignDTO> filterEnabledReviewRules(List<ReviewRuleDTO> reviewRuleDTOS) {
        if (reviewRuleDTOS == null || reviewRuleDTOS.isEmpty()) {
            return new ArrayList<>();
        }

        List<ReviewRuleFeignDTO> enabledRules = new ArrayList<>();
        reviewRuleDTOS.forEach(reviewRuleDTO -> {
            if (Boolean.TRUE.equals(reviewRuleDTO.getEnabled())) {
                ReviewRuleFeignDTO feignDTO = convertToFeignDTO(reviewRuleDTO);
                if (feignDTO != null) {
                    enabledRules.add(feignDTO);
                }
            }
        });

        log.debug("过滤启用审查规则完成，启用记录数: {}", enabledRules.size());

        return enabledRules;
    }

    /**
     * 过滤适用的审查规则
     *
     * @param reviewRuleDTOS 应用层DTO列表
     * @param contractType   合同类型
     * @param clauseType     条款类型
     * @return 适用的审查规则列表
     */
    public static List<ReviewRuleFeignDTO> filterApplicableReviewRules(
            List<ReviewRuleDTO> reviewRuleDTOS, String contractType, String clauseType) {

        if (reviewRuleDTOS == null || reviewRuleDTOS.isEmpty()) {
            return new ArrayList<>();
        }

        List<ReviewRuleFeignDTO> applicableRules = new ArrayList<>();
        reviewRuleDTOS.forEach(reviewRuleDTO -> {
            if (Boolean.TRUE.equals(reviewRuleDTO.getEnabled()) && isRuleApplicable(reviewRuleDTO, contractType, clauseType)) {
                ReviewRuleFeignDTO feignDTO = convertToFeignDTO(reviewRuleDTO);
                if (feignDTO != null) {
                    applicableRules.add(feignDTO);
                }
            }
        });

        log.debug("过滤适用审查规则完成，合同类型: {}, 条款类型: {}, 适用记录数: {}",
            contractType, clauseType, applicableRules.size());

        return applicableRules;
    }

    /**
     * 判断规则是否适用于指定合同类型和条款类型
     */
    private static boolean isRuleApplicable(ReviewRuleDTO rule, String contractType, String clauseType) {
        // 检查合同类型适用性
        boolean contractTypeMatch = rule.getApplicableContractType() == null ||
                rule.getApplicableContractType().isEmpty() ||
                rule.getApplicableContractType().contains("ALL") ||
                rule.getApplicableContractType().contains(contractType);

        // 检查条款类型适用性
        boolean clauseTypeMatch = rule.getApplicableClauseType() == null ||
                rule.getApplicableClauseType().isEmpty() ||
                rule.getApplicableClauseType().contains("ALL") ||
                (clauseType != null && rule.getApplicableClauseType().contains(clauseType));

        return contractTypeMatch && clauseTypeMatch;
    }
}