package com.s2.Logitrack.dto.response;

import java.math.BigDecimal;

public record ProductoResponse(
        Long id,
        String nombre,
        String categoria,
        Integer stock,
        BigDecimal precio
) {
}
