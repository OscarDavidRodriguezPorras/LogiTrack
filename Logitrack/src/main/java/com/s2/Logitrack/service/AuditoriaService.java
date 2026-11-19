package com.s2.Logitrack.service;

import com.s2.Logitrack.dto.response.AuditoriaResponse;
import com.s2.Logitrack.mapper.AuditoriaMapper;
import com.s2.Logitrack.model.Auditoria;
import com.s2.Logitrack.model.enums.TipoOperacion;
import com.s2.Logitrack.repository.AuditoriaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AuditoriaService {

    private final AuditoriaRepository auditoriaRepository;
    private final AuditoriaMapper auditoriaMapper;

    @Transactional(readOnly = true)
    public List<AuditoriaResponse> consultar(String usuario, TipoOperacion operacion) {
        boolean hayUsuario = usuario != null && !usuario.isBlank();
        List<Auditoria> resultado;

        if (hayUsuario) {
            resultado = auditoriaRepository.findByUsuarioIgnoreCaseOrderByFechaHoraDesc(usuario.trim());
            if (operacion != null) {
                resultado = resultado.stream()
                        .filter(a -> a.getOperacion() == operacion)
                        .toList();
            }
        } else if (operacion != null) {
            resultado = auditoriaRepository.findByOperacionOrderByFechaHoraDesc(operacion);
        } else {
            resultado = auditoriaRepository.findAllByOrderByFechaHoraDesc();
        }

        return resultado.stream().map(auditoriaMapper::toResponse).toList();
    }
}
