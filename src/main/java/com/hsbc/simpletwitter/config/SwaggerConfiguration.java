package com.hsbc.simpletwitter.config;

import com.google.common.base.Predicates;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

import javax.inject.Inject;

@Slf4j
@Configuration
@EnableSwagger2
public class SwaggerConfiguration {

    private static final String APP_TITLE = "simple-twitter";
    private static final String APP_DESCRIPTION = "Docs for simple-twitter";
    private static final String APP_VERSION = "1.0";

    private final Boolean swaggerDocsEnabled;

    @Inject
    public SwaggerConfiguration(@Value("${swagger.enabled}") Boolean swaggerDocsEnabled) {
        this.swaggerDocsEnabled = swaggerDocsEnabled;
    }


    @Bean
    public Docket newsApi() {
        log.debug("SWAGGER CONFIG enabled: {}", swaggerDocsEnabled);
        return new Docket(DocumentationType.SWAGGER_2)
                .useDefaultResponseMessages(false)
                .apiInfo(apiInfo())
                .enable(swaggerDocsEnabled)
                .select()
                .apis(RequestHandlerSelectors.basePackage("com.hsbc.simpletwitter"))
                .apis(Predicates.not(RequestHandlerSelectors.basePackage("org.springframework.boot")))
                .paths(PathSelectors.any())
                .build()
                .pathMapping("/");
    }


    private ApiInfo apiInfo() {
        return new ApiInfoBuilder()
                .title(APP_TITLE)
                .description(APP_DESCRIPTION)
                .version(APP_VERSION)
                .build();
    }
}
