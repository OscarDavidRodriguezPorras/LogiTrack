package com.s2.Logitrack.repository;

import com.s2.Logitrack.dto.response.ProductoMovidoResponse;
import com.s2.Logitrack.model.DetalleMovimiento;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.awt.print.Pageable;
import java.util.List;

public interface DetalleMovimientoRepository extends JpaRepository<DetalleMovimiento, Long> {

    // Reporte: productos más movidos (usar PageRequest.of(0, N) para limitar)
    @Query("""
            select new com.s2.Logitrack.dto.response.ProductoMovidoResponse(p.id, p.nombre, sum(d.cantidad))
            from DetalleMovimiento d join d.producto p
            group by p.id, p.nombre
            order by sum(d.cantidad) desc
            """)
    List<ProductoMovidoResponse> productosMasMovidos(Pageable pageable);
}