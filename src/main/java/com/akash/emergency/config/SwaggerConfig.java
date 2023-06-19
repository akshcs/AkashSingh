package com.akash.emergency.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.service.Contact;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

import java.util.Collections;

@Configuration
@EnableSwagger2
public class SwaggerConfig {
    @Bean
    public Docket getSwaggerConfig(){
        return new Docket(DocumentationType.SWAGGER_2)
                .select()
                .apis(RequestHandlerSelectors.basePackage("com.emergency"))
                .build()
                .apiInfo(metaInfo());
    }

    private ApiInfo metaInfo() {
        return new ApiInfo(
                "What3Words Assignment",
                "APIs for Emergency Service",
                "1.0",
                "Free to Use",
                new Contact("Akash Singh", "https://www.linkedin.com/in/akash-singh-507110151/", "akash2103.singh@gmail.com"),
                "Apache License Version 2.0",
                "https://www.apache.org/licesen.html",
                Collections.emptyList()
        );
    }
}
