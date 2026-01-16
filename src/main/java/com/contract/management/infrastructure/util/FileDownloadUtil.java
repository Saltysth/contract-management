package com.contract.management.infrastructure.util;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * 文件下载工具类
 * 用于从远程URL下载文件到本地存储
 *
 * @author SaltyFish
 * @since 1.0.0
 */
@Slf4j
@Component
public class FileDownloadUtil {

//    private final String TMP_STORAGE_DIR = "/srv/contract/tmpStorage";
    private final String TMP_STORAGE_DIR = "/Users/wangcong/Projects/SaltyFish/ContractReviewer/contract-management/tmpStorage";
    @Value("${file.download.local.image-base-url}")
    private String IMAGE_LOAD_BASE_URL;

    /**
     * 创建临时存储目录
     */
    private void ensureTmpStorageDir() {
        try {
            Path tmpDir = Paths.get(TMP_STORAGE_DIR);
            if (!Files.exists(tmpDir)) {
                Files.createDirectories(tmpDir);
                log.info("创建临时存储目录: {}", tmpDir.toAbsolutePath());
            }
        } catch (IOException e) {
            log.error("创建临时存储目录失败", e);
            throw new RuntimeException("无法创建临时存储目录: " + e.getMessage(), e);
        }
    }

    /**
     * 获取本地文件的访问URL
     *
     * @param fileName 文件名
     * @return 可访问的URL
     */
    public String getLocalFileUrl(String fileName) {
        return IMAGE_LOAD_BASE_URL + fileName;
    }


    /**
     * 获取临时存储目录路径
     *
     * @return 目录路径
     */
    public String getTmpStorageDir() {
        ensureTmpStorageDir();
        return Paths.get(TMP_STORAGE_DIR).toAbsolutePath().toString();
    }
}