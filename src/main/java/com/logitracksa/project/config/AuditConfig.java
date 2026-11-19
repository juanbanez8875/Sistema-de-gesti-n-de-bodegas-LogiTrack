package com.logitracksa.project.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Configura el ObjectMapper usado para serializar entidades en los registros
 * de auditoría. Es independiente del ObjectMapper principal de la aplicación
 * para evitar problemas con proxies de Hibernate.
 */
@Configuration
public class AuditConfig {

    @Bean(name = "auditObjectMapper")
    public ObjectMapper auditObjectMapper() {
        ObjectMapper mapper = new ObjectMapper();
        mapper.configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false);
        mapper.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);
        mapper.registerModule(new JavaTimeModule());
        return mapper;
    }
}
