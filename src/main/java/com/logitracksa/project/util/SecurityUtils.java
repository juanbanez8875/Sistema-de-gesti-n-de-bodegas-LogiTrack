package com.logitracksa.project.util;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

/**
 * Utilidades para obtener información del usuario autenticado actual.
 */
public final class SecurityUtils {

    private SecurityUtils() {}

    /**
     * Devuelve el username del usuario autenticado o "system" si no hay
     * una sesión activa (por ejemplo, en el registro o arranque).
     */
    public static String getCurrentUsername() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !auth.isAuthenticated()) {
            return "system";
        }
        String name = auth.getName();
        if ("anonymousUser".equals(name)) {
            return "system";
        }
        return name;
    }
}
