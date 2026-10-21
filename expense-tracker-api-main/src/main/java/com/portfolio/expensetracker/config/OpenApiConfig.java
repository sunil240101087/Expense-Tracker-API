package com.portfolio.expensetracker.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Configures OpenAPI metadata and registers the JWT bearer security scheme so
 * the Swagger UI exposes an "Authorize" button.
 */
@Configuration
public class OpenApiConfig {

    private static final String SCHEME_NAME = "bearerAuth";

    @Bean
    public OpenAPI expenseTrackerOpenApi() {
        return new OpenAPI()
                .info(new Info()
                        .title("Expense Tracker API")
                        .description("A secure, multi-user expense tracking REST API with JWT authentication, "
                                + "role-based access control, and spending analytics.")
                        .version("1.0.0")
                        .contact(new Contact().name("Portfolio Project"))
                        .license(new License().name("MIT").url("https://opensource.org/licenses/MIT")))
                .components(new Components().addSecuritySchemes(SCHEME_NAME,
                        new SecurityScheme()
                                .name(SCHEME_NAME)
                                .type(SecurityScheme.Type.HTTP)
                                .scheme("bearer")
                                .bearerFormat("JWT")));
    }
}
