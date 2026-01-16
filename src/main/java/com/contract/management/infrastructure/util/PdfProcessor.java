package com.contract.management.infrastructure.util;

import com.contract.management.domain.exception.FileProcessingException;
import lombok.extern.slf4j.Slf4j;
import org.apache.pdfbox.Loader;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.rendering.ImageType;
import org.apache.pdfbox.rendering.PDFRenderer;
import org.springframework.stereotype.Component;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * PDF处理工具类
 * 提供PDF页数判断和转图片功能
 *
 * @author SaltyFish
 * @since 1.0.0
 */
@Slf4j
@Component
public class PdfProcessor {

    /**
     * 获取PDF文件的页数
     *
     * @param pdfBytes PDF文件字节数组
     * @return 页数
     * @throws FileProcessingException 文件处理异常
     */
    public int getPageCount(byte[] pdfBytes) throws FileProcessingException {
        try (PDDocument document = Loader.loadPDF(pdfBytes)) {

            int pageCount = document.getNumberOfPages();
            log.debug("PDF文件页数: {}", pageCount);
            return pageCount;

        } catch (IOException e) {
            log.error("获取PDF页数失败", e);
            throw new FileProcessingException("无法读取PDF文件页数: " + e.getMessage(), e);
        }
    }

    /**
     * 检查PDF页数是否超过限制
     *
     * @param pdfBytes PDF文件字节数组
     * @param maxPages 最大允许页数
     * @return 是否超过限制
     * @throws FileProcessingException 文件处理异常
     */
    public boolean isPageCountExceeded(byte[] pdfBytes, int maxPages) throws FileProcessingException {
        int pageCount = getPageCount(pdfBytes);
        return pageCount > maxPages;
    }

    /**
     * 将PDF转换为图片列表
     *
     * @param pdfBytes PDF文件字节数组
     * @param dpi 分辨率，默认300
     * @param imageFormat 图片格式，默认PNG
     * @return 图片字节数组列表
     * @throws FileProcessingException 文件处理异常
     */
    public List<byte[]> convertToImages(byte[] pdfBytes, int dpi, String imageFormat) throws FileProcessingException {
        List<byte[]> images = new ArrayList<>();

        try (PDDocument document = Loader.loadPDF(pdfBytes)) {

            PDFRenderer renderer = new PDFRenderer(document);
            int pageCount = document.getNumberOfPages();

            for (int pageIndex = 0; pageIndex < pageCount; pageIndex++) {
                BufferedImage image = renderer.renderImageWithDPI(pageIndex, dpi, ImageType.RGB);

                try (ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
                    ImageIO.write(image, imageFormat, baos);
                    images.add(baos.toByteArray());
                }

                log.debug("已转换第 {} 页为图片", pageIndex + 1);
            }

            log.info("PDF转换完成，共 {} 页，生成 {} 张图片", pageCount, images.size());
            return images;

        } catch (IOException e) {
            log.error("PDF转图片失败", e);
            throw new FileProcessingException("PDF转图片失败: " + e.getMessage(), e);
        }
    }

    /**
     * 将PDF转换为PNG图片列表（使用默认参数）
     *
     * @param pdfBytes PDF文件字节数组
     * @return 图片字节数组列表
     * @throws FileProcessingException 文件处理异常
     */
    public List<byte[]> convertToImages(byte[] pdfBytes) throws FileProcessingException {
        return convertToImages(pdfBytes, 300, "PNG");
    }

    /**
     * 将PDF的指定页转换为图片
     *
     * @param pdfBytes PDF文件字节数组
     * @param pageIndex 页码（从0开始）
     * @param dpi 分辨率，默认300
     * @param imageFormat 图片格式，默认PNG
     * @return 图片字节数组
     * @throws FileProcessingException 文件处理异常
     */
    public byte[] convertPageToImage(byte[] pdfBytes, int pageIndex, int dpi, String imageFormat)
            throws FileProcessingException {

        try (PDDocument document = Loader.loadPDF(pdfBytes)) {

            if (pageIndex < 0 || pageIndex >= document.getNumberOfPages()) {
                throw new FileProcessingException(
                    String.format("页码 %d 超出范围，文档总页数: %d", pageIndex, document.getNumberOfPages())
                );
            }

            PDFRenderer renderer = new PDFRenderer(document);
            BufferedImage image = renderer.renderImageWithDPI(pageIndex, dpi, ImageType.RGB);

            try (ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
                ImageIO.write(image, imageFormat, baos);
                byte[] imageBytes = baos.toByteArray();
                log.debug("已转换第 {} 页为图片，大小: {} bytes", pageIndex + 1, imageBytes.length);
                return imageBytes;
            }

        } catch (IOException e) {
            log.error("PDF第{}页转图片失败", pageIndex + 1, e);
            throw new FileProcessingException(
                String.format("PDF第%d页转图片失败: %s", pageIndex + 1, e.getMessage()), e
            );
        }
    }
}