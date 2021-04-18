package com.github.estuaryoss.agent.configuration;

import com.github.estuaryoss.agent.component.About;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.service.Contact;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;

@Configuration
public class SwaggerConfig {
    @Autowired
    private About about;

    ApiInfo apiInfo() {
        return new ApiInfoBuilder()
                .title("estuary-agent")
                .description("Estuary Agent will run your shell commands via REST API")
                .license("Apache 2.0")
                .licenseUrl("http://www.apache.org/licenses/LICENSE-2.0.html")
                .termsOfServiceUrl("")
                .version(about.getVersion())
                .contact(new Contact("Catalin Dinuta", "https://github.com/dinuta", "constantin.dinuta@gmail.com"))
                .build();
    }

    @Bean
    public Docket customImplementation() {
        return new Docket(DocumentationType.SWAGGER_2)
                .select()
                .apis(RequestHandlerSelectors.basePackage("com.github.estuaryoss.agent.api"))
                .build()
                .apiInfo(apiInfo());
    }

}
