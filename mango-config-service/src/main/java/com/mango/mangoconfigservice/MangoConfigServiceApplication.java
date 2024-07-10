package com.mango.mangoconfigservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.config.server.EnableConfigServer;

@SpringBootApplication
@EnableConfigServer
public class MangoConfigServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(MangoConfigServiceApplication.class, args);
	}

}
