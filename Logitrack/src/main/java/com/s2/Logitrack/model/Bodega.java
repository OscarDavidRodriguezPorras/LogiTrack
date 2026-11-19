package com.s2.Logitrack.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.util.LinkedHashMap;
import java.util.Map;

@Entity
@Table(name = "bodegas")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Bodega extends AuditableEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 100)
    private String nombre;

    @Column(nullable = false, length = 150)
    private String ubicacion;

    @Column(nullable = false)
    private Integer capacidad;

    @Column(nullable = false, length = 100)
    private String encargado;

    @Override
    public Map<String, Object> valoresAuditables() {
        Map<String, Object> v = new LinkedHashMap<>();
        v.put("nombre", nombre);
        v.put("ubicacion", ubicacion);
        v.put("capacidad", capacidad);
        v.put("encargado", encargado);
        return v;
    }
}
