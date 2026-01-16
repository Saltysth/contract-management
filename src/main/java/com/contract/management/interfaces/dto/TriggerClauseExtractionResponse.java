package com.contract.management.interfaces.dto;

import com.contract.management.domain.model.ClauseExtraction;
import lombok.Data;

/**
 * 触发条款抽取响应DTO
 * 用于REST API
 *
 * @author SaltyFish
 * @since 1.0.0
 */
@Data
public class TriggerClauseExtractionResponse {

    private Long contractId;

    private Long extractionId;

    private String attachmentUuid;

    private String extractionStatus;

    private String errorMessage;

    private Integer extractedClauseNumber;


    public static TriggerClauseExtractionResponse error(Long contractId, String errorMessage) {
        TriggerClauseExtractionResponse response = new TriggerClauseExtractionResponse();
        response.setContractId(contractId);
        response.setExtractionId(null);
        response.setAttachmentUuid(null);
        response.setExtractionStatus("ERROR");
        response.setErrorMessage(errorMessage);
        return response;
    }

    public static TriggerClauseExtractionResponse fromDomain(Long contractId, String attachmentUuid,
        ClauseExtraction extraction) {
        TriggerClauseExtractionResponse response = new TriggerClauseExtractionResponse();
        response.setContractId(contractId);
        response.setExtractionId(extraction.getId().getValue());
        response.setAttachmentUuid(attachmentUuid);
        response.setExtractionStatus(extraction.getStatus().name());
        response.setErrorMessage(extraction.getErrorMessage());
        return response;
    }
}