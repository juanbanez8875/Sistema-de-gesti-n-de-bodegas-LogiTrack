package com.logitracksa.project.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Configuración de OpenAPI 3 / Swagger UI.
 * Define la información de la API y el esquema de seguridad JWT.
 */
@Configuration
public class OpenApiConfig {

    private static final String SECURITY_SCHEME = "Bearer Authentication";

    @Bean
    public OpenAPI logiTrackOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("LogiTrack API — Sistema de Gestión de Bodegas")
                        .description("""
                                API REST para la gestión de bodegas, productos, movimientos
                                de inventario y auditoría de cambios. Autenticación con JWT.
                                """)
                        .version("1.0.0")
                        .contact(new Contact()
                                .name("LogiTrack S.A.")
                                .email("contacto@logitrack.com"))
                        .license(new License()
                                .name("Apache 2.0")))
                .addSecurityItem(new SecurityRequirement().addList(SECURITY_SCHEME))
                .components(new Components()
                        .addSecuritySchemes(SECURITY_SCHEME,
                                new SecurityScheme()
                                        .type(SecurityScheme.Type.HTTP)
                                        .bearerFormat("JWT")
                                        .scheme("bearer")
                                        .in(SecurityScheme.In.HEADER)
                                        .name("Authorization")
                                        .description("Ingrese el token JWT obtenido en /auth/login")));
    }
}
