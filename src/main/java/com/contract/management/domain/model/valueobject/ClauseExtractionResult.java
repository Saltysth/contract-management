package com.contract.management.domain.model.valueobject;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

/**
 * 条款抽取结果值对象
 *
 * @author SaltyFish
 * @since 1.0.0
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class ClauseExtractionResult {

    /**
     * 合同主体信息
     */
    private ContractParties contractParties;

    /**
     * 合同期限信息
     */
    private ContractTerms contractTerms;

    /**
     * 合同金额信息
     */
    private ContractAmount contractAmount;

    /**
     * 关键条款列表
     */
    private List<KeyClause> keyClauses;

    /**
     * 风险条款列表
     */
    private List<RiskClause> riskClauses;

    /**
     * 其他重要信息
     */
    private Map<String, Object> additionalInfo;

    /**
     * 抽取置信度 (0-1)
     */
    private Double confidenceScore;

    /**
     * 模型处理耗时（毫秒）
     */
    private Long processingTimeMs;

    /**
     * 使用的AI模型
     */
    private String aiModel;

    /**
     * 抽取时间
     */
    private LocalDateTime extractionTime;

    /**
     * 合同主体信息
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ContractParties {
        private String partyA; // 甲方
        private String partyB; // 乙方
        private String partyALegalRepresentative; // 甲方法定代表人
        private String partyBLegalRepresentative; // 乙方法定代表人
        private String partyAAddress; // 甲方地址
        private String partyBAddress; // 乙方地址
        private String partyAContact; // 甲方联系方式
        private String partyBContact; // 乙方联系方式
    }

    /**
     * 合同期限信息
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ContractTerms {
        private LocalDateTime signDate; // 签订日期
        private LocalDateTime effectiveDate; // 生效日期
        private LocalDateTime expiryDate; // 到期日期
        private String contractPeriod; // 合同期限描述
        private Boolean autoRenewal; // 是否自动续约
        private Integer renewalNoticeDays; // 续约通知天数
    }

    /**
     * 合同金额信息
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ContractAmount {
        private Double totalAmount; // 总金额
        private String currency; // 币种
        private String paymentMethod; // 支付方式
        private List<PaymentSchedule> paymentSchedules; // 付款计划
        private String amountDescription; // 金额描述
    }

    /**
     * 付款计划
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class PaymentSchedule {
        private String phase; // 阶段
        private Double amount; // 金额
        private LocalDateTime dueDate; // 到期日
        private String condition; // 条件
    }

    /**
     * 关键条款
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class KeyClause {
        private String clauseType; // 条款类型
        private String clauseContent; // 条款内容
        private String location; // 在合同中的位置
        private Double importance; // 重要性评分 (0-1)
        private List<String> keywords; // 关键词
    }

    /**
     * 风险条款
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class RiskClause {
        private String riskType; // 风险类型
        private String riskLevel; // 风险等级 (高/中/低)
        private String clauseContent; // 条款内容
        private String riskDescription; // 风险描述
        private List<String> suggestions; // 建议措施
        private Double riskScore; // 风险评分 (0-1)
    }
}