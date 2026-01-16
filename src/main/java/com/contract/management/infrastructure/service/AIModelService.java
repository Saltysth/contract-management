package com.contract.management.infrastructure.service;

import com.alibaba.nacos.shaded.com.google.gson.Gson;
import com.contract.ai.feign.client.AiClient;
import com.contract.ai.feign.dto.ChatRequest;
import com.contract.ai.feign.dto.ChatResponse;
import com.contract.ai.feign.enums.ModelType;
import com.contract.common.constant.ContractType;
import com.contract.management.domain.exception.FileProcessingException;
import com.contract.management.domain.model.Prompt;
import com.contract.management.domain.model.valueobject.PromptType;
import com.contract.management.domain.repository.PromptFilters;
import com.contract.management.infrastructure.dto.ContractClassificationResult;
import com.contract.management.infrastructure.repository.PromptRepositoryImpl;
import com.contract.management.util.RepositoryQueryHelper;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.compress.utils.Lists;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * AI模型服务
 * 负责与大模型接口的交互，进行合同类型分类
 *
 * @author SaltyFish
 * @since 1.0.0
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class AIModelService {
    private final AiClient aiClient;
    private final ObjectMapper objectMapper;
    private final PromptRepositoryImpl promptRepositoryImpl;

    /**
     * 通过文本内容进行合同分类
     *
     * @param textContent 文本内容（前2000字）
     * @return 合同分类结果
     */
    public ContractClassificationResult classifyByText(String textContent) {
        try {
            log.debug("开始通过文本内容进行合同分类，文本长度: {}", textContent != null ? textContent.length() : 0);

            if (textContent == null || textContent.trim().isEmpty()) {
                throw new FileProcessingException("文本内容为空，无法进行分类");
            }

            // 限制文本长度为2000字
            String limitedText = textContent.length() > 2000 ?
                textContent.substring(0, 2000) : textContent;

            Prompt prompts = RepositoryQueryHelper.queryOneAndCheckEmpty(
                () -> promptRepositoryImpl.findByFilters(PromptFilters.builder()
                    .promptName("分类合同提示词-文字")
                    .promptType(PromptType.INNER)
                    .enabled(true)
                    .build()),
                "未找到有效的文字合同分类提示词",
                () -> new FileProcessingException("未找到有效的合同分类提示词")
            );

            // 实际调用大模型对话接口
            String prompt = prompts.getPromptContent().getValue();
            prompt = prompt.replace("{CONTENT}", limitedText);
            ChatRequest.Message message = ChatRequest.Message.textMessage("user",  prompt);
            ArrayList<ChatRequest.Message> messages = Lists.newArrayList();
            messages.add(message);
            ChatResponse response = aiClient.chat(
                ChatRequest.builder()
                    .model(ModelType.IFlow_GLM_4_6.getModelCode())
                    .messages(messages)
                .build()).getData();
            if (null == response || CollectionUtils.isEmpty(response.getMessages())) {
                log.warn("模型分类结果为空");
                throw new FileProcessingException("模型分类结果为空");
            }

            return parseAIResponse(response.getMessages().get(0));

        } catch (Exception e) {
            log.error("AI文本分类失败", e);
            return ContractClassificationResult.builder()
                .contractType(ContractType.OTHER)
                .confidence(0.0)
                .success(false)
                .errorMessage("AI分类失败: " + e.getMessage())
                .build();
        }
    }

    /**
     * 通过文件内容进行合同分类
     *
     * @param fileContent 文件内容（图片/PDF）
     * @param fileType 文件类型
     * @return 合同分类结果
     */
    public ContractClassificationResult classifyByFile(byte[] fileContent, String fileType) {
        try {
            log.debug("开始通过文件内容进行合同分类，文件类型: {}, 大小: {} bytes", fileType, fileContent.length);

            Prompt prompts = RepositoryQueryHelper.queryOneAndCheckEmpty(
                () -> promptRepositoryImpl.findByFilters(PromptFilters.builder()
                    .promptName("分类合同提示词-图片")
                    .promptType(PromptType.INNER)
                    .enabled(true)
                    .build()),
                "未找到有效的图片合同分类提示词",
                () -> new FileProcessingException("未找到有效的合同分类提示词")
            );
            // 调用大模型视觉接口进行文件分析
            String prompt = prompts.getPromptContent().getValue();
            ChatRequest.Message message = ChatRequest.Message.textMessage("user",  prompt);
            ArrayList<ChatRequest.Message> messages = Lists.newArrayList();
            messages.add(message);

            // 将byte[]转换为MultipartFile
            String fileName = "contract." + getFileExtension(fileType);
            MultipartFile multipartFile = createMultipartFile(fileName, getContentType(fileType), fileContent);

            // 调用带视觉功能的AI接口
            var apiResponse = aiClient.chatWithVisionBase64(
                new Gson().toJson(ChatRequest.builder()
                    .model(ModelType.GLM_4V_PLUS_0111.getModelCode())
                    .messages(messages)
                    .build()),
                new MultipartFile[]{multipartFile}
            );

            if (null == apiResponse || apiResponse.getData() == null ||
                CollectionUtils.isEmpty(apiResponse.getData().getMessages())) {
                log.warn("视觉模型分类结果为空");
                throw new FileProcessingException("视觉模型分类结果为空");
            }

            return parseAIResponse(apiResponse.getData().getMessages().get(0));

        } catch (Exception e) {
            log.error("AI文件分类失败", e);
            return ContractClassificationResult.builder()
                .contractType(ContractType.OTHER)
                .confidence(0.0)
                .success(false)
                .errorMessage("AI文件分类失败: " + e.getMessage())
                .build();
        }
    }

    /**
     * 获取文件扩展名
     */
    private String getFileExtension(String fileType) {
        switch (fileType.toLowerCase()) {
            case "pdf":
                return "pdf";
            case "image":
                return "jpg";
            case "document":
                return "docx";
            default:
                return "txt";
        }
    }

    /**
     * 获取Content-Type
     */
    private String getContentType(String fileType) {
        switch (fileType.toLowerCase()) {
            case "pdf":
                return "application/pdf";
            case "image":
                return "image/jpeg";
            case "document":
                return "application/vnd.openxmlformats-officedocument.wordprocessingml.document";
            default:
                return "text/plain";
        }
    }

    /**
     * 创建MultipartFile
     */
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
            public byte[] getBytes() throws java.io.IOException {
                return content;
            }

            @Override
            public java.io.InputStream getInputStream() throws java.io.IOException {
                return new java.io.ByteArrayInputStream(content);
            }

            @Override
            public void transferTo(java.io.File dest) throws java.io.IOException, java.lang.IllegalStateException {
                new java.io.FileOutputStream(dest).write(content);
            }
        };
    }

    /**
     * 通过PDF提取的多张图片进行合同分类
     *
     * @param imageContents PDF提取的图片内容列表
     * @return 合同分类结果
     */
    public ContractClassificationResult classifyByPDFImages(List<byte[]> imageContents) {
        try {
            log.debug("开始通过PDF提取的图片进行合同分类，图片数量: {}", imageContents.size());

            if (imageContents.isEmpty()) {
                throw new FileProcessingException("PDF图片内容为空，无法进行分类");
            }

            Prompt prompts = RepositoryQueryHelper.queryOneAndCheckEmpty(
                () -> promptRepositoryImpl.findByFilters(PromptFilters.builder()
                    .promptName("分类合同提示词-图片")
                    .promptType(PromptType.INNER)
                    .enabled(true)
                    .build()),
                "未找到有效的图片合同分类提示词",
                () -> new FileProcessingException("未找到有效的合同分类提示词")
            );

            // 调用大模型视觉接口进行文件分析
            String prompt = prompts.getPromptContent().getValue();
            ChatRequest.Message message = ChatRequest.Message.textMessage("user", prompt);
            ArrayList<ChatRequest.Message> messages = Lists.newArrayList();
            messages.add(message);

            // 将List<byte[]>转换为MultipartFile数组
            MultipartFile[] multipartFiles = new MultipartFile[imageContents.size()];
            for (int i = 0; i < imageContents.size(); i++) {
                String fileName = "pdf_page_" + (i + 1) + ".png";
                multipartFiles[i] = createMultipartFile(fileName, "image/png", imageContents.get(i));
            }

            // 调用带视觉功能的AI接口
            var apiResponse = aiClient.chatWithVisionBase64(
                new Gson().toJson(ChatRequest.builder()
                    .model(ModelType.GLM_4V_PLUS_0111.getModelCode())
                    .messages(messages)
                    .build()),
                multipartFiles
            );

            if (null == apiResponse || apiResponse.getData() == null ||
                CollectionUtils.isEmpty(apiResponse.getData().getMessages())) {
                log.warn("视觉模型分类结果为空");
                throw new FileProcessingException("视觉模型分类结果为空");
            }

            return parseAIResponse(apiResponse.getData().getMessages().get(0));

        } catch (Exception e) {
            log.error("AI PDF图片分类失败", e);
            return ContractClassificationResult.builder()
                .contractType(ContractType.OTHER)
                .confidence(0.0)
                .success(false)
                .errorMessage("AI PDF图片分类失败: " + e.getMessage())
                .build();
        }
    }

  /**
     * 解析AI响应
     */
    private ContractClassificationResult parseAIResponse(ChatResponse.Message aiResponse) {
        try {
            String content = aiResponse.getContent();
            JsonNode jsonNode = objectMapper.readTree(content);

            String contractTypeStr = jsonNode.path("contractType").asText("OTHER");
            Double confidence = jsonNode.path("confidence").asDouble(0.0);
            String reason = jsonNode.path("reason").asText("");

            ContractType contractType;
            try {
                contractType = ContractType.of(contractTypeStr);
            } catch (IllegalArgumentException e) {
                log.warn("未知的合同类型: {}, 使用默认值", contractTypeStr);
                contractType = ContractType.OTHER;
            }

            Map<String, Object> extendedProperties = new HashMap<>();
            extendedProperties.put("reason", reason);
            extendedProperties.put("aiRawResponse", aiResponse);

            return ContractClassificationResult.builder()
                .contractType(contractType)
                .confidence(confidence)
                .classificationMethod("AI_TEXT_ANALYSIS")
                .aiRawResponse(content)
                .extendedProperties(extendedProperties)
                .success(true)
                .build();

        } catch (Exception e) {
            log.error("JSON解析失败: {}", aiResponse, e);
            return ContractClassificationResult.builder()
                .contractType(ContractType.OTHER)
                .confidence(0.0)
                .success(false)
                .errorMessage("AI响应格式错误")
                .build();
        }
    }

}