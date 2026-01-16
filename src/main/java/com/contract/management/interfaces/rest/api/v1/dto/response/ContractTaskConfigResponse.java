package com.contract.management.interfaces.rest.api.v1.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * 合同任务配置信息响应
 *
 * @author SaltyFish
 * @since 1.0.0
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ContractTaskConfigResponse {

    /**
     * 合同任务ID
     */
    private Long contractTaskId;

    /**
     * 业务标签
     */
    private String businessTags;

    /**
     * 审查类型
     */
    private String reviewType;

    /**
     * 自定义选择的审查类型
     */
    private List<String> customSelectedReviewTypes;

    /**
     * 行业
     */
    private String industry;

    /**
     * 币种
     */
    private String currency;

    /**
     * 合同类型
     */
    private String contractType;

    /**
     * 类型置信度
     */
    private String typeConfidence;

    /**
     * 审查规则
     */
    private String reviewRules;

    /**
     * 提示词模板
     */
    private PromptTemplateInfo promptTemplate;

    /**
     * 是否启用术语库
     */
    private Boolean enableTerminology;

    /**
     * 术语库信息
     */
    private TerminologyInfo terminology;

    /**
     * 规则信息
     */
    private RuleInfo rule;

    /**
     * 总耗时(分钟)
     */
    private Integer totalTimeConsuming;

    /**
     * 提示词模板信息
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class PromptTemplateInfo {
        /**
         * 模板名称
         */
        private String promptName;

        /**
         * 模板类型
         */
        private String promptType;

        /**
         * 模板角色
         */
        private String promptRole;

        /**
         * 模板内容
         */
        private String promptContent;

        /**
         * 响应格式
         */
        private String responseFormat;

        /**
         * 耗时(分钟)
         */
        private Integer timeConsuming;
    }

    /**
     * 术语库信息
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class TerminologyInfo {
        /**
         * 是否存在相关配置
         */
        private Boolean hasConfig;

        /**
         * 配置内容
         */
        private String configContent;

        /**
         * 耗时(分钟)
         */
        private Integer timeConsuming;
    }

    /**
     * 规则信息
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class RuleInfo {
        /**
         * 规则条数
         */
        private Integer ruleCount;

        /**
         * 耗时(分钟) - 每条规则1分钟
         */
        private Integer timeConsuming;
    }
}