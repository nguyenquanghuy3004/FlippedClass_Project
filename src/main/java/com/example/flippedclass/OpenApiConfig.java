package com.example.flippedclass;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI flippedClassOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("FlippedClass API")
                        .description("REST API: Quiz (CRUD, thống kê) và Đánh giá / Tra cứu hồ sơ (Giảng viên)")
                        .version("1.0"));
    }
}
