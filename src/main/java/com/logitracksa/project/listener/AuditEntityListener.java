package com.logitracksa.project.listener;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.logitracksa.project.config.ApplicationContextHolder;
import com.logitracksa.project.model.entity.AuditableEntity;
import com.logitracksa.project.model.entity.Auditoria;
import com.logitracksa.project.model.enums.TipoOperacion;
import com.logitracksa.project.util.SecurityUtils;
import jakarta.persistence.PostLoad;
import jakarta.persistence.PostPersist;
import jakarta.persistence.PostRemove;
import jakarta.persistence.PostUpdate;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;

import java.lang.reflect.Method;
import java.util.List;

/**
 * Listener JPA que captura operaciones INSERT, UPDATE y DELETE sobre las
 * entidades que lo declaran con {@code @EntityListeners}.
 *
 * Funciona junto con {@link AuditHolder} y {@link AuditSynchronization}:
 * durante la transacción se acumulan los eventos en un ThreadLocal y se
 * persisten después del commit.
 */
public class AuditEntityListener {

    /**
     * Captura el estado de la entidad al cargarla para usarlo como
     * "valores anteriores" en un futuro UPDATE o DELETE.
     */
    @PostLoad
    public void postLoad(Object entity) {
        if (entity instanceof AuditableEntity auditable) {
            auditable.setAuditSnapshot(toJson(entity));
        }
    }

    @PostPersist
    public void postPersist(Object entity) {
        queue(entity, TipoOperacion.INSERT, null, toJson(entity));
        registerSynchronization();
    }

    @PostUpdate
    public void postUpdate(Object entity) {
        String oldValues = (entity instanceof AuditableEntity a) ? a.getAuditSnapshot() : null;
        queue(entity, TipoOperacion.UPDATE, oldValues, toJson(entity));
        registerSynchronization();
    }

    @PostRemove
    public void postRemove(Object entity) {
        String oldValues = (entity instanceof AuditableEntity a) ? a.getAuditSnapshot() : null;
        queue(entity, TipoOperacion.DELETE, oldValues, null);
        registerSynchronization();
    }

    private void queue(Object entity, TipoOperacion op, String oldValues, String newValues) {
        if (entity instanceof Auditoria) {
            return; // la auditoría no se audita a sí misma
        }
        AuditInfo info = new AuditInfo(
                entity.getClass().getSimpleName(),
                getEntityId(entity),
                op,
                SecurityUtils.getCurrentUsername(),
                oldValues,
                newValues
        );
        AuditHolder.add(info);
    }

    /**
     * Registra la sincronización de transacción una sola vez por transacción.
     */
    private void registerSynchronization() {
        if (!TransactionSynchronizationManager.isSynchronizationActive()) {
            return;
        }
        List<TransactionSynchronization> syncs = TransactionSynchronizationManager.getSynchronizations();
        boolean already = syncs.stream().anyMatch(AuditSynchronization.class::isInstance);
        if (!already) {
            TransactionSynchronizationManager.registerSynchronization(new AuditSynchronization());
        }
    }

    private String toJson(Object entity) {
        try {
            ObjectMapper mapper = ApplicationContextHolder.getBean("auditObjectMapper", ObjectMapper.class);
            if (mapper == null) {
                return null;
            }
            return mapper.writeValueAsString(entity);
        } catch (Exception e) {
            return null;
        }
    }

    private Long getEntityId(Object entity) {
        try {
            Method m = entity.getClass().getMethod("getId");
            Object result = m.invoke(entity);
            return result instanceof Number n ? n.longValue() : null;
        } catch (Exception e) {
            return null;
        }
    }
}
