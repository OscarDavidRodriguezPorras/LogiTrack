package com.s2.Logitrack.dto.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record BodegaRequest(
        @NotBlank(message = "El nombre es obligatorio")
        @Size(max = 100, message = "El nombre no puede superar 100 caracteres")
        String nombre,

        @NotBlank(message = "La ubicación es obligatoria")
        @Size(max = 150, message = "La ubicación no puede superar 150 caracteres")
        String ubicacion,

        @NotNull(message = "La capacidad es obligatoria")
        @Min(value = 1, message = "La capacidad debe ser al menos 1")
        Integer capacidad,

        @NotBlank(message = "El encargado es obligatorio")
        @Size(max = 100, message = "El encargado no puede superar 100 caracteres")
        String encargado
) {
}