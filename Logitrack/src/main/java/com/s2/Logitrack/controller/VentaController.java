package com.s2.Logitrack.controller;

import com.s2.Logitrack.service.VentaService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "administrador de la venta", description = "Esta api sirve para agregar, editar, eliminar y listar")
@RestController
@RequestMapping("api/venta")
@RequiredArgsConstructor
public class VentaController {

    private final VentaService ventaService;

    //POST http://localhost:8080/api/venta
    @Operation(summary = "Permite agregar una venta",
            description = "Registra una nueva venta; el total inicia en 0 y se calcula con sus detalles")
    @ApiResponses(
            value = {
                    @ApiResponse(responseCode = "201",
                            description = "El registro se realizo correctamente"),
                    @ApiResponse(responseCode = "400",
                            description = "Los datos enviados no son validos")
            }
    )
    @PostMapping
    public ResponseEntity<VentaDtoResponse> guardar(@Valid @RequestBody VentaDtoRequest dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(ventaService.guardar(dto));
    }

    //GET http://localhost:8080/api/venta
    @Operation(summary = "Permite listar las ventas",
            description = "Obtiene todas las ventas registradas en el sistema")
    @ApiResponses(
            value = {
                    @ApiResponse(responseCode = "200",
                            description = "La consulta se realizo correctamente")
            }
    )
    @GetMapping
    public ResponseEntity<List<VentaDtoResponse>> listar() {
        return ResponseEntity.ok(ventaService.listar());
    }

    //GET http://localhost:8080/api/venta/2
    @Operation(summary = "Permite buscar una venta por id",
            description = "Obtiene una venta a partir de su id")
    @ApiResponses(
            value = {
                    @ApiResponse(responseCode = "200",
                            description = "La consulta se realizo correctamente"),
                    @ApiResponse(responseCode = "404",
                            description = "No se encontro el registro")
            }
    )
    @GetMapping("/{id}")
    public ResponseEntity<VentaDtoResponse> buscar(@Schema(description = "Ingresa el id de la venta", example = "1") @PathVariable Long id) {
        return ResponseEntity.ok(ventaService.buscarId(id));
    }

    //PUT http://localhost:8080/api/venta/2
    @Operation(summary = "Permite actualizar una venta",
            description = "Modifica los datos del cliente de una venta existente")
    @ApiResponses(
            value = {
                    @ApiResponse(responseCode = "200",
                            description = "El registro se actualizo correctamente"),
                    @ApiResponse(responseCode = "400",
                            description = "Los datos enviados no son validos"),
                    @ApiResponse(responseCode = "404",
                            description = "No se encontro el registro")
            }
    )
    @PutMapping("/{id}")
    public ResponseEntity<VentaDtoResponse> actualizar(@Valid @RequestBody VentaDtoRequest dto, @Schema(description = "Ingresa el id de la venta", example = "1") @PathVariable Long id) {
        return ResponseEntity.ok(ventaService.actualizar(dto, id));
    }

    //DELETE http://localhost:8080/api/venta/2
    @Operation(summary = "Permite eliminar una venta",
            description = "Elimina una venta siempre que no tenga detalles de venta")
    @ApiResponses(
            value = {
                    @ApiResponse(responseCode = "204",
                            description = "El registro se elimino correctamente"),
                    @ApiResponse(responseCode = "404",
                            description = "No se encontro el registro"),
                    @ApiResponse(responseCode = "409",
                            description = "La venta esta ligada a un detalle de venta")
            }
    )
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@Schema(description = "Ingresa el id de la venta", example = "1") @PathVariable Long id) {
        ventaService.eliminar(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}
