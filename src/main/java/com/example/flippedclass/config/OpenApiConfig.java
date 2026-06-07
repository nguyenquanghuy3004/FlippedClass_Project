package com.example.flippedclass.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                //  Định nghĩa thông tin tiêu đề và mô tả của trang tài liệu API
                .info(new Info()
                        .title("Flipped Classroom System APIs")
                        .version("1.0.0")
                        .description("Tài liệu đặc tả API và giao diện thử nghiệm cho hệ thống Flipped Classroom"))
                
                // Kích hoạt yêu cầu xác thực Bearer Token (JWT) cho toàn bộ hệ thống API
                .addSecurityItem(new SecurityRequirement().addList("BearerAuth"))
                
                        //       Khai báo nút "Authorize" (Chiếc khóa JWT) trên giao diện Swagger
                .components(new Components()
                        .addSecuritySchemes("BearerAuth", new SecurityScheme()
                                .name("BearerAuth")
                                .type(SecurityScheme.Type.HTTP)
                                .scheme("bearer")
                                .bearerFormat("JWT")));
    }
}
