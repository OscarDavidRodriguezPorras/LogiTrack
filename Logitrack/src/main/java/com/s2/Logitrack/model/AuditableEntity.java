package com.s2.Logitrack.model;

import com.s2.Logitrack.auditoria.AuditoriaListener;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.MappedSuperclass;
import jakarta.persistence.Transient;

import java.util.Map;

// Las entidades que extienden esta clase se auditan automáticamente
// (INSERT / UPDATE / DELETE) gracias al listener de JPA.
@MappedSuperclass
@EntityListeners(AuditoriaListener.class)
public abstract class AuditableEntity {

    // Foto de los valores al cargar la entidad; sirve para saber qué cambió en un UPDATE
    @Transient
    private Map<String, Object> estadoAnterior;

    public abstract Long getId();

    // Valores que se guardan en la auditoría (NUNCA incluir contraseñas)
    public abstract Map<String, Object> valoresAuditables();

    public Map<String, Object> getEstadoAnterior() {
        return estadoAnterior;
    }

    public void setEstadoAnterior(Map<String, Object> estadoAnterior) {
        this.estadoAnterior = estadoAnterior;
    }
}}
