package com.contract.management.infrastructure.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;

/**
 * 安全配置（测试阶段：放行全部请求）
 * 说明：
 * - 关闭 CSRF 以便于接口测试（尤其是 POST/PUT/DELETE）
 * - 放行所有请求，不要求认证
 * 后续若引入鉴权，再按需调整此配置
 */
@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            // 关闭 CSRF，便于接口联调测试
            .csrf(csrf -> csrf.disable())
            // 放行所有请求
            .authorizeHttpRequests(auth -> auth
                .anyRequest().permitAll()
            );

        return http.build();
    }
}