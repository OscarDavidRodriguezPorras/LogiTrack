package com.s2.Logitrack.controller;

import com.s2.Logitrack.service.DetalleVentaService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;
@Validated
@Tag(name = "administrador del detalle de la venta", description = "Esta api sirve para agregar, editar, eliminar y listar")
@RestController
@RequestMapping("/api/detalleventa")
@RequiredArgsConstructor
public class DetalleVentaController {

    private final DetalleVentaService detalleVentaService;

    //POST http://localhost:8080/api/detalleventa
    @Operation(summary = "Permite agregar los detalles",
            description = "Registra un detalle de venta, descuenta el stock y recalcula el total de la venta")
    @ApiResponses(
            value = {
                    @ApiResponse(responseCode = "201",
                            description = "El registro se realizo correctamente"),
                    @ApiResponse(responseCode = "400",
                            description = "Datos invalidos o stock insuficiente"),
                    @ApiResponse(responseCode = "404",
                            description = "No se encontro el registro")
            }
    )
    @PostMapping
    public ResponseEntity<DetalleVentaDtoResponse> guardar(@Valid @RequestBody DetalleVentaDtoRequest dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(detalleVentaService.guardar(dto));
    }

    //GET http://localhost:8080/api/detalleventa
    @Operation(summary = "Permite listar los detalles",
            description = "Obtiene todos los detalles de venta registrados en el sistema")
    @ApiResponses(
            value = {
                    @ApiResponse(responseCode = "200",
                            description = "La consulta se realizo correctamente")
            }
    )
    @GetMapping
    public ResponseEntity<List<DetalleVentaDtoResponse>> listar() {
        return ResponseEntity.ok(detalleVentaService.listar());
    }

    //GET http://localhost:8080/api/detalleventa/2
    @Operation(summary = "Permite buscar un detalle por id",
            description = "Obtiene un detalle de venta a partir de su id")
    @ApiResponses(
            value = {
                    @ApiResponse(responseCode = "200",
                            description = "La consulta se realizo correctamente"),
                    @ApiResponse(responseCode = "404",
                            description = "No se encontro el registro")
            }
    )
    @GetMapping("/{id}")
    public ResponseEntity<DetalleVentaDtoResponse> buscar(@Schema(description = "Ingresa el id del detalle de la venta", example = "1") @PathVariable Long id) {
        return ResponseEntity.ok(detalleVentaService.buscarId(id));
    }

    //GET http://localhost:8080/api/detalleventa/venta/2
    @Operation(summary = "Permite listar los detalles de una venta",
            description = "Obtiene todos los detalles que pertenecen a la venta indicada")
    @ApiResponses(
            value = {
                    @ApiResponse(responseCode = "200",
                            description = "La consulta se realizo correctamente"),
                    @ApiResponse(responseCode = "404",
                            description = "No se encontro el registro")
            }
    )
    @GetMapping("/venta/{id}")
    public ResponseEntity<List<DetalleVentaDtoResponse>> buscarVenta(@Schema(description = "Ingresa el id de la venta", example = "1") @PathVariable Long id) {
        return ResponseEntity.ok(detalleVentaService.buscarVentaId(id));
    }

    //GET http://localhost:8080/api/detalleventa/producto/1
    @Operation(summary = "Permite listar los detalles de un producto",
            description = "Obtiene todos los detalles en los que aparece el producto indicado")
    @ApiResponses(
            value = {
                    @ApiResponse(responseCode = "200",
                            description = "La consulta se realizo correctamente"),
                    @ApiResponse(responseCode = "404",
                            description = "No se encontro el registro")
            }
    )
    @GetMapping("/producto/{id}")
    public ResponseEntity<List<DetalleVentaDtoResponse>> buscarProducto(@Schema(description = "Ingresa el id del producto", example = "1") @PathVariable Long id) {
        return ResponseEntity.ok(detalleVentaService.buscarProductoId(id));
    }

    //GET http://localhost:8080/api/detalleventa/filtrarProductoSubtotal?id=1&subtotal=50000
    @Operation(summary = "Permite filtrar los detalles por producto y subtotal",
            description = "Obtiene los detalles del producto indicado cuyo subtotal es mayor o igual al valor enviado")
    @ApiResponses(
            value = {
                    @ApiResponse(responseCode = "200",
                            description = "La consulta se realizo correctamente")
            }
    )
    @GetMapping("/filtrarProductoSubtotal")
    public ResponseEntity<List<DetalleVentaDtoResponse>> buscarProductoSubtotal(
            @Schema(description = "Ingresa el id del producto", example = "1") @RequestParam Long id,
            @Schema(description = "Ingresa el subtotal minimo", example = "50000") @RequestParam BigDecimal subtotal) {
        return ResponseEntity.ok(detalleVentaService.filtrarProductoSubtotal(id, subtotal));
    }

    //PUT http://localhost:8080/api/detalleventa/2
    @Operation(summary = "Permite actualizar un detalle",
            description = "Modifica un detalle de venta, ajusta el stock y recalcula el total de la venta")
    @ApiResponses(
            value = {
                    @ApiResponse(responseCode = "200",
                            description = "El registro se actualizo correctamente"),
                    @ApiResponse(responseCode = "400",
                            description = "Datos invalidos o stock insuficiente"),
                    @ApiResponse(responseCode = "404",
                            description = "No se encontro el registro")
            }
    )
    @PutMapping("/{id}")
    public ResponseEntity<DetalleVentaDtoResponse> actualizar(@Valid @RequestBody DetalleVentaDtoRequest dto, @Schema(description = "Ingresa el id del detalle de la venta", example = "1") @PathVariable Long id) {
        return ResponseEntity.ok(detalleVentaService.actualizar(dto, id));
    }

    //DELETE http://localhost:8080/api/detalleventa/2
    @Operation(summary = "Permite eliminar un detalle",
            description = "Elimina un detalle de venta, devuelve el stock y recalcula el total de la venta")
    @ApiResponses(
            value = {
                    @ApiResponse(responseCode = "204",
                            description = "El registro se elimino correctamente"),
                    @ApiResponse(responseCode = "404",
                            description = "No se encontro el registro")
            }
    )
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@Schema(description = "Ingresa el id del detalle de la venta", example = "1") @PathVariable Long id) {
        detalleVentaService.eliminar(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}
