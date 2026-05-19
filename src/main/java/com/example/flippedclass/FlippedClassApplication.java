package com.example.flippedclass;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync; // Import thư viện

@SpringBootApplication
@EnableAsync
public class FlippedClassApplication {

    public static void main(String[] args) {
        SpringApplication.run(FlippedClassApplication.class, args);
    }

}
