package com.contract.management.infrastructure.service;

import com.contract.ai.feign.client.AiClient;
import com.contract.ai.feign.dto.ChatRequest;
import com.contract.ai.feign.enums.ModelType;
import com.contract.ai.feign.enums.PlatFormType;
import com.contract.management.domain.exception.ClauseExtractionException;
import com.contract.management.domain.exception.FileProcessingException;
import com.contract.management.domain.model.Clause;
import com.contract.management.domain.model.ClauseExtraction;
import com.contract.management.domain.model.ClauseType;
import com.contract.management.domain.model.Prompt;
import com.contract.management.domain.model.RiskLevel;
import com.contract.management.domain.model.valueobject.ClauseContent;
import com.contract.management.domain.model.valueobject.ClauseExtractionResult;
import com.contract.management.domain.model.valueobject.ClauseTitle;
import com.contract.management.domain.model.valueobject.ComprehensiveClauseExtractionResult;
import com.contract.management.domain.model.valueobject.ExtractionId;
import com.contract.management.domain.model.valueobject.ExtractionStatus;
import com.contract.management.domain.model.valueobject.PromptType;
import com.contract.management.domain.repository.ClauseExtractionRepository;
import com.contract.management.domain.repository.ClauseRepository;
import com.contract.management.domain.repository.PromptFilters;
import com.contract.management.domain.repository.PromptRepository;
import com.contract.management.domain.service.CosService;
import com.contract.management.domain.service.OperationLogDomainService;
import com.contract.management.infrastructure.util.FileDownloadUtil;
import com.contract.management.infrastructure.util.PdfProcessor;
import com.contract.management.util.RepositoryQueryHelper;
import com.contractreview.fileapi.client.FileClient;
import com.contractreview.fileapi.dto.response.FileInfoResponse;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.compress.utils.Lists;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.net.URL;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * 条款抽取服务
 * 负责调用AI模型进行合同条款抽取
 *
 * @author SaltyFish
 * @since 1.0.0
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class ClauseExtractionService {

    private final AiClient aiClient;
    private final FileClient fileClient;
    private final ClauseExtractionRepository clauseExtractionRepository;
    private final ClauseRepository clauseRepository;
    private final OperationLogDomainService operationLogDomainService;
    private final ObjectMapper objectMapper;
    private final PromptRepository promptRepository;
    private final PdfProcessor pdfProcessor;
    private final FileDownloadUtil fileDownloadUtil;
    private final CosService cosService;

    @Value("${ruoyi.remote-auth.secret:}")
    private String secret;

    @Value("${file.download.type}")
    private String uploadType;

    // 最大页数限制，根据设计方案只处理<20页的文档
    private static final int MAX_PAGES_LIMIT = 20;

    /**
     * 异步执行条款抽取
     */
    @Async("clauseExtractionExecutor")
    public void extractClausesAsync(ExtractionId extractionId, Long contractId, String fileUuid) {
        log.info("开始异步执行条款抽取，extractionId: {}, contractId: {}, fileUuid: {}",
                extractionId, contractId, fileUuid);

        long startTime = System.currentTimeMillis();
        String ipAddress = "127.0.0.1"; // TODO: 从上下文获取真实IP
        Long userId = 1L; // TODO: 从上下文获取真实用户ID

        // 记录开始日志
        operationLogDomainService.recordClauseExtractionStart(
            extractionId.getValue(), contractId, userId, ipAddress
        );

        try {
            // 获取合同信息以获取本地文件URL
            // TODO: 这里需要从合同服务获取合同信息，当前先使用原始方法
            FileInfoResponse fileInfo = fileClient.queryByUuid(fileUuid, secret);
            if (fileInfo == null) {
                throw new ClauseExtractionException("文件不存在: " + fileUuid);
            }
            String fileName = fileInfo.getUuid() + "_" + fileInfo.getFileName();

            // 尝试获取本地文件URL，如果存在则使用本地文件
            String effectiveFileUrl = getEffectiveFileUrl(contractId, fileInfo.getFileUrl(), fileName);

            // 更新FileInfo中的URL为有效URL
            fileInfo.setFileUrl(effectiveFileUrl);

            // 根据文件类型选择抽取策略
            ComprehensiveClauseExtractionResult result = extractFromBinaryFile(fileInfo);

            // 完成抽取
            completeComprehensiveExtraction(extractionId, result);

            long executionTime = System.currentTimeMillis() - startTime;
            String resultSummary = String.format("成功抽取%d个条款，文档质量：%s，置信度%.2f",
                result.getClauses() != null ? result.getClauses().size() : 0,
                result.getTaskInfo() != null ? result.getTaskInfo().getDocumentQuality() : "unknown",
                result.getQualityMetrics() != null && result.getQualityMetrics().getOcrConfidenceAvg() != null ?
                    result.getQualityMetrics().getOcrConfidenceAvg() : 0.0
            );

            // 记录成功日志
            operationLogDomainService.recordClauseExtractionSuccess(
                extractionId.getValue(), contractId, userId, ipAddress, executionTime, resultSummary
            );

            log.info("条款抽取完成，extractionId: {}, 耗时: {}ms", extractionId, executionTime);

        } catch (Exception e) {
            long executionTime = System.currentTimeMillis() - startTime;
            String errorMessage = "条款抽取失败: " + e.getMessage();

            log.error("条款抽取失败，extractionId: {}", extractionId, e);

            // 记录失败日志
            operationLogDomainService.recordClauseExtractionFailure(
                extractionId.getValue(), contractId, userId, ipAddress, executionTime, errorMessage
            );

            failExtraction(extractionId, errorMessage);
        }
    }

    /**
     * 从二进制文件进行一体化条款抽取
     * 实现OCR + 结构分析 + 条款抽取 + 位置定位
     */
    private ComprehensiveClauseExtractionResult extractFromBinaryFile(FileInfoResponse fileInfo) {
        try {
            // 1. 获取文件内容（优先从本地URL下载，如果失败则从文件服务获取）
            byte[] fileContent = getFileBytesFromUrl(fileInfo);

            // 2. 检查文件类型和页数限制
            validateFile(fileInfo, fileContent);

            // 3. 构建一体化处理提示词
            String prompt = buildComprehensiveExtractionPrompt();

            // 4. 调用AI进行一体化处理
            String aiResponse = callAiForComprehensiveExtraction(prompt, fileInfo.getFileUrl());

            // 5. 解析AI响应为综合结果
            return parseComprehensiveExtractionResult(aiResponse);

        } catch (Exception e) {
            log.error("一体化条款抽取失败", e);
            throw new ClauseExtractionException("一体化条款抽取失败: " + e.getMessage(), e);
        }
    }

    /**
     * 验证文件是否符合处理要求
     */
    private void validateFile(FileInfoResponse fileInfo, byte[] fileContent) throws FileProcessingException {
        // 检查文件大小
        if (fileContent == null || fileContent.length == 0) {
            throw new FileProcessingException("文件内容为空");
        }

        // 对于PDF文件，检查页数
        if (StringUtils.hasText(fileInfo.getFileName()) &&
            fileInfo.getFileName().toLowerCase().endsWith(".pdf")) {

            if (pdfProcessor.isPageCountExceeded(fileContent, MAX_PAGES_LIMIT)) {
                int pageCount = pdfProcessor.getPageCount(fileContent);
                throw new FileProcessingException(
                    String.format("PDF页数超过处理限制。当前页数：%d，最大允许：%d", pageCount, MAX_PAGES_LIMIT)
                );
            }
            log.info("PDF文件验证通过，页数：{}", pdfProcessor.getPageCount(fileContent));
        }

        log.info("文件验证通过：{}", fileInfo.getFileName());
    }

    /**
     * 构建一体化处理提示词
     */
    private String buildComprehensiveExtractionPrompt() {
        Prompt prompt = RepositoryQueryHelper.queryOneAndCheckEmpty(
            () -> promptRepository.findByFilters(PromptFilters.builder()
                .promptName("一体化条款抽取")
                .promptType(PromptType.INNER)
                .enabled(true)
                .build()
            ),
            "未找到有效的一体化条款抽取提示词",
            () -> new FileProcessingException("未找到有效的一体化条款抽取提示词")
        );
        return prompt.getPromptContent().getValue();
    }

    /**
     * 调用AI进行一体化抽取
     */
    private String callAiForComprehensiveExtraction(String prompt, String fileUrl) {
        // 创建包含文件URL的多模态消息
        List<ChatRequest.Message.ContentItem> contentItems = new ArrayList<>();

        // 添加文本内容
        contentItems.add(ChatRequest.Message.ContentItem.text(prompt));

        // 添加合同文件
        contentItems.add(ChatRequest.Message.ContentItem.fileUrl(fileUrl));

        ChatRequest.Message message = ChatRequest.Message.multimodalMessage("user", contentItems);
        ArrayList<ChatRequest.Message> messages = Lists.newArrayList();
        messages.add(message);

        // 调用带视觉功能的AI接口
        var response = aiClient.chat(ChatRequest.builder()
            .platform(PlatFormType.GLM)
            .model(ModelType.GLM_4_5V.getModelCode())
            .maxTokens(10240)
            .messages(messages)
            .build(), secret);

        if (response == null || response.getData() == null ||
            response.getData().getMessages() == null || response.getData().getMessages().isEmpty()) {
            throw new ClauseExtractionException("AI视觉模型响应为空");
        }

        return response.getData().getMessages().get(0).getContent();
    }

    /**
     * 解析综合抽取结果
     */
    private ComprehensiveClauseExtractionResult parseComprehensiveExtractionResult(String aiResponse) {
        try {
            JsonNode jsonNode = objectMapper.readTree(aiResponse);

            // 解析任务信息
            ComprehensiveClauseExtractionResult.TaskInfo taskInfo = parseTaskInfo(
                jsonNode.path("task_info")
            );

            // 解析文档信息
            ComprehensiveClauseExtractionResult.DocumentInfo documentInfo = parseDocumentInfo(
                jsonNode.path("document_info")
            );

            // 解析结构信息
            ComprehensiveClauseExtractionResult.StructureInfo structureInfo = parseStructureInfo(
                jsonNode.path("structure")
            );

            // 解析条款列表
            List<ComprehensiveClauseExtractionResult.ExtractedClause> clauses = parseClauses(
                jsonNode.path("clauses")
            );

            // 解析质量指标
            ComprehensiveClauseExtractionResult.QualityMetrics qualityMetrics = parseQualityMetrics(
                jsonNode.path("quality_metrics")
            );

            return ComprehensiveClauseExtractionResult.builder()
                .taskInfo(taskInfo)
                .documentInfo(documentInfo)
                .structureInfo(structureInfo)
                .clauses(clauses)
                .qualityMetrics(qualityMetrics)
                .build();

        } catch (JsonProcessingException e) {
            log.error("解析AI综合抽取结果失败: {}", aiResponse, e);
            throw new ClauseExtractionException("解析AI综合抽取结果失败: " + e.getMessage(), e);
        }
    }

    /**
     * 解析任务信息
     */
    private ComprehensiveClauseExtractionResult.TaskInfo parseTaskInfo(JsonNode node) {
        return ComprehensiveClauseExtractionResult.TaskInfo.builder()
            .extractionTaskId(getTextValue(node, "task_id"))
            .processingTime(getIntValue(node, "processing_time"))
            .documentQuality(getTextValue(node, "document_quality"))
            .build();
    }

    /**
     * 解析文档信息
     */
    private ComprehensiveClauseExtractionResult.DocumentInfo parseDocumentInfo(JsonNode node) {
        JsonNode partiesNode = node.path("parties");
        ComprehensiveClauseExtractionResult.ContractParties parties =
            ComprehensiveClauseExtractionResult.ContractParties.builder()
                .partyA(getTextValue(partiesNode, "party_a"))
                .partyB(getTextValue(partiesNode, "party_b"))
                .build();

        return ComprehensiveClauseExtractionResult.DocumentInfo.builder()
            .totalPages(getIntValue(node, "total_pages"))
            .documentType(getTextValue(node, "document_type"))
            .parties(parties)
            .contractDate(getTextValue(node, "contract_date"))
            .contractAmount(getTextValue(node, "contract_amount"))
            .build();
    }

    /**
     * 解析结构信息
     */
    private ComprehensiveClauseExtractionResult.StructureInfo parseStructureInfo(JsonNode node) {
        List<ComprehensiveClauseExtractionResult.Chapter> chapters = new ArrayList<>();
        JsonNode chaptersNode = node.path("chapters");
        if (chaptersNode.isArray()) {
            for (JsonNode chapterNode : chaptersNode) {
                List<ComprehensiveClauseExtractionResult.Subsection> subsections = new ArrayList<>();
                JsonNode subsectionsNode = chapterNode.path("subsections");
                if (subsectionsNode.isArray()) {
                    for (JsonNode subsectionNode : subsectionsNode) {
                        ComprehensiveClauseExtractionResult.Subsection subsection =
                            ComprehensiveClauseExtractionResult.Subsection.builder()
                                .title(getTextValue(subsectionNode, "title"))
                                .page(getIntValue(subsectionNode, "page"))
                                .build();
                        subsections.add(subsection);
                    }
                }

                ComprehensiveClauseExtractionResult.Chapter chapter =
                    ComprehensiveClauseExtractionResult.Chapter.builder()
                        .title(getTextValue(chapterNode, "title"))
                        .pageStart(getIntValue(chapterNode, "page_start"))
                        .pageEnd(getIntValue(chapterNode, "page_end"))
                        .level(getIntValue(chapterNode, "level"))
                        .subsections(subsections)
                        .build();
                chapters.add(chapter);
            }
        }

        List<ComprehensiveClauseExtractionResult.SpecialArea> specialAreas = new ArrayList<>();
        JsonNode specialAreasNode = node.path("special_areas");
        if (specialAreasNode.isArray()) {
            for (JsonNode areaNode : specialAreasNode) {
                ComprehensiveClauseExtractionResult.SpecialArea area =
                    ComprehensiveClauseExtractionResult.SpecialArea.builder()
                        .type(getTextValue(areaNode, "type"))
                        .pages(parseIntegerList(areaNode.path("pages")))
                        .description(getTextValue(areaNode, "description"))
                        .build();
                specialAreas.add(area);
            }
        }

        return ComprehensiveClauseExtractionResult.StructureInfo.builder()
            .chapters(chapters)
            .specialAreas(specialAreas)
            .build();
    }

    /**
     * 解析条款列表
     */
    private List<ComprehensiveClauseExtractionResult.ExtractedClause> parseClauses(JsonNode node) {
        List<ComprehensiveClauseExtractionResult.ExtractedClause> clauses = new ArrayList<>();
        if (node.isArray()) {
            for (JsonNode clauseNode : node) {
                ComprehensiveClauseExtractionResult.ExtractedClause clause =
                    ComprehensiveClauseExtractionResult.ExtractedClause.builder()
                        .clauseId(getTextValue(clauseNode, "clause_id"))
                        .clauseType(getTextValue(clauseNode, "clause_type"))
                        .clauseTitle(getTextValue(clauseNode, "clause_title"))
                        .content(getTextValue(clauseNode, "content"))
                        .confidenceScore(getDoubleValue(clauseNode, "confidence_score"))
                        .riskLevel(getTextValue(clauseNode, "risk_level"))
                        .extractedEntities(parseExtractedEntities(clauseNode.path("extracted_entities")))
                        .riskFactors(parseRiskFactors(clauseNode.path("risk_factors")))
                        .positions(parsePositions(clauseNode.path("positions")))
                        .relatedChapters(parseStringList(clauseNode.path("related_chapters")))
                        .extractedAt(LocalDateTime.now()) // 当前时间
                        .build();
                clauses.add(clause);
            }
        }
        return clauses;
    }

    /**
     * 解析提取的实体
     */
    private ComprehensiveClauseExtractionResult.ExtractedEntities parseExtractedEntities(JsonNode node) {
        return ComprehensiveClauseExtractionResult.ExtractedEntities.builder()
            .amountPercentage(getTextValue(node, "amount_percentage"))
            .paymentPeriod(getTextValue(node, "payment_period"))
            .paymentCondition(getTextValue(node, "payment_condition"))
            .paymentAmount(getTextValue(node, "payment_amount"))
            .build();
    }

    /**
     * 解析风险因子
     */
    private List<ComprehensiveClauseExtractionResult.RiskFactor> parseRiskFactors(JsonNode node) {
        List<ComprehensiveClauseExtractionResult.RiskFactor> factors = new ArrayList<>();
        if (node.isArray()) {
            for (JsonNode factorNode : node) {
                ComprehensiveClauseExtractionResult.RiskFactor factor =
                    ComprehensiveClauseExtractionResult.RiskFactor.builder()
                        .factor(getTextValue(factorNode, "factor"))
                        .severity(getTextValue(factorNode, "severity"))
                        .build();
                factors.add(factor);
            }
        }
        return factors;
    }

    /**
     * 解析位置信息
     */
    private List<ComprehensiveClauseExtractionResult.ClausePosition> parsePositions(JsonNode node) {
        List<ComprehensiveClauseExtractionResult.ClausePosition> positions = new ArrayList<>();
        if (node.isArray()) {
            for (JsonNode positionNode : node) {
                ComprehensiveClauseExtractionResult.ClausePosition position =
                    ComprehensiveClauseExtractionResult.ClausePosition.builder()
                        .page(getIntValue(positionNode, "page"))
                        .bbox(parseIntegerList(positionNode.path("bbox")))
                        .context(getTextValue(positionNode, "context"))
                        .textSnippet(getTextValue(positionNode, "text_snippet"))
                        .build();
                positions.add(position);
            }
        }
        return positions;
    }

    /**
     * 解析质量指标
     */
    private ComprehensiveClauseExtractionResult.QualityMetrics parseQualityMetrics(JsonNode node) {
        return ComprehensiveClauseExtractionResult.QualityMetrics.builder()
            .totalClausesFound(getIntValue(node, "total_clauses_found"))
            .highConfidenceClauses(getIntValue(node, "high_confidence_clauses"))
            .mediumConfidenceClauses(getIntValue(node, "medium_confidence_clauses"))
            .lowConfidenceClauses(getIntValue(node, "low_confidence_clauses"))
            .pagesWithIssues(parseIntegerList(node.path("pages_with_issues")))
            .ocrConfidenceAvg(getDoubleValue(node, "ocr_confidence_avg"))
            .structureAccuracy(getDoubleValue(node, "structure_accuracy"))
            .recommendedReviewClauses(parseStringList(node.path("recommended_review_clauses")))
            .build();
    }

    // 辅助方法
    private List<Integer> parseIntegerList(JsonNode node) {
        List<Integer> list = new ArrayList<>();
        if (node.isArray()) {
            for (JsonNode item : node) {
                if (item.isInt()) {
                    list.add(item.asInt());
                }
            }
        }
        return list;
    }

    /**
     * 解析合同主体信息
     */
    private ClauseExtractionResult.ContractParties parseContractParties(JsonNode node) {
        return ClauseExtractionResult.ContractParties.builder()
            .partyA(getTextValue(node, "partyA"))
            .partyB(getTextValue(node, "partyB"))
            .partyALegalRepresentative(getTextValue(node, "partyALegalRepresentative"))
            .partyBLegalRepresentative(getTextValue(node, "partyBLegalRepresentative"))
            .partyAAddress(getTextValue(node, "partyAAddress"))
            .partyBAddress(getTextValue(node, "partyBAddress"))
            .partyAContact(getTextValue(node, "partyAContact"))
            .partyBContact(getTextValue(node, "partyBContact"))
            .build();
    }

    /**
     * 解析合同期限信息
     */
    private ClauseExtractionResult.ContractTerms parseContractTerms(JsonNode node) {
        return ClauseExtractionResult.ContractTerms.builder()
            .signDate(getDateTimeValue(node, "signDate"))
            .effectiveDate(getDateTimeValue(node, "effectiveDate"))
            .expiryDate(getDateTimeValue(node, "expiryDate"))
            .contractPeriod(getTextValue(node, "contractPeriod"))
            .autoRenewal(getBooleanValue(node, "autoRenewal"))
            .renewalNoticeDays(getIntValue(node, "renewalNoticeDays"))
            .build();
    }

    /**
     * 解析合同金额信息
     */
    private ClauseExtractionResult.ContractAmount parseContractAmount(JsonNode node) {
        return ClauseExtractionResult.ContractAmount.builder()
            .totalAmount(getDoubleValue(node, "totalAmount"))
            .currency(getTextValue(node, "currency"))
            .paymentMethod(getTextValue(node, "paymentMethod"))
            .amountDescription(getTextValue(node, "amountDescription"))
            .build();
    }

    /**
     * 解析关键条款列表
     */
    private List<ClauseExtractionResult.KeyClause> parseKeyClauses(JsonNode node) {
        List<ClauseExtractionResult.KeyClause> clauses = new ArrayList<>();
        if (node.isArray()) {
            for (JsonNode clauseNode : node) {
                ClauseExtractionResult.KeyClause clause = ClauseExtractionResult.KeyClause.builder()
                    .clauseType(getTextValue(clauseNode, "clauseType"))
                    .clauseContent(getTextValue(clauseNode, "clauseContent"))
                    .location(getTextValue(clauseNode, "location"))
                    .importance(getDoubleValue(clauseNode, "importance"))
                    .keywords(parseStringList(clauseNode.path("keywords")))
                    .build();
                clauses.add(clause);
            }
        }
        return clauses;
    }

    /**
     * 解析风险条款列表
     */
    private List<ClauseExtractionResult.RiskClause> parseRiskClauses(JsonNode node) {
        List<ClauseExtractionResult.RiskClause> clauses = new ArrayList<>();
        if (node.isArray()) {
            for (JsonNode clauseNode : node) {
                ClauseExtractionResult.RiskClause clause = ClauseExtractionResult.RiskClause.builder()
                    .riskType(getTextValue(clauseNode, "riskType"))
                    .riskLevel(getTextValue(clauseNode, "riskLevel"))
                    .clauseContent(getTextValue(clauseNode, "clauseContent"))
                    .riskDescription(getTextValue(clauseNode, "riskDescription"))
                    .suggestions(parseStringList(clauseNode.path("suggestions")))
                    .riskScore(getDoubleValue(clauseNode, "riskScore"))
                    .build();
                clauses.add(clause);
            }
        }
        return clauses;
    }

    // 辅助方法
    private String getTextValue(JsonNode node, String fieldName) {
        JsonNode fieldNode = node.path(fieldName);
        return fieldNode.isTextual() ? fieldNode.asText() : null;
    }

    private LocalDateTime getDateTimeValue(JsonNode node, String fieldName) {
        JsonNode fieldNode = node.path(fieldName);
        if (fieldNode.isTextual() && !fieldNode.asText().isEmpty()) {
            try {
                return LocalDateTime.parse(fieldNode.asText());
            } catch (Exception e) {
                log.warn("日期格式解析失败: {}", fieldNode.asText());
            }
        }
        return null;
    }

    private Double getDoubleValue(JsonNode node, String fieldName) {
        JsonNode fieldNode = node.path(fieldName);
        return fieldNode.isNumber() ? fieldNode.asDouble() : null;
    }

    private Integer getIntValue(JsonNode node, String fieldName) {
        JsonNode fieldNode = node.path(fieldName);
        return fieldNode.isInt() ? fieldNode.asInt() : null;
    }

    private Boolean getBooleanValue(JsonNode node, String fieldName) {
        JsonNode fieldNode = node.path(fieldName);
        return fieldNode.isBoolean() ? fieldNode.asBoolean() : null;
    }

    private List<String> parseStringList(JsonNode node) {
        List<String> list = new ArrayList<>();
        if (node.isArray()) {
            for (JsonNode item : node) {
                if (item.isTextual()) {
                    list.add(item.asText());
                }
            }
        }
        return list;
    }

    /**
     * 从URL获取文件字节数组（优先使用本地URL）
     */
    private byte[] getFileBytesFromUrl(FileInfoResponse fileInfo) {
        try {
            String fileUrl = fileInfo.getFileUrl();
            String fileName = fileInfo.getFileName();
            String contentType = fileInfo.getContentType();

            log.info("从URL获取文件内容: url={}, fileName={}", fileUrl, fileName);

            // 从URL下载文件
            URL url = new URL(fileUrl);
            try (java.io.InputStream inputStream = url.openStream();
                 java.io.ByteArrayOutputStream outputStream = new java.io.ByteArrayOutputStream()) {

                byte[] buffer = new byte[8192];
                int bytesRead;
                long totalBytes = 0;

                while ((bytesRead = inputStream.read(buffer)) != -1) {
                    outputStream.write(buffer, 0, bytesRead);
                    totalBytes += bytesRead;
                }

                byte[] fileContent = outputStream.toByteArray();
                log.info("文件下载成功: url={}, fileName={}, size={} bytes", fileUrl, fileName, totalBytes);
                return fileContent;

            } catch (IOException e) {
                log.error("从URL下载文件失败: url={}", fileUrl, e);
                throw new RuntimeException("从URL下载文件失败: " + e.getMessage(), e);
            }

        } catch (Exception e) {
            log.error("获取文件内容失败，尝试从文件服务获取: fileInfo={}", fileInfo.getFileName(), e);

            // 降级方案：从文件服务获取
            return getFileBytesFromFileService(fileInfo.getUuid());
        }
    }

    /**
     * 从文件服务获取文件字节数组（降级方案）
     */
    private byte[] getFileBytesFromFileService(String fileUuid) {
        try {
            log.info("从文件服务获取文件内容: fileUuid={}", fileUuid);
            java.io.InputStream inputStream = fileClient.downloadByUuid(fileUuid);
            return inputStream.readAllBytes();
        } catch (Exception e) {
            log.error("从文件服务下载文件失败: {}", fileUuid, e);
            throw new RuntimeException("下载文件失败: " + e.getMessage(), e);
        }
    }

    private byte[] getFileBytes(String fileUuid) {
        return getFileBytesFromFileService(fileUuid);
    }

    private MultipartFile createMultipartFile(String fileName, String contentType, byte[] content) {
        return new MultipartFile() {
            @Override
            public String getName() {
                return "file";
            }

            @Override
            public String getOriginalFilename() {
                return fileName;
            }

            @Override
            public String getContentType() {
                return contentType;
            }

            @Override
            public boolean isEmpty() {
                return content == null || content.length == 0;
            }

            @Override
            public long getSize() {
                return content != null ? content.length : 0;
            }

            @Override
            public byte[] getBytes() {
                return content;
            }

            @Override
            public java.io.InputStream getInputStream() {
                return new java.io.ByteArrayInputStream(content);
            }

            @Override
            public void transferTo(java.io.File dest) {
                try (java.io.FileOutputStream fos = new java.io.FileOutputStream(dest)) {
                    fos.write(content);
                } catch (java.io.IOException e) {
                    throw new RuntimeException("文件写入失败", e);
                }
            }
        };
    }

    /**
     * 获取有效的文件URL
     * 优先使用本地文件URL，如果不存在则使用原始URL
     */
    private String getEffectiveFileUrl(Long contractId, String originalUrl, String fileName) {
        try {
            if ("local".equals(uploadType)) {
                String localFileUrl = fileDownloadUtil.getLocalFileUrl(fileName);
                log.info("使用本地文件URL进行条款抽取: contractId={}, localUrl={}", contractId, localFileUrl);
                return localFileUrl;
            } else if ("cos".equals(uploadType)) {
                return cosService.getPresignedUrl(fileName);
            } else {
                throw new IllegalArgumentException("非法上传文件类型");
            }
        } catch (Exception e) {
            log.warn("获取有效文件URL失败, contractId={}, error={}", contractId, e.getMessage());
            return originalUrl;
        }
    }

    /**
     * 根据UUID查找本地文件
     */
    private String findLocalFileByUuid(String fileUuid) {
        try {
            String tmpDir = fileDownloadUtil.getTmpStorageDir();
            java.io.File dir = new java.io.File(tmpDir);

            if (dir.exists() && dir.isDirectory()) {
                java.io.File[] files = dir.listFiles((file, name) -> name.startsWith(fileUuid + "_"));
                if (files != null && files.length > 0) {
                    return files[0].getName(); // 返回第一个匹配的文件名
                }
            }
        } catch (Exception e) {
            log.error("查找本地文件失败: uuid={}", fileUuid, e);
        }
        return null;
    }

    // ==================== 条款抽取状态管理方法 ====================

    /**
     * 完成综合条款抽取
     */
    private void completeComprehensiveExtraction(ExtractionId extractionId, ComprehensiveClauseExtractionResult result) {
        try {
            // 获取当前的抽取任务
            ClauseExtraction extraction = clauseExtractionRepository.findById(extractionId);
            if (extraction == null) {
                log.error("条款抽取任务不存在: {}", extractionId);
                return;
            }

            // 将综合结果转换为原有的ClauseExtractionResult格式
            ClauseExtractionResult legacyResult = convertToLegacyResult(result);

            // 按照状态机规则进行状态转换：PENDING -> PROCESSING -> COMPLETED
            if (extraction.getStatus().canTransitionTo(ExtractionStatus.PROCESSING)) {
                extraction.startProcessing();
                clauseExtractionRepository.update(extraction);
                log.debug("条款抽取状态已更新为处理中: {}", extractionId);
            }

            clauseExtractionRepository.update(extraction);

            // 保存详细条款信息到clause表
            saveDetailedClauses(extractionId, result);

            // 更新状态为完成
            extraction.complete(legacyResult);
            clauseExtractionRepository.save(extraction);
            log.info("综合条款抽取状态已更新为完成: {}", extractionId);
        } catch (Exception e) {
            log.error("更新综合条款抽取状态失败: extractionId={}", extractionId, e);
        }
    }

    /**
     * 将综合结果转换为原有的ClauseExtractionResult格式
     * 保持向后兼容性
     */
    private ClauseExtractionResult convertToLegacyResult(ComprehensiveClauseExtractionResult comprehensiveResult) {
        // 转换合同主体信息
        ClauseExtractionResult.ContractParties parties = null;
        if (comprehensiveResult.getDocumentInfo() != null &&
            comprehensiveResult.getDocumentInfo().getParties() != null) {

            var docParties = comprehensiveResult.getDocumentInfo().getParties();
            parties = ClauseExtractionResult.ContractParties.builder()
                .partyA(docParties.getPartyA())
                .partyB(docParties.getPartyB())
                .build();
        }

        // 转换关键条款（从综合结果中的条款列表）
        List<ClauseExtractionResult.KeyClause> keyClauses = new ArrayList<>();
        if (comprehensiveResult.getClauses() != null) {
            for (var clause : comprehensiveResult.getClauses()) {
                ClauseExtractionResult.KeyClause keyClause = ClauseExtractionResult.KeyClause.builder()
                    .clauseType(clause.getClauseType())
                    .clauseContent(clause.getContent())
                    .location(clause.getClauseTitle())
                    .importance(clause.getConfidenceScore())
                    .build();
                keyClauses.add(keyClause);
            }
        }

        // 转换风险条款
        List<ClauseExtractionResult.RiskClause> riskClauses = new ArrayList<>();
        if (comprehensiveResult.getClauses() != null) {
            for (var clause : comprehensiveResult.getClauses()) {
                if ("high".equals(clause.getRiskLevel()) || "medium".equals(clause.getRiskLevel())) {
                    ClauseExtractionResult.RiskClause riskClause = ClauseExtractionResult.RiskClause.builder()
                        .riskType(clause.getClauseType())
                        .riskLevel(clause.getRiskLevel())
                        .clauseContent(clause.getContent())
                        .riskDescription("Extracted from comprehensive analysis")
                        .build();
                    riskClauses.add(riskClause);
                }
            }
        }

        return ClauseExtractionResult.builder()
            .contractParties(parties)
            .keyClauses(keyClauses)
            .riskClauses(riskClauses)
            .confidenceScore(comprehensiveResult.getQualityMetrics() != null ?
                comprehensiveResult.getQualityMetrics().getOcrConfidenceAvg() : 0.8)
            .processingTimeMs(comprehensiveResult.getTaskInfo() != null ?
                (long) comprehensiveResult.getTaskInfo().getProcessingTime() * 1000 : 5000L)
            .aiModel(ModelType.GLM_4_5V.getModelCode())
            .extractionTime(LocalDateTime.now())
            .build();
    }

    /**
     * 条款抽取失败
     */
    private void failExtraction(ExtractionId extractionId, String errorMessage) {
        try {
            // 获取当前的抽取任务
            ClauseExtraction extraction = clauseExtractionRepository.findById(extractionId);
            if (extraction == null) {
                log.error("条款抽取任务不存在: {}", extractionId);
                return;
            }

            // 按照状态机规则进行状态转换：PENDING -> PROCESSING -> FAILED
            if (extraction.getStatus().canTransitionTo(ExtractionStatus.PROCESSING)) {
                extraction.startProcessing();
                clauseExtractionRepository.update(extraction);
                log.debug("条款抽取状态已更新为处理中: {}", extractionId);
            }

            // 更新状态为失败
            extraction.fail(errorMessage);
            clauseExtractionRepository.update(extraction);

            log.info("条款抽取状态已更新为失败: {}", extractionId);
        } catch (Exception e) {
            log.error("更新条款抽取状态失败: extractionId={}", extractionId, e);
        }
    }

    /**
     * 保存详细条款信息到clause表
     * 将综合抽取结果中的条款转换为Clause领域模型并保存
     *
     * @param extractionId 抽取任务ID
     * @param comprehensiveResult 综合抽取结果
     */
    private void saveDetailedClauses(ExtractionId extractionId, ComprehensiveClauseExtractionResult comprehensiveResult) {
        try {
            if (comprehensiveResult == null || comprehensiveResult.getClauses() == null
                || comprehensiveResult.getClauses().isEmpty()) {
                log.info("没有条款信息需要保存: extractionId={}", extractionId);
                return;
            }

            // 获取抽取任务信息
            ClauseExtraction extraction = clauseExtractionRepository.findById(extractionId);
            if (extraction == null) {
                log.error("抽取任务不存在，无法保存条款: extractionId={}", extractionId);
                return;
            }

            // 转换综合结果为Clause领域模型列表
            List<Clause> clauses = comprehensiveResult.getClauses().stream()
                .map(extractedClause -> convertToClause(extractedClause, extraction))
                .filter(java.util.Objects::nonNull)
                .collect(java.util.stream.Collectors.toList());

            if (clauses.isEmpty()) {
                log.info("转换后没有有效的条款需要保存: extractionId={}", extractionId);
                return;
            }

            // 批量保存条款
            List<Clause> savedClauses = clauseRepository.saveAll(clauses);

            log.info("成功保存{}个条款到clause表: extractionId={}", savedClauses.size(), extractionId);

        } catch (Exception e) {
            log.error("保存详细条款信息失败: extractionId={}", extractionId, e);
            // 不抛出异常，避免影响主流程
        }
    }

    /**
     * 将抽取的条款转换为Clause领域模型
     *
     * @param extractedClause 抽取的条款
     * @param extraction 抽取任务
     * @return Clause领域模型
     */
    private Clause convertToClause(ComprehensiveClauseExtractionResult.ExtractedClause extractedClause,
                                  ClauseExtraction extraction) {
        try {
            // 构建条款类型
            ClauseType clauseType = ClauseType.fromString(extractedClause.getClauseType());

            // 构建条款标题
            ClauseTitle clauseTitle = extractedClause.getClauseTitle() != null ?
                ClauseTitle.of(extractedClause.getClauseTitle()) : null;

            // 构建条款内容
            ClauseContent clauseContent = ClauseContent.of(extractedClause.getContent());

            // 构建风险等级
            RiskLevel riskLevel = extractedClause.getRiskLevel() != null ?
                RiskLevel.fromString(extractedClause.getRiskLevel()) : null;

            // 转换位置信息为JSON字符串
            String clausePositionJson = null;
            if (extractedClause.getPositions() != null && !extractedClause.getPositions().isEmpty()) {
                try {
                    clausePositionJson = objectMapper.writeValueAsString(extractedClause.getPositions());
                } catch (Exception e) {
                    log.warn("转换条款位置信息失败: {}", e.getMessage());
                }
            }

            // 转换提取的实体为JSON字符串
            String extractedEntitiesJson = null;
            if (extractedClause.getExtractedEntities() != null) {
                try {
                    extractedEntitiesJson = objectMapper.writeValueAsString(extractedClause.getExtractedEntities());
                } catch (Exception e) {
                    log.warn("转换提取实体信息失败: {}", e.getMessage());
                }
            }

            // 转换风险因子为JSON字符串
            String riskFactorsJson = null;
            if (extractedClause.getRiskFactors() != null && !extractedClause.getRiskFactors().isEmpty()) {
                try {
                    riskFactorsJson = objectMapper.writeValueAsString(extractedClause.getRiskFactors());
                } catch (Exception e) {
                    log.warn("转换风险因子信息失败: {}", e.getMessage());
                }
            }

            // 使用重建构造函数创建Clause
            return new Clause(
                null, // ID将由数据库生成
                extraction.getId().getValue(), // extraction_taskId
                extraction.getContractId(), // contractId
                clauseType,
                clauseTitle,
                clauseContent,
                clausePositionJson,
                extractedClause.getConfidenceScore() != null ?
                    java.math.BigDecimal.valueOf(extractedClause.getConfidenceScore()) : null,
                extractedEntitiesJson,
                riskLevel,
                riskFactorsJson,
                extractedClause.getExtractedAt() != null ? extractedClause.getExtractedAt() : java.time.LocalDateTime.now()
            );

        } catch (Exception e) {
            log.error("转换条款失败: clauseId={}, error={}", extractedClause.getClauseId(), e.getMessage());
            return null;
        }
    }
}