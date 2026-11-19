package com.s2.Logitrack.model;


import com.s2.Logitrack.model.enums.tipomovimiento;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.util.LinkedHashMap;
import java.util.Map;
import org.hibernate.Hibernate;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "movimientos")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Movimiento extends AuditableEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private LocalDateTime fecha;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private tipomovimiento tipo;

    // Empleado logueado que registra el movimiento
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "usuario_id", nullable = false)
    private Usuario usuario;

    // Null en ENTRADA
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "bodega_origen_id")
    private Bodega bodegaOrigen;

    // Null en SALIDA
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "bodega_destino_id")
    private Bodega bodegaDestino;

    @Column(length = 255)
    private String observacion;

    @OneToMany(mappedBy = "movimiento", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<DetalleMovimiento> detalles = new ArrayList<>();

    // Mantiene sincronizados los dos lados de la relación
    public void agregarDetalle(DetalleMovimiento detalle) {
        detalles.add(detalle);
        detalle.setMovimiento(this);
    }

    @PrePersist
    void antesDeGuardar() {
        if (fecha == null) {
            fecha = LocalDateTime.now();
        }
    }

    @Override
    public Map<String, Object> valoresAuditables() {
        Map<String, Object> v = new LinkedHashMap<>();
        v.put("tipo", tipo != null ? tipo.name() : null);
        v.put("usuarioId", usuario != null ? usuario.getId() : null);
        v.put("bodegaOrigenId", bodegaOrigen != null ? bodegaOrigen.getId() : null);
        v.put("bodegaDestinoId", bodegaDestino != null ? bodegaDestino.getId() : null);
        v.put("observacion", observacion);
        // Solo si la lista ya está cargada (evita consultas extra al leer movimientos)
        if (Hibernate.isInitialized(detalles)) {
            List<Map<String, Object>> lineas = new ArrayList<>();
            for (DetalleMovimiento d : detalles) {
                Map<String, Object> linea = new LinkedHashMap<>();
                linea.put("productoId", d.getProducto().getId());
                linea.put("cantidad", d.getCantidad());
                lineas.add(linea);
            }
            v.put("detalles", lineas);
        }
        return v;
    }
}