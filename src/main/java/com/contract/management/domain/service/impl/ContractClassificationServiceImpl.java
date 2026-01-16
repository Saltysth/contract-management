package com.contract.management.domain.service.impl;

import com.contract.management.domain.exception.ContractNotFoundException;
import com.contract.management.domain.exception.FileProcessingException;
import com.contract.management.domain.model.Contract;
import com.contract.management.domain.model.ContractId;
import com.contract.management.domain.repository.ContractRepository;
import com.contract.management.domain.service.ContractClassificationService;
import com.contract.management.infrastructure.service.FileProcessingService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * 合同分类领域服务实现
 *
 * @author SaltyFish
 * @since 1.0.0
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class ContractClassificationServiceImpl implements ContractClassificationService {

    private final ContractRepository contractRepository;
    private final FileProcessingService fileProcessingService;

    @Override
    public ContractClassificationResult classifyContract(ContractId contractId) {
        // 参数验证
        if (contractId == null || contractId.getValue() == null) {
            throw new IllegalArgumentException("合同ID不能为空");
        }

        try {
            log.debug("开始分类合同: {}", contractId.getValue());

            // 1. 获取合同信息
            Contract contract = contractRepository.findById(contractId)
                .orElseThrow(() -> new ContractNotFoundException("合同不存在: " + contractId.getValue()));

            // 2. 检查合同是否有附件
            if (contract.getAttachmentUuid() == null || contract.getAttachmentUuid().trim().isEmpty()) {
                throw new FileProcessingException("合同没有附件文件: " + contractId.getValue());
            }

            String fileUuid = contract.getAttachmentUuid();
            log.debug("合同附件UUID: {}", fileUuid);

            // 3. 处理文件内容
            FileProcessingService.FileContentInfo fileContentInfo = fileProcessingService.getFileContent(fileUuid);

            String fileType = fileContentInfo.getFileType();
            String extractedText = fileContentInfo.getExtractedText();
            byte[] fileContent = fileContentInfo.getContent();

            log.debug("文件处理完成 - 类型: {}, 提取文本长度: {}",
                     fileType, extractedText != null ? extractedText.length() : 0);

            // 4. 返回分类结果
            return new ContractClassificationResult(
                contractId,
                fileUuid,
                fileType,
                fileContent,
                extractedText
            );

        } catch (ContractNotFoundException e) {
            log.warn("合同不存在: {}", contractId.getValue());
            throw e;
        } catch (FileProcessingException e) {
            log.error("文件处理失败: {}", contractId.getValue(), e);
            throw e;
        } catch (Exception e) {
            log.error("合同分类过程中发生未知错误: {}", contractId.getValue(), e);
            throw new FileProcessingException("合同分类失败: " + e.getMessage(), e);
        }
    }
}