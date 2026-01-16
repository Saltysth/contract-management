package com.contract.management.application.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.contract.management.domain.exception.ContractNotFoundException;
import com.contract.management.infrastructure.entity.ContractTaskEntity;
import com.contract.management.infrastructure.entity.PromptEntity;
import com.contract.management.infrastructure.entity.ReviewRuleEntity;
import com.contract.management.infrastructure.mapper.ContractTaskMapper;
import com.contract.management.infrastructure.mapper.PromptMapper;
import com.contract.management.infrastructure.mapper.ReviewRuleMapper;
import com.contract.management.interfaces.rest.api.v1.dto.response.ContractTaskConfigResponse;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;

/**
 * 合同任务配置服务
 *
 * @author SaltyFish
 * @since 1.0.0
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class ContractTaskConfigService {

    private final ContractTaskMapper contractTaskMapper;
    private final PromptMapper promptMapper;
    private final ReviewRuleMapper reviewRuleMapper;
    private final ObjectMapper objectMapper;

    /**
     * 获取合同任务配置信息和耗时
     *
     * @param contractTaskId 合同任务ID
     * @return 配置信息和耗时
     */
    public ContractTaskConfigResponse getContractTaskConfig(Long contractTaskId) {
        log.info("查询合同任务配置信息, contractTaskId: {}", contractTaskId);

        // 1. 查询合同任务信息
        ContractTaskEntity contractTask = contractTaskMapper.selectById(contractTaskId);
        if (contractTask == null) {
            throw new ContractNotFoundException("合同任务不存在: " + contractTaskId);
        }

        // 2. 查询提示词模板信息
        ContractTaskConfigResponse.PromptTemplateInfo promptTemplateInfo = null;
        if (contractTask.getPromptTemplate() != null) {
            promptTemplateInfo = getPromptTemplateInfo(contractTask.getPromptTemplate());
        }

        // 3. 查询术语库配置信息
        ContractTaskConfigResponse.TerminologyInfo terminologyInfo = getTerminologyInfo(contractTask);

        // 4. 查询规则配置信息
        ContractTaskConfigResponse.RuleInfo ruleInfo = getRuleInfo(contractTask);

        // 5. 计算总耗时
        Integer totalTimeConsuming = calculateTotalTimeConsuming(promptTemplateInfo, terminologyInfo, ruleInfo);

        // 6. 解析自定义审查类型
        List<String> customSelectedReviewTypes = parseCustomSelectedReviewTypes(contractTask.getCustomSelectedReviewTypes());

        // 7. 构建响应
        return ContractTaskConfigResponse.builder()
                .contractTaskId(contractTaskId)
                .businessTags(contractTask.getBusinessTags())
                .reviewType(contractTask.getReviewType())
                .customSelectedReviewTypes(customSelectedReviewTypes)
                .industry(contractTask.getIndustry())
                .currency(contractTask.getCurrency())
                .contractType(contractTask.getContractType())
                .typeConfidence(contractTask.getTypeConfidence() != null ?
                    contractTask.getTypeConfidence().toString() : null)
                .reviewRules(contractTask.getReviewRules())
                .promptTemplate(promptTemplateInfo)
                .enableTerminology(contractTask.getEnableTerminology())
                .terminology(terminologyInfo)
                .rule(ruleInfo)
                .totalTimeConsuming(totalTimeConsuming)
                .build();
    }

    /**
     * 获取提示词模板信息
     */
    private ContractTaskConfigResponse.PromptTemplateInfo getPromptTemplateInfo(String promptTemplate) {
        try {
            LambdaQueryWrapper<PromptEntity> queryWrapper = new LambdaQueryWrapper<>();
            queryWrapper.eq(PromptEntity::getPromptName, promptTemplate)
                       .eq(PromptEntity::getEnabled, true)
                       .last("LIMIT 1");

            PromptEntity prompt = promptMapper.selectOne(queryWrapper);
            if (prompt == null) {
                log.warn("未找到启用的提示词模板: {}", promptTemplate);
                return null;
            }

            // 提示词模板标准耗时设为3分钟
            return ContractTaskConfigResponse.PromptTemplateInfo.builder()
                    .promptName(prompt.getPromptName())
                    .promptType(prompt.getPromptType())
                    .promptRole(prompt.getPromptRole())
                    .promptContent(prompt.getPromptContent())
                    .responseFormat(prompt.getResponseFormat())
                    .timeConsuming(3) // 标准耗时3分钟
                    .build();
        } catch (Exception e) {
            log.error("查询提示词模板信息失败: {}", promptTemplate, e);
            return null;
        }
    }

    /**
     * 获取术语库配置信息
     */
    private ContractTaskConfigResponse.TerminologyInfo getTerminologyInfo(ContractTaskEntity contractTask) {
        if (!Boolean.TRUE.equals(contractTask.getEnableTerminology())) {
            return ContractTaskConfigResponse.TerminologyInfo.builder()
                    .hasConfig(false)
                    .configContent("术语库未启用")
                    .timeConsuming(0)
                    .build();
        }

        // 这里可以根据实际需求查询术语库配置
        // 目前模拟不存在相关配置的情况
        return ContractTaskConfigResponse.TerminologyInfo.builder()
                .hasConfig(false)
                .configContent("术语库不存在相关配置")
                .timeConsuming(2) // 术语库标准耗时2分钟
                .build();
    }

    /**
     * 获取规则配置信息
     */
    private ContractTaskConfigResponse.RuleInfo getRuleInfo(ContractTaskEntity contractTask) {
        try {
            LambdaQueryWrapper<ReviewRuleEntity> queryWrapper = new LambdaQueryWrapper<>();
            queryWrapper.eq(ReviewRuleEntity::getEnabled, true);

            // 如果有指定审查规则，按规则查询
            if (contractTask.getReviewRules() != null && !contractTask.getReviewRules().trim().isEmpty()) {
                // 这里可以根据reviewRules字段的具体格式进行查询优化
                // 目前简单查询所有启用的规则
            }

            List<ReviewRuleEntity> rules = reviewRuleMapper.selectList(queryWrapper);
            int ruleCount = rules != null ? rules.size() : 0;

            return ContractTaskConfigResponse.RuleInfo.builder()
                    .ruleCount(ruleCount)
                    .timeConsuming(ruleCount) // 每条规则1分钟
                    .build();
        } catch (Exception e) {
            log.error("查询审查规则信息失败", e);
            return ContractTaskConfigResponse.RuleInfo.builder()
                    .ruleCount(0)
                    .timeConsuming(0)
                    .build();
        }
    }

    /**
     * 计算总耗时
     */
    private Integer calculateTotalTimeConsuming(
            ContractTaskConfigResponse.PromptTemplateInfo promptTemplateInfo,
            ContractTaskConfigResponse.TerminologyInfo terminologyInfo,
            ContractTaskConfigResponse.RuleInfo ruleInfo) {

        int totalTime = 0;

        if (promptTemplateInfo != null) {
            totalTime += promptTemplateInfo.getTimeConsuming();
        }

        if (terminologyInfo != null) {
            totalTime += terminologyInfo.getTimeConsuming();
        }

        if (ruleInfo != null) {
            totalTime += ruleInfo.getTimeConsuming();
        }

        return totalTime;
    }

    /**
     * 解析自定义审查类型
     */
    private List<String> parseCustomSelectedReviewTypes(String customSelectedReviewTypes) {
        if (customSelectedReviewTypes == null || customSelectedReviewTypes.trim().isEmpty()) {
            return Collections.emptyList();
        }

        try {
            return objectMapper.readValue(customSelectedReviewTypes,
                new TypeReference<List<String>>() {});
        } catch (Exception e) {
            log.error("解析自定义审查类型失败: {}", customSelectedReviewTypes, e);
            return Collections.singletonList(customSelectedReviewTypes);
        }
    }
}