package com.s2.Logitrack.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.util.LinkedHashMap;
import java.util.Map;

import java.math.BigDecimal;

@Entity
@Table(name = "productos")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Producto extends AuditableEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 100)
    private String nombre;

    @Column(nullable = false, length = 60)
    private String categoria;

    // Total general de la empresa (suma de inventario.cantidad)
    @Column(nullable = false)
    private Integer stock = 0;

    @Column(nullable = false, precision = 12, scale = 2)
    private BigDecimal precio;

    @Override
    public Map<String, Object> valoresAuditables() {
        Map<String, Object> v = new LinkedHashMap<>();
        v.put("nombre", nombre);
        v.put("categoria", categoria);
        v.put("stock", stock);
        v.put("precio", precio);
        return v;
    }
}