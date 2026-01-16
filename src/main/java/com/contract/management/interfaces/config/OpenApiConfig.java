package com.contract.management.interfaces.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

/**
 * OpenAPI配置类
 * 配置Swagger文档的基本信息
 */
@Configuration
public class OpenApiConfig {

    @Value("${server.port:11000}")
    private String serverPort;

    @Bean
    public OpenAPI contractManagementOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Contract Management API")
                        .description("合同管理系统 RESTful API 文档")
                        .version("1.0.0")
                        .contact(new Contact()
                                .name("Contract Management Team")
                                .email("1432488520@qq.com"))
                        .license(new License()
                                .name("Apache 2.0")
                                .url("https://www.apache.org/licenses/LICENSE-2.0")))
                .servers(List.of(
                        new Server()
                                .url("http://localhost:" + serverPort + "/contract-management")
                                .description("开发环境服务器")
                ));
    }
}