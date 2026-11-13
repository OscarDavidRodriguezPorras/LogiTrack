package com.s2.Logitrack.service;

import java.math.BigDecimal;
import java.util.List;

public interface DetalleVentaService {

    DetalleVentaDtoResponse guardar(DetalleVentaDtoRequest dto);

    DetalleVentaDtoResponse actualizar(DetalleVentaDtoRequest dto, Long id);

    void eliminar(Long id);

    List<DetalleVentaDtoResponse> listar();

    DetalleVentaDtoResponse buscarId(Long id);

    List<DetalleVentaDtoResponse> buscarVentaId(Long id);

    List<DetalleVentaDtoResponse> buscarProductoId(Long id);

    List<DetalleVentaDtoResponse> filtrarProductoSubtotal(Long id, BigDecimal subtotal);
}
