package com.s2.Logitrack.dto.response;

public record BodegaResponse(
        Long id,
        String nombre,
        String ubicacion,
        Integer capacidad,
        String encargado
) {
}