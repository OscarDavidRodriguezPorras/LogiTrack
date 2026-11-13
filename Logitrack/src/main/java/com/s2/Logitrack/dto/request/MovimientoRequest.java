package com.s2.Logitrack.dto.request;

import com.s2.Logitrack.model.enums.tipomovimiento;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.util.List;

public record MovimientoRequest(
        @NotNull(message = "El tipo de movimiento es obligatorio")
        tipomovimiento tipo,

        Long bodegaOrigenId,

        Long bodegaDestinoId,

        @Size(max = 255, message = "La observación no puede superar 255 caracteres")
        String observacion,

        @NotEmpty(message = "Debe incluir al menos un producto")
        List<@Valid DetalleMovimientoRequest> detalles
) {
}
