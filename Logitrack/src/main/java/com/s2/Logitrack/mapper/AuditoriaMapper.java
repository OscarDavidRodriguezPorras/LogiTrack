package com.s2.Logitrack.mapper;

import com.s2.Logitrack.dto.response.AuditoriaResponse;
import com.s2.Logitrack.model.Auditoria;
import org.springframework.stereotype.Component;

@Component
public class AuditoriaMapper {

    public AuditoriaResponse toResponse(Auditoria auditoria) {
        return new AuditoriaResponse(
                auditoria.getId(),
                auditoria.getOperacion(),
                auditoria.getFechaHora(),
                auditoria.getUsuario(),
                auditoria.getEntidad(),
                auditoria.getEntidadId(),
                auditoria.getValoresAnteriores(),
                auditoria.getValoresNuevos());
    }
}
