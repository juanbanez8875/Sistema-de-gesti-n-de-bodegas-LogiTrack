package com.logitracksa.project.exception;

/**
 * Regla de negocio violada (HTTP 400), por ejemplo: stock insuficiente.
 */
public class BusinessException extends RuntimeException {

    public BusinessException(String message) {
        super(message);
    }
}
