package com.s2.Logitrack.repository;

import com.s2.Logitrack.model.Movimiento;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;

public interface MovimientoRepository extends JpaRepository<Movimiento, Long> {

    // Movimientos por rango de fechas (BETWEEN)
    @EntityGraph(attributePaths = {"usuario", "bodegaOrigen", "bodegaDestino", "detalles", "detalles.producto"})
    List<Movimiento> findByFechaBetweenOrderByFechaDesc(LocalDateTime desde, LocalDateTime hasta);

    @EntityGraph(attributePaths = {"usuario", "bodegaOrigen", "bodegaDestino", "detalles", "detalles.producto"})
    List<Movimiento> findAllByOrderByFechaDesc();
}
