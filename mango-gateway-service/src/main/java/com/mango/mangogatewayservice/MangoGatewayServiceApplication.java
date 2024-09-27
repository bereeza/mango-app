package com.mango.mangogatewayservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.r2dbc.repository.config.EnableR2dbcRepositories;

@SpringBootApplication
public class MangoGatewayServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(MangoGatewayServiceApplication.class, args);
    }
}
