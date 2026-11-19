package com.logitracksa.project.listener;

import java.util.ArrayList;
import java.util.List;

/**
 * Almacén temporal (ThreadLocal) de eventos de auditoría generados durante
 * una transacción. Se vacía después del commit en {@link AuditSynchronization}.
 */
public final class AuditHolder {

    private static final ThreadLocal<List<AuditInfo>> HOLDER =
            ThreadLocal.withInitial(ArrayList::new);

    private AuditHolder() {}

    public static void add(AuditInfo info) {
        HOLDER.get().add(info);
    }

    public static List<AuditInfo> drain() {
        List<AuditInfo> copy = new ArrayList<>(HOLDER.get());
        HOLDER.remove();
        return copy;
    }

    public static void clear() {
        HOLDER.remove();
    }
}
