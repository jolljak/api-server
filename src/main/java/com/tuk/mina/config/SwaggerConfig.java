package com.tuk.mina.config;

import java.security.Security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;

@Configuration
public class SwaggerConfig {
    
    @Bean
    public OpenAPI openAPI() {
        String jwt = "jwt";
        SecurityRequirement securityRequirement = new SecurityRequirement().addList(jwt);
        Components components = new Components()
            .addSecuritySchemes(jwt, new SecurityScheme()
                .name(jwt)
                .type(SecurityScheme.Type.HTTP)
                .scheme("bearer")
            );
        
        return new OpenAPI()
            .components(new Components())
            .info(apiInfo())
            .addSecurityItem(securityRequirement)
            .components(components);
    }

    private Info apiInfo() {
        return new Info()
            .title("MINA API DOCS")
            .description("MINA API 명세서")
            .version("v1.0");
    }
}
