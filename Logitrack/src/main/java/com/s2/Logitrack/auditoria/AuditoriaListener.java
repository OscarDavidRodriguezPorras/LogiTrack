package com.s2.Logitrack.auditoria;

import com.s2.Logitrack.model.AuditableEntity;
import com.s2.Logitrack.model.enums.TipoOperacion;
import jakarta.persistence.PostLoad;
import jakarta.persistence.PostPersist;
import jakarta.persistence.PostRemove;
import jakarta.persistence.PostUpdate;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;

import java.math.BigDecimal;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;


@Slf4j
public class AuditoriaListener {

    @PostLoad
    public void alCargar(Object entidad) {
        if (entidad instanceof AuditableEntity auditable) {
            auditable.setEstadoAnterior(auditable.valoresAuditables());
        }
    }

    @PostPersist
    public void despuesDeInsertar(Object entidad) {
        if (entidad instanceof AuditableEntity auditable) {
            Map<String, Object> nuevos = auditable.valoresAuditables();
            registrar(TipoOperacion.INSERT, auditable, null, nuevos);
            auditable.setEstadoAnterior(nuevos);
        }
    }

    @PostUpdate
    public void despuesDeActualizar(Object entidad) {
        if (!(entidad instanceof AuditableEntity auditable)) {
            return;
        }
        Map<String, Object> actuales = auditable.valoresAuditables();
        Map<String, Object> previos = auditable.getEstadoAnterior();


        Map<String, Object> anteriores = new LinkedHashMap<>();
        Map<String, Object> nuevos = new LinkedHashMap<>();
        for (Map.Entry<String, Object> campo : actuales.entrySet()) {
            Object antes = previos != null ? previos.get(campo.getKey()) : null;
            if (!iguales(antes, campo.getValue())) {
                anteriores.put(campo.getKey(), antes);
                nuevos.put(campo.getKey(), campo.getValue());
            }
        }

        if (!nuevos.isEmpty()) {
            registrar(TipoOperacion.UPDATE, auditable, anteriores, nuevos);
        }
        auditable.setEstadoAnterior(actuales);
    }

    @PostRemove
    public void despuesDeEliminar(Object entidad) {
        if (entidad instanceof AuditableEntity auditable) {
            registrar(TipoOperacion.DELETE, auditable, auditable.valoresAuditables(), null);
        }
    }



    private void registrar(TipoOperacion operacion, AuditableEntity entidad,
                           Map<String, Object> anteriores, Map<String, Object> nuevos) {
        String usuario = usuarioActual();
        String nombreEntidad = entidad.getClass().getSimpleName();
        Long entidadId = entidad.getId();
        String jsonAnteriores = JsonMapa.aJson(anteriores);
        String jsonNuevos = JsonMapa.aJson(nuevos);

        Runnable guardar = () -> {
            try {
                ApplicationContextProvider.getBean(AuditoriaRegistrador.class)
                        .guardar(operacion, usuario, nombreEntidad, entidadId, jsonAnteriores, jsonNuevos);
            } catch (Exception e) {
                // Un fallo al auditar no debe romper la operación del usuario
                log.error("No se pudo guardar la auditoría de {} {}", nombreEntidad, entidadId, e);
            }
        };

        if (TransactionSynchronizationManager.isSynchronizationActive()) {
            TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronization() {
                @Override
                public void afterCommit() {
                    guardar.run();
                }
            });
        } else {
            guardar.run();
        }
    }

    private String usuarioActual() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !auth.isAuthenticated() || auth instanceof AnonymousAuthenticationToken) {
            return "ANONIMO";
        }
        return auth.getName();
    }

    private boolean iguales(Object a, Object b) {
        if (a instanceof BigDecimal x && b instanceof BigDecimal y) {
            return x.compareTo(y) == 0;
        }
        return Objects.equals(a, b);
    }
}