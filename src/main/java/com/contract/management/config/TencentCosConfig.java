package com.contract.management.config;

import com.qcloud.cos.COSClient;
import com.qcloud.cos.ClientConfig;
import com.qcloud.cos.auth.BasicCOSCredentials;
import com.qcloud.cos.auth.COSCredentials;
import com.qcloud.cos.region.Region;
import com.qcloud.cos.transfer.TransferManager;
import jakarta.annotation.PreDestroy;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.StringUtils;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/**
 * 腾讯云COS配置类（包含COSClient + TransferManager 单例配置）
 */
@Configuration
@Slf4j
public class TencentCosConfig {

    // ========== 基础配置 ==========
    @Value("${file.download.cos.secret-id}")
    private String secretId;
    @Value("${file.download.cos.secret-key}")
    private String secretKey;
    @Value("${file.download.cos.region}")
    private String cosRegion;
    @Value("${file.download.cos.socket-timeout:30000}")
    private int socketTimeout;
    @Value("${file.download.cos.connection-timeout:30000}")
    private int connectionTimeout;

    // ========== COSClient 单例 Bean（基础） ==========
    @Bean(destroyMethod = "shutdown") // 单独销毁COSClient（若TransferManager不传true则生效）
    public COSClient cosClient() {
        // 校验参数（省略，同之前逻辑）
        if (StringUtils.isEmpty(secretId) || StringUtils.isEmpty(secretKey) || StringUtils.isEmpty(cosRegion)) {
            throw new IllegalArgumentException("COS配置参数不能为空");
        }
        COSCredentials credentials = new BasicCOSCredentials(secretId, secretKey);
        ClientConfig clientConfig = new ClientConfig();
        clientConfig.setRegion(new Region(cosRegion));
        clientConfig.setSocketTimeout(socketTimeout);
        clientConfig.setConnectionTimeout(connectionTimeout);
        return new COSClient(credentials, clientConfig);
    }

    // ========== TransferManager 单例 Bean（核心：自定义销毁） ==========
    /**
     * 注册TransferManager Bean，通过包装类处理带参数的销毁逻辑
     */
    @Bean
    public TransferManager transferManager(COSClient cosClient) {
        // 自定义线程池（推荐，避免默认线程池参数不合理）
        ExecutorService threadPool = Executors.newFixedThreadPool(16);
        // 创建TransferManager（关联COSClient和自定义线程池）
        TransferManager transferManager = new TransferManager(cosClient, threadPool);
        // 包装成带销毁逻辑的实例
        return new TransferManagerWrapper(transferManager).getTransferManager();
    }

    /**
     * TransferManager包装类：通过@PreDestroy实现带参数的销毁
     */
    @Slf4j
    static class TransferManagerWrapper {
        private final TransferManager transferManager;

        public TransferManagerWrapper(TransferManager transferManager) {
            this.transferManager = transferManager;
        }

        public TransferManager getTransferManager() {
            return transferManager;
        }

        /**
         * Spring容器销毁Bean时触发（@PreDestroy优先级高于destroyMethod）
         * shutdownNow(true)：关闭TransferManager并连带关闭内部的COSClient
         * 若需保留COSClient（其他地方复用），则传 false，依赖COSClient自身的destroyMethod="shutdown"
         */
        @PreDestroy
        public void shutdown() {
            if (transferManager != null) {
                transferManager.shutdownNow(false);
            }
        }
    }
}