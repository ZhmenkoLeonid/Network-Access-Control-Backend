package com.zhmenko.main;

import com.ulisesbocchio.jasyptspringboot.annotation.EnableEncryptableProperties;
import com.ulisesbocchio.jasyptspringboot.annotation.EncryptablePropertySource;
//import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(scanBasePackages = {"com.zhmenko.*"})
// Jasypt
@EnableEncryptableProperties
@EncryptablePropertySource(name = "mainconf", value = "classpath:application.yml")
//
// Swagger
//@OpenAPIDefinition
//
public class IpsSpringApplication {
    public static void main(String[] args) {
        SpringApplication.run(IpsSpringApplication.class, args);
        //ApplicationContext context = new SpringApplicationBuilder(IpsSpringApplication.class).headless(false).run(args);
    }
}
