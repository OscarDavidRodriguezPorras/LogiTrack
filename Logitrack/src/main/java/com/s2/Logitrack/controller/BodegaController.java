package com.s2.Logitrack.controller;

import com.s2.Logitrack.dto.request.BodegaRequest;
import com.s2.Logitrack.dto.response.BodegaResponse;
import com.s2.Logitrack.service.BodegaService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.apache.coyote.BadRequestException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/bodegas")
@RequiredArgsConstructor
public class BodegaController {

    private final BodegaService bodegaService;

    @GetMapping
    public ResponseEntity<List<BodegaResponse>> listar() {
        return ResponseEntity.ok(bodegaService.listar());
    }

    @GetMapping("/{id}")
    public ResponseEntity<BodegaResponse> obtener(@PathVariable Long id) {
        return ResponseEntity.ok(bodegaService.obtener(id));
    }

    @PostMapping
    public ResponseEntity<BodegaResponse> crear(@Valid @RequestBody BodegaRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(bodegaService.crear(request));
    }

    @PutMapping("/{id}")
    public ResponseEntity<BodegaResponse> actualizar(@PathVariable Long id,
                                                     @Valid @RequestBody BodegaRequest request) throws BadRequestException {
        return ResponseEntity.ok(bodegaService.actualizar(id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) throws BadRequestException {
        bodegaService.eliminar(id);
        return ResponseEntity.noContent().build();
    }
}
