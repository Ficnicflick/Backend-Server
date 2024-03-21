package com.example.BackendServer.common.config;


import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;

import java.util.Arrays;

/*@OpenAPIDefinition(
        info = @Info(
                title = "창의학기제 API 명세서",
                description = "오류 혹은 설명이 부족한 점이 있다면 연락주세요.",
                version = "v1"
        )
)*/
@Configuration
public class SwaggerConfig {
    private static final String BEARER_TOKEN_PREFIX = "Bearer";

    @Value("${server.url}")
    private String SERVER_URL;

    @Bean
    // 운영 환경에는 Swagger를 비활성화하기 위해 추가했습니다.
    //@Profile("!Prod")
    public OpenAPI openAPI() {

        io.swagger.v3.oas.models.info.Info info = new io.swagger.v3.oas.models.info.Info()
        .title("Example API 문서") // 타이틀
        .description("잘못된 부분이나 오류 발생 시 바로 말씀해주세요.") // 문서 설명
        .contact(new Contact() // 연락처
                .name("김승진")
                .email("whffkaos007@naver.com"));

        SecurityRequirement securityRequirement = new SecurityRequirement().addList(HttpHeaders.AUTHORIZATION);
        Components components = new Components()
                .addSecuritySchemes(HttpHeaders.AUTHORIZATION, new SecurityScheme()
                        .name(HttpHeaders.AUTHORIZATION)
                        .type(SecurityScheme.Type.HTTP)
                        .scheme(BEARER_TOKEN_PREFIX)
                        .bearerFormat("JWT"));

        // Swagger UI 접속 후, 딱 한 번만 accessToken을 입력해주면 모든 API에 토큰 인증 작업이 적용됩니다.
        return new OpenAPI()
                .info(info)
                .addSecurityItem(securityRequirement)
                .components(components)
                .servers(Arrays.asList(
                        new Server().url(SERVER_URL),
                        new Server().url("http://localhost:8080")
                ));
    }
}
