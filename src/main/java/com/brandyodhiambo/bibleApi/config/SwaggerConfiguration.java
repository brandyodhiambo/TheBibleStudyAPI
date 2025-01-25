package com.brandyodhiambo.bibleApi.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import org.springdoc.core.models.GroupedOpenApi;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerConfiguration {

    @Bean
    public GroupedOpenApi publicApi() {
        return GroupedOpenApi.builder()
                .group("base-service")
                .pathsToMatch("/**")
                .build();
    }

    @Bean
    public OpenAPI customOpenAPI() {
        Contact contact = new Contact()
                .name("brandyodhiambo")
                .url("https://github.com/brandyodhiambo");

        return new OpenAPI()
                .info(new Info()
                        .title("Spring Boot backend with RESTful APIs, session management")
                        .version("1.0")
                        .description("Spring Boot Kotlin, built with java and PostgreSQL database")
                        .termsOfService("http://swagger.io/terms/")
                        .license(new License().name("Apache 2.0").url("http://springdoc.org"))
                        .contact(contact)
                );
    }
}

