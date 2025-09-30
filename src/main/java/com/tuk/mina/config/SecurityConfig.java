package com.tuk.mina.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            .csrf(csrf -> csrf.disable()) // CSRF 보호 비활성화
            .authorizeHttpRequests(auth -> auth.anyRequest().permitAll()) // 모든 요청 허용
            .formLogin(form -> form.disable()) // 기본 로그인 화면 비활성화
            .httpBasic(httpBasic -> httpBasic.disable()) // HTTP 기본 인증 비활성화
            .exceptionHandling(exception -> exception.authenticationEntryPoint((request, response, authException) -> {
                response.sendError(403, "Forbidden"); // 인증되지 않은 요청에 대해 403 응답
            }));

        return http.build();
    }
}
