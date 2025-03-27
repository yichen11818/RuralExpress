package com.ruralexpress.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

/**
 * 应用全局配置类
 */
@Configuration
public class AppConfig {

    /**
     * 配置RestTemplate Bean
     * @return RestTemplate实例
     */
    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }
} 