package com.logitracksa.project.config;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

/**
 * Mantiene una referencia estática al ApplicationContext de Spring para que
 * clases no gestionadas por Spring (como el {@code AuditEntityListener} de JPA)
 * puedan acceder a los beans.
 */
@Component
public class ApplicationContextHolder implements ApplicationContextAware {

    private static ApplicationContext context;

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        context = applicationContext;
    }

    public static ApplicationContext getContext() {
        return context;
    }

    public static <T> T getBean(Class<T> type) {
        return context != null ? context.getBean(type) : null;
    }

    public static <T> T getBean(String name, Class<T> type) {
        return context != null ? context.getBean(name, type) : null;
    }
}
