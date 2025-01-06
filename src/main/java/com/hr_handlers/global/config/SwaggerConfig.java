package com.hr_handlers.global.config;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

// 기본 정보 정의
@OpenAPIDefinition(
        info = @Info(
                title = "HR Handlers API Documentation",
                description = "이 API는 사원 및 HR 관련 작업을 관리하는 엔드포인트를 제공합니다.",
                version = "v1"
        )
)

@Configuration
public class SwaggerConfig {

    @Bean
    public OpenAPI openAPI() {
        String securityJwtName = "access";
        SecurityRequirement securityRequirement = new SecurityRequirement().addList(securityJwtName);
        // 인증 스키마 정의
        Components components = new Components()
                .addSecuritySchemes(securityJwtName, new SecurityScheme()
                        .name(securityJwtName)
                        .type(SecurityScheme.Type.APIKEY)
                        .in(SecurityScheme.In.HEADER));

        return new OpenAPI()
                .addSecurityItem(securityRequirement)
                .components(components);
    }
}