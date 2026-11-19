package com.s2.Logitrack.model;

import com.s2.Logitrack.auditoria.AuditoriaListener;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.MappedSuperclass;
import jakarta.persistence.Transient;

import java.util.Map;

@MappedSuperclass
@EntityListeners(AuditoriaListener.class)
public abstract class AuditableEntity {


    @Transient
    private Map<String, Object> estadoAnterior;

    public abstract Long getId();


    public abstract Map<String, Object> valoresAuditables();

    public Map<String, Object> getEstadoAnterior() {
        return estadoAnterior;
    }

    public void setEstadoAnterior(Map<String, Object> estadoAnterior) {
        this.estadoAnterior = estadoAnterior;
    }
}
