package com.s2.Logitrack.repository;

import com.s2.Logitrack.model.Auditoria;
import com.s2.Logitrack.model.enums.TipoOperacion;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AuditoriaRepository extends JpaRepository<Auditoria, Long> {

    List<Auditoria> findByUsuarioIgnoreCaseOrderByFechaHoraDesc(String usuario);

    List<Auditoria> findByOperacionOrderByFechaHoraDesc(TipoOperacion operacion);

    List<Auditoria> findAllByOrderByFechaHoraDesc();
}