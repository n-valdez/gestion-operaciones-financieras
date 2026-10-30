package com.entidadfinanciera.operaciones.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("API de Gestión de Operaciones Financieras y Procesos BPMN")
                        .version("1.0.0")
                        .description("Documentación oficial de los servicios REST integrados con Camunda y PostgreSQL para la entidad financiera."));
    }
}