package com.contract.management.domain.service;

import com.contract.management.domain.model.ContractId;

/**
 * 合同分类领域服务接口
 * 定义合同分类的核心业务逻辑
 *
 * @author SaltyFish
 * @since 1.0.0
 */
public interface ContractClassificationService {

    /**
     * 通过合同ID获取合同分类信息
     *
     * @param contractId 合同ID
     * @return 合同分类结果
     * @throws IllegalArgumentException 当contractId无效时
     */
    ContractClassificationResult classifyContract(ContractId contractId);

    /**
     * 合同分类结果值对象
     */
    class ContractClassificationResult {
        private final ContractId contractId;
        private final String fileUuid;
        private final String fileType;
        private final byte[] fileContent;
        private final String extractedText;

        public ContractClassificationResult(ContractId contractId, String fileUuid,
                                          String fileType, byte[] fileContent, String extractedText) {
            this.contractId = contractId;
            this.fileUuid = fileUuid;
            this.fileType = fileType;
            this.fileContent = fileContent;
            this.extractedText = extractedText;
        }

        public ContractId getContractId() {
            return contractId;
        }

        public String getFileUuid() {
            return fileUuid;
        }

        public String getFileType() {
            return fileType;
        }

        public byte[] getFileContent() {
            return fileContent;
        }

        public String getExtractedText() {
            return extractedText;
        }
    }
}