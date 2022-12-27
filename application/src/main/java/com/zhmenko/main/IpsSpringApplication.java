package com.zhmenko.main;

import com.ulisesbocchio.jasyptspringboot.annotation.EnableEncryptableProperties;
import com.ulisesbocchio.jasyptspringboot.annotation.EncryptablePropertySource;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.ConfigurableApplicationContext;

@SpringBootApplication(scanBasePackages = {"com.zhmenko.*"})
// Jasypt
@EnableEncryptableProperties
@EncryptablePropertySource(name = "mainconf", value = "classpath:application.yml")
@EntityScan(basePackages = {"com.zhmenko.*"})
public class IpsSpringApplication {
    public static void main(String[] args) {
        ConfigurableApplicationContext run = SpringApplication.run(IpsSpringApplication.class, args);
    }
}
