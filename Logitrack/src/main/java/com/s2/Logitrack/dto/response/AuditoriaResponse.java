package com.s2.Logitrack.dto.response;

import com.s2.Logitrack.model.enums.TipoOperacion;

import java.time.LocalDateTime;

public record AuditoriaResponse(
        Long id,
        TipoOperacion operacion,
        LocalDateTime fechaHora,
        String usuario,
        String entidad,
        Long entidadId,
        String valoresAnteriores,
        String valoresNuevos
) {
}
