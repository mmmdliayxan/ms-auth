package com.example.msauth.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class SwaggerConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        Server gatewayServer = new Server()
                .url("http://localhost:8080") // Gateway URL
                .description("Access through API Gateway");

        return new OpenAPI()
                .info(new Info()
                        .title("MS Auth API")
                        .version("1.0")
                        .description("Authentication service integrated with Gateway")
                        .license(new License().name("Apache 2.0").url("http://springdoc.org")))
                .servers(List.of(gatewayServer));
    }
}