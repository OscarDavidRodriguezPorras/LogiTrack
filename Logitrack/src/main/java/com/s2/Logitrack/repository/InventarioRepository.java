package com.s2.Logitrack.repository;

import com.s2.Logitrack.dto.response.StockBodegaResponse;
import com.s2.Logitrack.model.Inventario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface InventarioRepository extends JpaRepository<Inventario, Long> {

    Optional<Inventario> findByBodegaIdAndProductoId(Long bodegaId, Long productoId);

    List<Inventario> findByBodegaId(Long bodegaId);

    @Query("""
            select new com.s2.logitrack.dto.response.StockBodegaResponse(b.id, b.nombre, coalesce(sum(i.cantidad), 0L))
            from Bodega b left join Inventario i on i.bodega = b
            group by b.id, b.nombre
            order by b.nombre
            """)
    List<StockBodegaResponse> stockTotalPorBodega();
}
