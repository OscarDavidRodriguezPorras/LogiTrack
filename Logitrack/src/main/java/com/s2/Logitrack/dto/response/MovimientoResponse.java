package com.s2.Logitrack.dto.response;

import com.s2.Logitrack.model.enums.tipomovimiento;

import java.time.LocalDateTime;
import java.util.List;

public record MovimientoResponse(
        Long id,
        LocalDateTime fecha,
        tipomovimiento tipo,
        String usuario,
        String bodegaOrigen,
        String bodegaDestino,
        String observacion,
        List<DetalleMovimientoResponse> detalles
) {
}
