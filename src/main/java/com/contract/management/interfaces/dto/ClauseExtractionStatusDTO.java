package com.contract.management.interfaces.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.Map;

/**
 * 条款抽取状态DTO
 *
 * @author SaltyFish
 * @since 1.0.0
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ClauseExtractionStatusDTO {

    /**
     * 抽取状态
     */
    private String status;

    /**
     * 状态描述
     */
    private String statusDescription;

    /**
     * 错误信息（失败时有值）
     */
    private String errorMessage;

    /**
     * 抽取结果（完成时有值）
     */
    private ClauseExtractionResultDTO result;

    /**
     * 抽取结果DTO
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class ClauseExtractionResultDTO {

        /**
         * 合同主体信息
         */
        private ContractPartiesDTO contractParties;

        /**
         * 合同期限信息
         */
        private ContractTermsDTO contractTerms;

        /**
         * 合同金额信息
         */
        private ContractAmountDTO contractAmount;

        /**
         * 关键条款数量
         */
        private Integer keyClausesCount;

        /**
         * 风险条款数量
         */
        private Integer riskClausesCount;

        /**
         * 抽取置信度 (0-1)
         */
        private Double confidenceScore;

        /**
         * 使用的AI模型
         */
        private String aiModel;

        /**
         * 抽取时间
         */
        @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
        private LocalDateTime extractionTime;

        /**
         * 其他重要信息
         */
        private Map<String, Object> additionalInfo;
    }

    /**
     * 合同主体信息DTO
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class ContractPartiesDTO {
        private String partyA;
        private String partyB;
        private String partyALegalRepresentative;
        private String partyBLegalRepresentative;
        private String partyAAddress;
        private String partyBAddress;
        private String partyAContact;
        private String partyBContact;
    }

    /**
     * 合同期限信息DTO
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class ContractTermsDTO {
        @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
        private LocalDateTime signDate;

        @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
        private LocalDateTime effectiveDate;

        @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
        private LocalDateTime expiryDate;

        private String contractPeriod;
        private Boolean autoRenewal;
        private Integer renewalNoticeDays;
    }

    /**
     * 合同金额信息DTO
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class ContractAmountDTO {
        private Double totalAmount;
        private String currency;
        private String paymentMethod;
        private String amountDescription;
    }
}