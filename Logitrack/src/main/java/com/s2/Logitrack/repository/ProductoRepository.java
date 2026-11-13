package com.s2.Logitrack.repository;

import com.s2.Logitrack.model.Producto;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ProductoRepository extends JpaRepository<Producto, Long> {


    List<Producto> findByStockLessThanOrderByStockAsc(Integer limite);
}
