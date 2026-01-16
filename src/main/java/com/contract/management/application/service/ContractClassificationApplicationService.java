package com.contract.management.application.service;

import com.contract.common.constant.ContractType;
import com.contract.management.application.dto.ContractClassificationDTO;
import com.contract.management.domain.model.ClassificationMethod;
import com.contract.management.domain.model.ClassificationResult;
import com.contract.management.domain.model.Contract;
import com.contract.management.domain.model.ContractId;
import com.contract.management.domain.model.valueobject.ClassificationMetadata;
import com.contract.management.domain.repository.ClassificationRepository;
import com.contract.management.domain.service.ContractClassificationService;
import com.contract.management.domain.service.ContractDomainService;
import com.contract.management.infrastructure.dto.ContractClassificationResult;
import com.contract.management.infrastructure.service.AIModelService;
import com.contract.management.infrastructure.service.FileProcessingService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.CacheManager;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * 合同分类应用服务
 * 负责编排合同分类的完整业务流程
 *
 * @author SaltyFish
 * @since 1.0.0
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class ContractClassificationApplicationService {

    private final ContractClassificationService contractClassificationService;
    private final AIModelService aiModelService;
    private final FileProcessingService fileProcessingService;
    private final ContractDomainService contractDomainService;
    private final ClassificationRepository classificationRepository;
    private final CacheManager cacheManager;

    /**
     * 通过合同ID获取合同分类信息
     * @param contractId 合同ID
     * @return 合同分类DTO
     * @throws IllegalArgumentException 当contractId无效时
     * @throws com.contract.management.domain.exception.ContractNotFoundException 当合同不存在时
     * @throws com.contract.management.domain.exception.FileProcessingException 当文件处理失败时
     */
    @Transactional
    @Cacheable(value = "classification", key = "'contract-classification:' + #contractId", unless = "#result == null || !#result.success")
    public ContractClassificationDTO classifyContract(Long contractId) {
        // 参数验证
        if (contractId == null) {
            throw new IllegalArgumentException("合同ID不能为空");
        }

        Optional<Contract> optionalContract = contractDomainService.findById(ContractId.of(contractId));
        if (optionalContract.isEmpty()) {
            throw new IllegalArgumentException("合同不存在");
        }
        Contract contract = optionalContract.get();

        // 查询分类记录表是否存在对应数据，如果存在就直接返回，不存在就进行分类
        Optional<ClassificationResult> existingResult = classificationRepository.findActiveByContractId(ContractId.of(contractId));
        if (existingResult.isPresent()) {
            log.info("合同 {} 已存在分类结果，直接返回", contractId);
            ClassificationResult result = existingResult.get();

            // 如果合同表中的类型与分类结果不一致，更新合同表
            if (!result.getContractType().equals(contract.getContractType())) {
                contract.setContractType(result.getContractType());
                contractDomainService.updateContract(contract);
                log.debug("同步更新合同 {} 的类型为: {}", contractId, result.getContractType());
            }

            return ContractClassificationDTO.builder()
                .contractId(contractId)
                .contractType(result.getContractType())
                .confidence(result.getConfidenceScore() != null ? result.getConfidenceScore().doubleValue() : null)
                .classificationMethod(result.getClassificationMethod().getCode())
                .fileType(null) // 从历史记录中无法获取文件类型
                .success(true)
                .errorMessage(null)
                .fileProcessingStatus(ContractClassificationDTO.FileProcessingStatus.COMPLETED)
                .build();
        }

        try {
            log.debug("开始应用层合同分类流程: {}", contractId);

            // 1. 调用领域服务进行初步分类
            ContractClassificationService.ContractClassificationResult domainResult =
                contractClassificationService.classifyContract(new ContractId(contractId));

            // 2. 根据文件类型选择不同的AI处理策略
            ContractClassificationResult aiResult;
            String classificationMethod;
            ContractClassificationDTO.FileProcessingStatus processingStatus;

            if ("document".equalsIgnoreCase(domainResult.getFileType()) ||
                "txt".equalsIgnoreCase(domainResult.getFileType())) {

                // 文档类：提取前2000字，调用大模型对话接口
                aiResult = aiModelService.classifyByText(domainResult.getExtractedText());
                classificationMethod = "TEXT_ANALYSIS";
                processingStatus = ContractClassificationDTO.FileProcessingStatus.COMPLETED;

            } else if ("pdf".equalsIgnoreCase(domainResult.getFileType())) {

                // PDF类：提取前两张图片，再调用大模型文件上传接口
                List<byte[]> pdfImages = fileProcessingService.extractImagesFromPDF(domainResult.getFileContent());
                aiResult = aiModelService.classifyByPDFImages(pdfImages);
                classificationMethod = "PDF_IMAGE_ANALYSIS";
                processingStatus = ContractClassificationDTO.FileProcessingStatus.COMPLETED;

            } else if ("image".equalsIgnoreCase(domainResult.getFileType())) {

                // 图片类：直接调用大模型文件上传接口
                aiResult = aiModelService.classifyByFile(domainResult.getFileContent(), domainResult.getFileType());
                classificationMethod = "IMAGE_ANALYSIS";
                processingStatus = ContractClassificationDTO.FileProcessingStatus.COMPLETED;

            } else {
                // 不支持的文件类型
                log.warn("不支持的文件类型: {}", domainResult.getFileType());
                aiResult = ContractClassificationResult.builder()
                    .success(false)
                    .errorMessage("不支持的文件类型: " + domainResult.getFileType())
                    .build();
                classificationMethod = "UNSUPPORTED";
                processingStatus = ContractClassificationDTO.FileProcessingStatus.FAILED;
            }

            // 3. 构建最终返回的DTO
            ContractClassificationDTO.FileProcessingStatus finalStatus =
                aiResult.getSuccess() ? processingStatus : ContractClassificationDTO.FileProcessingStatus.FAILED;

            Map<String, Object> extendedProperties = new HashMap<>();
            if (aiResult.getExtendedProperties() != null) {
                extendedProperties.putAll(aiResult.getExtendedProperties());
            }
            extendedProperties.put("domainFileUuid", domainResult.getFileUuid());
            extendedProperties.put("domainFileType", domainResult.getFileType());

            ContractClassificationDTO result = ContractClassificationDTO.builder()
                .contractId(contractId)
                .contractType(aiResult.getContractType())
                .confidence(aiResult.getConfidence())
                .classificationMethod(classificationMethod)
                .fileType(domainResult.getFileType())
                .success(aiResult.getSuccess())
                .errorMessage(aiResult.getErrorMessage())
                .fileProcessingStatus(finalStatus)
                .build();

            contract.setContractType(aiResult.getContractType());
            contractDomainService.updateContract(contract);

            // 保存分类结果到分类结果表
            try {
                // 先停用旧的分类记录（如果有）
                classificationRepository.deactivateByContractId(ContractId.of(contractId), 1L); // TODO: 获取当前操作用户ID

                // 创建新的分类结果
                ClassificationMetadata metadata = ClassificationMetadata.builder()
                    .reason("AI自动分类")
                    .domainFileUuid(domainResult.getFileUuid())
                    .domainFileType(domainResult.getFileType())
                    .classificationTimestamp(System.currentTimeMillis())
                    .build();

                // 如果有AI原始响应，解析并设置
                if (aiResult.getAiRawResponse() != null && !aiResult.getAiRawResponse().isEmpty()) {
                    try {
                        // aiRawResponse已经是JSON字符串，需要解析为对象
                        ObjectMapper mapper = new ObjectMapper();
                        ClassificationMetadata.AIRawResponse aiResponse = mapper.readValue(
                            aiResult.getAiRawResponse(),
                            ClassificationMetadata.AIRawResponse.class
                        );
                        metadata.setAiRawResponse(aiResponse);
                    } catch (Exception e) {
                        log.warn("解析AI原始响应失败: {}", aiResult.getAiRawResponse(), e);
                    }
                }

                ClassificationResult classificationResult = new ClassificationResult(
                    ContractId.of(contractId),
                    aiResult.getContractType(),
                    ClassificationMethod.AI,
                    aiResult.getConfidence() != null ? java.math.BigDecimal.valueOf(aiResult.getConfidence()) : null,
                    "暂未实现", // TODO: 从AI服务获取模型版本
                    null, // AI分类没有人工操作者
                    "AI自动分类",
                    metadata,
                    1L // TODO: 获取当前操作用户ID
                );

                // 清除缓存，因为分类结果已更新
                clearClassificationCache(contractId);

                classificationRepository.save(classificationResult);
                log.debug("已保存合同 {} 的分类结果到数据库", contractId);
            } catch (Exception e) {
                log.error("保存分类结果失败: {}", contractId, e);
                // 不影响主流程，继续返回结果
            }

            log.debug("合同分类完成: {} -> {} (置信度: {})",
                     contractId, aiResult.getContractType(), aiResult.getConfidence());

            return result;

        } catch (Exception e) {
            log.error("合同分类应用服务执行失败: {}", contractId, e);

            // 即使发生异常，也要返回一个包含错误信息的DTO
            return ContractClassificationDTO.builder()
                .contractId(contractId)
                .success(false)
                .errorMessage(e.getMessage())
                .fileProcessingStatus(ContractClassificationDTO.FileProcessingStatus.FAILED)
                .build();
        }
    }

    /**
     * 清除指定合同的分类缓存
     *
     * @param contractId 合同ID
     */
    private void clearClassificationCache(Long contractId) {
        try {
            var cache = cacheManager.getCache("classification");
            if (cache != null) {
                cache.evict("contract-classification:" + contractId);
                log.debug("已清除合同 {} 的分类缓存", contractId);
            }
        } catch (Exception e) {
            log.warn("清除合同 {} 的分类缓存失败", contractId, e);
        }
    }
}