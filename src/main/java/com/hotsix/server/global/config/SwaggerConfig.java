package com.hotsix.server.global.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerConfig {

    @Bean
    public OpenAPI openAPI() {
        final String JWT_SCHEME_NAME = "JWT";

        return new OpenAPI()
                .addServersItem(new Server().url("/"))
                .info(new Info()
                        .title("HotSix API")
                        .description("핫식스 팀 프로젝트 API 명세서")
                        .version("v1.0.0"))
                .addSecurityItem(new SecurityRequirement().addList(JWT_SCHEME_NAME))
                .components(new Components()
                        .addSecuritySchemes(JWT_SCHEME_NAME,
                                new SecurityScheme()
                                        .name(JWT_SCHEME_NAME)
                                        .type(SecurityScheme.Type.HTTP)
                                        .scheme("bearer")
                                        .bearerFormat("JWT")));
    }
}
