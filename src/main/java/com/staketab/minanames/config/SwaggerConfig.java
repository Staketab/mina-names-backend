package com.staketab.minanames.config;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Contact;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.servers.Server;
import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import static com.staketab.minanames.utils.Constants.API_KEY_HEADER;
import static io.swagger.v3.oas.models.security.SecurityScheme.In.HEADER;

@Configuration
@OpenAPIDefinition(info = @Info(title = "Mina Names API",
        version = "1.0.0",
        contact = @Contact(
                name = "Staketab tech support", email = "support@staketab.com", url = "https://staketab.com/"
        )
),
        servers = @Server(
                url = "${api.server.url}",
                description = "Production"
        ))
public class SwaggerConfig {
        @Bean
        public OpenAPI customizeOpenAPI() {
                final String securitySchemeName = API_KEY_HEADER;
                return new OpenAPI()
                        .addSecurityItem(new SecurityRequirement()
                                .addList(securitySchemeName))
                        .components(new Components()
                                .addSecuritySchemes(securitySchemeName, new SecurityScheme()
                                        .name(securitySchemeName)
                                        .type(SecurityScheme.Type.APIKEY)
                                        .in(HEADER)));
        }
}
