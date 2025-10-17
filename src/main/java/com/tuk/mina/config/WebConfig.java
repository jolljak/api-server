package com.tuk.mina.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**") // 1. 모든 경로에 대해 CORS 정책 적용
                .allowedOrigins("http://localhost:3000", "http://127.0.0.1:3000") // 2. 허용할 출처(Origin) 지정
                .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS") // 3. 허용할 HTTP 메서드 지정
                .allowedHeaders("*");// 4. 허용할 모든 HTTP 헤더 지정
    }
}