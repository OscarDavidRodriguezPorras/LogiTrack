package com.s2.Logitrack.dto.response;

public record DetalleMovimientoResponse(
        Long productoId,
        String producto,
        Integer cantidad
) {
}
