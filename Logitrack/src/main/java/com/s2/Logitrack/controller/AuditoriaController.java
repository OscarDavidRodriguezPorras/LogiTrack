package com.s2.Logitrack.controller;

import com.s2.Logitrack.dto.response.AuditoriaResponse;
import com.s2.Logitrack.model.enums.TipoOperacion;
import com.s2.Logitrack.service.AuditoriaService;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Tag(name = "Auditorías", description = "Consulta del historial de cambios (solo ADMIN)")
@RestController
@RequestMapping("/auditorias")
@RequiredArgsConstructor
public class AuditoriaController {

    private final AuditoriaService auditoriaService;

    @GetMapping
    public ResponseEntity<List<AuditoriaResponse>> consultar(
            @RequestParam(name = "usuario", required = false) String usuario,
            @RequestParam(name = "operacion", required = false) TipoOperacion operacion) {
        return ResponseEntity.ok(auditoriaService.consultar(usuario, operacion));
    }
}