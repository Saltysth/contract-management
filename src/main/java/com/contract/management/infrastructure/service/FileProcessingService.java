package com.contract.management.infrastructure.service;

import com.contract.management.domain.exception.FileProcessingException;
import com.contractreview.fileapi.client.FileClient;
import com.contractreview.fileapi.dto.response.FileInfoResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.pdfbox.Loader;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.rendering.PDFRenderer;
import org.apache.pdfbox.text.PDFTextStripper;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFParagraph;
import org.springframework.stereotype.Service;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;

/**
 * 文件处理服务
 * 负责文件内容的提取和格式转换
 *
 * @author SaltyFish
 * @since 1.0.0
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class FileProcessingService {

    private final FileClient fileClient;

    /**
     * 文件类型枚举
     */
    public enum FileType {
        DOCUMENT("document", "doc", "docx", "txt"),
        PDF("pdf", "pdf"),
        IMAGE("image", "jpg", "jpeg", "png", "bmp", "gif"),
        UNKNOWN("unknown");

        private final String category;
        private final String[] extensions;

        FileType(String category, String... extensions) {
            this.category = category;
            this.extensions = extensions;
        }

        public String getCategory() {
            return category;
        }

        public static FileType fromFileName(String fileName) {
            if (fileName == null) {
                return UNKNOWN;
            }

            String lowerName = fileName.toLowerCase();
            for (FileType type : values()) {
                for (String ext : type.extensions) {
                    if (lowerName.endsWith("." + ext)) {
                        return type;
                    }
                }
            }
            return UNKNOWN;
        }
    }

    /**
     * 获取文件内容和类型
     *
     * @param fileUuid 文件UUID
     * @return 文件信息包含内容和类型
     * @throws FileProcessingException 当文件处理失败时
     */
    public FileContentInfo getFileContent(String fileUuid) {
        try {
            log.debug("开始处理文件: {}", fileUuid);

            // 先查询文件信息获取文件名
            FileInfoResponse fileInfo = fileClient.queryByUuid(fileUuid);
            if (fileInfo == null) {
                throw new FileProcessingException("文件信息不存在: " + fileUuid);
            }

            String fileName = fileInfo.getFileName();
            FileType fileType = FileType.fromFileName(fileName);

            log.debug("文件类型: {}, 文件名: {}", fileType.getCategory(), fileName);

            // 下载文件内容
            byte[] fileContent;
            try (InputStream fileStream = fileClient.downloadByUuid(fileUuid)) {
                if (fileStream == null) {
                    throw new FileProcessingException("文件内容不存在: " + fileUuid);
                }

                // 使用缓冲读取文件内容到字节数组，避免readAllBytes()的问题
                try {
                    fileContent = readAllBytes(fileStream);
                } catch (IOException e) {
                    log.error("读取文件内容失败: {}", fileUuid, e);
                    throw new FileProcessingException("读取文件内容失败: " + fileUuid, e);
                }
            } catch (IOException e) {
                log.error("下载文件失败: {}", fileUuid, e);
                throw new FileProcessingException("下载文件失败: " + fileUuid, e);
            }

            String extractedText = null;

            if (fileContent.length == 0) {
                log.warn("文件内容为空: {}", fileUuid);
            } else {
                switch (fileType) {
                    case DOCUMENT:
                        extractedText = extractTextFromDocument(fileContent, fileName);
                        break;
                    case PDF:
                        extractedText = extractTextFromPDF(fileContent);
                        break;
                    case IMAGE:
                        extractedText = extractTextFromImage(fileContent);
                        break;
                    default:
                        log.warn("不支持的文件类型: {}", fileName);
                        break;
                }
            }

            return new FileContentInfo(fileUuid, fileName, fileType.getCategory(),
                                     fileContent, extractedText);

        } catch (Exception e) {
            log.error("文件处理失败: {}", fileUuid, e);
            throw new FileProcessingException("文件处理失败: " + fileUuid, e);
        }
    }

    /**
     * 从文档中提取文本
     */
    private String extractTextFromDocument(byte[] content, String fileName) throws IOException {
        if (fileName.toLowerCase().endsWith(".txt")) {
            return new String(content, StandardCharsets.UTF_8);
        }

        if (fileName.toLowerCase().endsWith(".docx")) {
            try (XWPFDocument document = new XWPFDocument(new ByteArrayInputStream(content))) {
                StringBuilder text = new StringBuilder();
                List<XWPFParagraph> paragraphs = document.getParagraphs();
                for (XWPFParagraph paragraph : paragraphs) {
                    text.append(paragraph.getText()).append("\n");
                }
                return text.toString();
            }
        }

        // 对于.doc文件，需要其他库支持，这里先返回空
        log.warn("暂不支持.doc格式文件: {}", fileName);
        return "";
    }

    /**
     * 从PDF中提取文本
     */
    private String extractTextFromPDF(byte[] content) throws IOException {
        try (PDDocument document = Loader.loadPDF(content)) {
            PDFTextStripper textStripper = new PDFTextStripper();
            textStripper.setSortByPosition(true);
            return textStripper.getText(document);
        }
    }

    /**
     * 从PDF中提取前两页的图片
     *
     * @param content PDF文件内容
     * @return 前两页的图片字节数组列表
     * @throws IOException 当PDF处理失败时
     */
    public List<byte[]> extractImagesFromPDF(byte[] content) throws IOException {
        List<byte[]> images = new ArrayList<>();

        try (PDDocument document = Loader.loadPDF(content)) {
            PDFRenderer renderer = new PDFRenderer(document);

            // 提取前两页，如果PDF只有一页则只提取一页
            int maxPages = Math.min(2, document.getNumberOfPages());

            for (int pageIndex = 0; pageIndex < maxPages; pageIndex++) {
                // 将PDF页面渲染为图片，使用300 DPI以保证清晰度
                BufferedImage image = renderer.renderImageWithDPI(pageIndex, 300);

                // 将BufferedImage转换为字节数组
                try (ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
                    ImageIO.write(image, "PNG", baos);
                    images.add(baos.toByteArray());
                }

                log.debug("成功提取PDF第{}页的图片，大小: {} bytes",
                         pageIndex + 1, images.get(images.size() - 1).length);
            }
        }

        return images;
    }

    /**
     * 安全地读取InputStream的所有字节
     */
    private byte[] readAllBytes(InputStream inputStream) throws IOException {
        try (ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {
            byte[] buffer = new byte[4096];
            int bytesRead;
            while ((bytesRead = inputStream.read(buffer)) != -1) {
                outputStream.write(buffer, 0, bytesRead);
            }
            return outputStream.toByteArray();
        }
    }

    /**
     * 从图片中提取文本（这里先返回图片的Base64编码，实际应该调用OCR）
     */
    private String extractTextFromImage(byte[] content) throws IOException {
        try (ByteArrayInputStream bis = new ByteArrayInputStream(content)) {
            BufferedImage image = ImageIO.read(bis);
            if (image == null) {
                throw new FileProcessingException("无法解析图片内容");
            }

            // 这里应该调用OCR服务，暂时返回Base64编码
            return "IMAGE_CONTENT:" + Base64.getEncoder().encodeToString(content);
        }
    }

    /**
     * 文件内容信息类
     */
    public static class FileContentInfo {
        private final String fileUuid;
        private final String fileName;
        private final String fileType;
        private final byte[] content;
        private final String extractedText;

        public FileContentInfo(String fileUuid, String fileName, String fileType,
                             byte[] content, String extractedText) {
            this.fileUuid = fileUuid;
            this.fileName = fileName;
            this.fileType = fileType;
            this.content = content;
            this.extractedText = extractedText;
        }

        public String getFileUuid() {
            return fileUuid;
        }

        public String getFileName() {
            return fileName;
        }

        public String getFileType() {
            return fileType;
        }

        public byte[] getContent() {
            return content;
        }

        public String getExtractedText() {
            return extractedText;
        }
    }
}