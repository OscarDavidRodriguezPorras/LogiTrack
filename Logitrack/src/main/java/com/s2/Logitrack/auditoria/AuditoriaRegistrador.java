package com.s2.Logitrack.auditoria;

import com.s2.Logitrack.model.Auditoria;
import com.s2.Logitrack.model.enums.TipoOperacion;
import com.s2.Logitrack.repository.AuditoriaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;


@Component
@RequiredArgsConstructor
public class AuditoriaRegistrador {

    private final AuditoriaRepository auditoriaRepository;

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void guardar(TipoOperacion operacion, String usuario, String entidad, Long entidadId,
                        String valoresAnteriores, String valoresNuevos) {
        Auditoria auditoria = new Auditoria();
        auditoria.setOperacion(operacion);
        auditoria.setUsuario(usuario);
        auditoria.setEntidad(entidad);
        auditoria.setEntidadId(entidadId);
        auditoria.setValoresAnteriores(valoresAnteriores);
        auditoria.setValoresNuevos(valoresNuevos);
        auditoriaRepository.save(auditoria);
    }
}