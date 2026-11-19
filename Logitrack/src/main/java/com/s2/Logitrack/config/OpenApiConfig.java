package com.s2.Logitrack.config;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.info.Contact;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import org.springframework.context.annotation.Configuration;

// Swagger UI: http://localhost:8080/swagger-ui/index.html
@Configuration
@OpenAPIDefinition(
        info = @Info(
                title = "LogiTrack API",
                version = "1.0",
                description = "Sistema de gestión y auditoría de bodegas: inventario, movimientos, "
                        + "auditoría de cambios y reportes. Autenticación con JWT y roles ADMIN / EMPLEADO.",
                contact = @Contact(name = "LogiTrack S.A.")),
        security = @SecurityRequirement(name = "bearerAuth"))
@SecurityScheme(
        name = "bearerAuth",
        type = SecuritySchemeType.HTTP,
        scheme = "bearer",
        bearerFormat = "JWT",
        description = "Token obtenido en POST /auth/login")
public class OpenApiConfig {
}