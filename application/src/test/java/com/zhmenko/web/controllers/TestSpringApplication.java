package com.zhmenko.web.controllers;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.Profile;

@SpringBootApplication(scanBasePackages = "com.zhmenko.*")
@EntityScan(basePackages = "com.zhmenko.*")
@Profile("test")
public class TestSpringApplication {
    public static void main(String[] args) {
        SpringApplication.run(TestSpringApplication.class, args);
    }
}
