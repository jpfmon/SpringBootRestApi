package com.montojo.restapi;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@OpenAPIDefinition(info = @Info(title = "Users API", version = "1.0", description = "Users API Challenge"))
public class RestApiApp {

	public static void main(String[] args) {
		SpringApplication.run(RestApiApp.class, args);
	}
}
