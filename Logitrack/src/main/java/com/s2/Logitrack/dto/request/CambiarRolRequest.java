package com.s2.Logitrack.dto.request;

import jakarta.validation.constraints.NotBlank;

public record CambiarRolRequest(
        @NotBlank(message = "El rol es obligatorio")
        String rol
) {
}