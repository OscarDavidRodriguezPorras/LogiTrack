package com.s2.Logitrack.dto.response;

public record AuthResponse(
        String token,
        String tipo,
        String username,
        String rol
) {
}