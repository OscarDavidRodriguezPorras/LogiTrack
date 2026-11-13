package com.s2.Logitrack.mapper;

import com.s2.Logitrack.dto.response.DetalleMovimientoResponse;
import com.s2.Logitrack.dto.response.MovimientoResponse;
import com.s2.Logitrack.model.DetalleMovimiento;
import com.s2.Logitrack.model.Movimiento;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class MovimientoMapper {

    public MovimientoResponse toResponse(Movimiento movimiento) {
        List<DetalleMovimientoResponse> detalles = movimiento.getDetalles().stream()
                .map(this::toDetalleResponse)
                .toList();

        return new MovimientoResponse(
                movimiento.getId(),
                movimiento.getFecha(),
                movimiento.getTipo(),
                movimiento.getUsuario().getUsername(),
                movimiento.getBodegaOrigen() != null ? movimiento.getBodegaOrigen().getNombre() : null,
                movimiento.getBodegaDestino() != null ? movimiento.getBodegaDestino().getNombre() : null,
                movimiento.getObservacion(),
                detalles);
    }

    public DetalleMovimientoResponse toDetalleResponse(DetalleMovimiento detalle) {
        return new DetalleMovimientoResponse(
                detalle.getProducto().getId(),
                detalle.getProducto().getNombre(),
                detalle.getCantidad());
    }
}
