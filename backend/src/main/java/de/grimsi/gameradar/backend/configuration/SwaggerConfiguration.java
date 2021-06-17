package de.grimsi.gameradar.backend.configuration;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.service.ApiKey;
import springfox.documentation.service.Contact;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;

import java.util.Collections;

@Configuration
public class SwaggerConfiguration {

    @Value("${application.version}")
    private String version;

    @Bean
    public Docket api() {
        return new Docket(DocumentationType.SWAGGER_2)
                .select()
                .apis(RequestHandlerSelectors.any())
                .paths(PathSelectors.ant("/v1/**"))
                .build()
                .apiInfo(apiInfo())
                .securitySchemes(Collections.singletonList(auth()));
    }

    private ApiInfo apiInfo() {
        return new ApiInfo(
                "LAN-Party Manager API",
                "Description of the HTTP-REST API from our LAN-Party Manager backend.",
                version,
                "",
                new Contact("grimsi", "https://www.grimsi.de", ""),
                "DBAD Public License",
                "https://dbad-license.org/",
                Collections.emptyList()
        );
    }

    private ApiKey auth() {
        return new ApiKey("auth", "Authorization", "header");
    }
}
