package com.contract.management;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.amqp.rabbit.annotation.EnableRabbit;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.transaction.annotation.EnableTransactionManagement;

/**
 * 合同管理模块启动类
 *
 * @author SaltyFish
 * @since 1.0.0
 */
@SpringBootApplication
@EnableCaching
@EnableRabbit
@EnableDiscoveryClient
@EnableAsync
@EnableTransactionManagement
@MapperScan("com.contract.management.infrastructure.mapper")
@ComponentScan(basePackages = {"com.contract.management", "com.contractreview.fileapi"})
@EnableFeignClients(
    basePackages = {"com.contract.common.feign", "com.ruoyi.feign.service"},
    defaultConfiguration = com.contract.common.config.FeignConfig.class
)
public class ContractManagementApplication {

    public static void main(String[] args) {
        SpringApplication.run(ContractManagementApplication.class, args);
    }
}