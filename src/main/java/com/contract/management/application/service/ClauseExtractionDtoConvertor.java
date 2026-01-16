package com.contract.management.application.service;

import com.contract.management.domain.model.ClauseExtraction;
import com.contract.management.domain.model.valueobject.ClauseExtractionResult;
import com.contract.management.interfaces.dto.ClauseExtractionStatusDTO;
import org.springframework.stereotype.Component;

/**
 * 条款抽取DTO转换器
 *
 * @author SaltyFish
 * @since 1.0.0
 */
@Component
public class ClauseExtractionDtoConvertor {

    /**
     * 领域模型转状态DTO
     */
    public ClauseExtractionStatusDTO toStatusDTO(ClauseExtraction domain) {
        if (domain == null) {
            return null;
        }

        return ClauseExtractionStatusDTO.builder()
            .status(domain.getStatus().name())
            .statusDescription(domain.getStatus().getDescription())
            .errorMessage(domain.getErrorMessage())
            .result(toResultDTO(domain.getResult()))
            .build();
    }

    /**
     * 抽取结果转DTO
     */
    private ClauseExtractionStatusDTO.ClauseExtractionResultDTO toResultDTO(ClauseExtractionResult result) {
        if (result == null) {
            return null;
        }

        return ClauseExtractionStatusDTO.ClauseExtractionResultDTO.builder()
            .contractParties(toContractPartiesDTO(result.getContractParties()))
            .contractTerms(toContractTermsDTO(result.getContractTerms()))
            .contractAmount(toContractAmountDTO(result.getContractAmount()))
            .keyClausesCount(result.getKeyClauses() != null ? result.getKeyClauses().size() : 0)
            .riskClausesCount(result.getRiskClauses() != null ? result.getRiskClauses().size() : 0)
            .confidenceScore(result.getConfidenceScore())
            .aiModel(result.getAiModel())
            .extractionTime(result.getExtractionTime())
            .additionalInfo(result.getAdditionalInfo())
            .build();
    }

    /**
     * 合同主体信息转DTO
     */
    private ClauseExtractionStatusDTO.ContractPartiesDTO toContractPartiesDTO(
            ClauseExtractionResult.ContractParties parties) {
        if (parties == null) {
            return null;
        }

        return ClauseExtractionStatusDTO.ContractPartiesDTO.builder()
            .partyA(parties.getPartyA())
            .partyB(parties.getPartyB())
            .partyALegalRepresentative(parties.getPartyALegalRepresentative())
            .partyBLegalRepresentative(parties.getPartyBLegalRepresentative())
            .partyAAddress(parties.getPartyAAddress())
            .partyBAddress(parties.getPartyBAddress())
            .partyAContact(parties.getPartyAContact())
            .partyBContact(parties.getPartyBContact())
            .build();
    }

    /**
     * 合同期限信息转DTO
     */
    private ClauseExtractionStatusDTO.ContractTermsDTO toContractTermsDTO(
            ClauseExtractionResult.ContractTerms terms) {
        if (terms == null) {
            return null;
        }

        return ClauseExtractionStatusDTO.ContractTermsDTO.builder()
            .signDate(terms.getSignDate())
            .effectiveDate(terms.getEffectiveDate())
            .expiryDate(terms.getExpiryDate())
            .contractPeriod(terms.getContractPeriod())
            .autoRenewal(terms.getAutoRenewal())
            .renewalNoticeDays(terms.getRenewalNoticeDays())
            .build();
    }

    /**
     * 合同金额信息转DTO
     */
    private ClauseExtractionStatusDTO.ContractAmountDTO toContractAmountDTO(
            ClauseExtractionResult.ContractAmount amount) {
        if (amount == null) {
            return null;
        }

        return ClauseExtractionStatusDTO.ContractAmountDTO.builder()
            .totalAmount(amount.getTotalAmount())
            .currency(amount.getCurrency())
            .paymentMethod(amount.getPaymentMethod())
            .amountDescription(amount.getAmountDescription())
            .build();
    }
}