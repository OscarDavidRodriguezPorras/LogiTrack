package com.s2.Logitrack.controller;

import com.s2.Logitrack.dto.response.ResumenGeneralResponse;
import com.s2.Logitrack.service.ReporteService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/reportes")
@RequiredArgsConstructor
public class ReporteController {

    private final ReporteService reporteService;

    @GetMapping("/resumen")
    public ResponseEntity<ResumenGeneralResponse> resumen() {
        return ResponseEntity.ok(reporteService.resumenGeneral());
    }
}
