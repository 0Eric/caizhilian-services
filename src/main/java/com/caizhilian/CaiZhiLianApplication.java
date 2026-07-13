package com.caizhilian;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

@SpringBootApplication
@EnableConfigurationProperties
public class CaiZhiLianApplication {
    public static void main(String[] args) {
        SpringApplication.run(CaiZhiLianApplication.class, args);
    }
}
