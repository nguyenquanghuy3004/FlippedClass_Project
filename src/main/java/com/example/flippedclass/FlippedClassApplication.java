package com.example.flippedclass;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.persistence.autoconfigure.EntityScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication(scanBasePackages = {
        "com.example.flippedclass",
        "entity", "controller", "repository", "service", "dto", "exception"
})
@EntityScan(basePackages = "entity")
@EnableJpaRepositories(basePackages = "repository")
public class FlippedClassApplication {

    public static void main(String[] args) {
        SpringApplication.run(FlippedClassApplication.class, args);
    }

}
