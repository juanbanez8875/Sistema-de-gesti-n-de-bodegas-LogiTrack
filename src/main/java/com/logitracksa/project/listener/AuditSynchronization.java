package com.logitracksa.project.listener;

import com.logitracksa.project.config.ApplicationContextHolder;
import com.logitracksa.project.service.AuditoriaService;
import org.springframework.transaction.support.TransactionSynchronization;

import java.util.List;

/**
 * Se ejecuta después del commit de la transacción principal. Toma los eventos
 * acumulados en {@link AuditHolder} y los persiste en una nueva transacción
 * independiente, de modo que las auditorías solo se guardan si la operación
 * original tuvo éxito.
 */
public class AuditSynchronization implements TransactionSynchronization {

    @Override
    public void afterCommit() {
        List<AuditInfo> audits = AuditHolder.drain();
        if (audits.isEmpty()) {
            return;
        }
        AuditoriaService service = ApplicationContextHolder.getBean(AuditoriaService.class);
        if (service == null) {
            return;
        }
        for (AuditInfo info : audits) {
            try {
                service.registrar(info);
            } catch (Exception e) {
                // La auditoría no debe romper la operación principal
                System.err.println("[AUDIT] Error al registrar auditoría: " + e.getMessage());
            }
        }
    }

    @Override
    public void afterCompletion(int status) {
        AuditHolder.clear();
    }
}
