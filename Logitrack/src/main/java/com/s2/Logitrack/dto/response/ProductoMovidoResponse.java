package com.s2.Logitrack.dto.response;

public record ProductoMovidoResponse(
        Long productoId,
        String producto,
        Long totalMovido
) {
}
