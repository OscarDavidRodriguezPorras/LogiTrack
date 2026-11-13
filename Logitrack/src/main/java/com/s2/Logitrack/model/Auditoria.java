package com.s2.Logitrack.model;

import com.s2.Logitrack.model.enums.TipoOperacion;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.Audited;
import org.springframework.data.annotation.Id;

import java.time.LocalDateTime;

@Entity
@Table(name = "auditorias")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Auditoria {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 10)
    private TipoOperacion operacion;

    @Column(name = "fecha_hora", nullable = false)
    private LocalDateTime fechaHora;

    @Column(nullable = false, length = 50)
    private String usuario;

    @Column(nullable = false, length = 50)
    private String entidad;

    @Column(name = "entidad_id")
    private Long entidadId;

    @Column(name = "valores_anteriores", columnDefinition = "TEXT")
    private String valoresAnteriores;

    @Column(name = "valores_nuevos", columnDefinition = "TEXT")
    private String valoresNuevos;

    @PrePersist
    void antesDeGuardar() {
        if (fechaHora == null) {
            fechaHora = LocalDateTime.now();
        }
    }
}
