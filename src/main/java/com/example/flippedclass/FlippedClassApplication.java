package com.example.flippedclass;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync; // Import thư viện
import org.springframework.boot.persistence.autoconfigure.EntityScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication
@EnableAsync
public class FlippedClassApplication {

    public static void main(String[] args) {
        SpringApplication.run(FlippedClassApplication.class, args);
    }

}
