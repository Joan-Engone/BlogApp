package org.studyeasy.SpringRestdemo.config;

import org.springframework.context.annotation.Configuration;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Contact;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.info.License;

@Configuration
@OpenAPIDefinition(
    info = @Info(
        title = "User API",
        version = "Versions 1.0.0",
        contact = @Contact(
            name = "StudyEasy", email = "engonej2@gmail.com", url = "https://studyeasy.org"
        ),
        license = @License(
            name = "Apache 2.0", url = "https://www.apache.org/licenses/LICENCE-2.0"
        ),
        termsOfService = "https://studyeasy.org/TOS",
        description = "Spring Boot RestFul API Demo by Joan"
    )
)
public class SwaggerConfig {
    
}
