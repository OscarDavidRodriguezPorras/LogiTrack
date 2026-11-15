package com.s2.Logitrack.dto.response;

public record UsuarioResponse(
        Long id,
        String nombre,
        String username,
        String rol,
        Boolean activo
) {
}