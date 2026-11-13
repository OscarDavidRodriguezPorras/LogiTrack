package com.s2.Logitrack.controller;

import com.s2.Logitrack.service.ProductoService;
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

@Tag(name = "administrador de productos", description = "Esta api sirve para agregar, editar, eliminar y listar")
@RestController
@RequestMapping("api/producto")
@RequiredArgsConstructor
public class ProductoController {

    private final ProductoService productoService;

    //POST http://localhost:8080/api/producto
    @Operation(summary = "Permite agregar un producto",
            description = "Registra un nuevo producto en el inventario")
    @ApiResponses(
            value = {
                    @ApiResponse(responseCode = "201",
                            description = "El registro se realizo correctamente"),
                    @ApiResponse(responseCode = "400",
                            description = "Los datos enviados no son validos")
            }
    )
    @PostMapping
    public ResponseEntity<ProductoDtoResponse> guardar(@Valid @RequestBody ProductoDtoRequest dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(productoService.guarda(dto));
    }

    //GET http://localhost:8080/api/producto
    @Operation(summary = "Permite listar los productos",
            description = "Obtiene todos los productos registrados en el sistema")
    @ApiResponses(
            value = {
                    @ApiResponse(responseCode = "200",
                            description = "La consulta se realizo correctamente")
            }
    )
    @GetMapping
    public ResponseEntity<List<ProductoDtoResponse>> listar() {
        return ResponseEntity.ok(productoService.listar());
    }

    //GET http://localhost:8080/api/producto/2
    @Operation(summary = "Permite buscar un producto por id",
            description = "Obtiene un producto a partir de su id")
    @ApiResponses(
            value = {
                    @ApiResponse(responseCode = "200",
                            description = "La consulta se realizo correctamente"),
                    @ApiResponse(responseCode = "404",
                            description = "No se encontro el registro")
            }
    )
    @GetMapping("/{id}")
    public ResponseEntity<ProductoDtoResponse> buscar(@Schema(description = "Ingresa el id del producto", example = "1") @PathVariable Long id) {
        return ResponseEntity.ok(productoService.buscarId(id));
    }

    //GET http://localhost:8080/api/producto/filtroMarca?nombre=Samsung
    @Operation(summary = "Permite filtrar los productos por marca",
            description = "Obtiene todos los productos de la marca indicada")
    @ApiResponses(
            value = {
                    @ApiResponse(responseCode = "200",
                            description = "La consulta se realizo correctamente")
            }
    )
    @GetMapping("/filtroMarca")
    public ResponseEntity<List<ProductoDtoResponse>> buscarMarca(@Schema(description = "Ingresa la marca del producto", example = "Samsung") @RequestParam String nombre) {
        return ResponseEntity.ok(productoService.buscarMarca(nombre));
    }

    //PUT http://localhost:8080/api/producto/2
    @Operation(summary = "Permite actualizar un producto",
            description = "Modifica los datos de un producto existente")
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
    public ResponseEntity<ProductoDtoResponse> actualizar(@Valid @RequestBody ProductoDtoRequest dto, @Schema(description = "Ingresa el id del producto", example = "1") @PathVariable Long id) {
        return ResponseEntity.ok(productoService.actualizar(dto, id));
    }

    //DELETE http://localhost:8080/api/producto/2
    @Operation(summary = "Permite eliminar un producto",
            description = "Elimina un producto siempre que no pertenezca a un detalle de venta")
    @ApiResponses(
            value = {
                    @ApiResponse(responseCode = "204",
                            description = "El registro se elimino correctamente"),
                    @ApiResponse(responseCode = "404",
                            description = "No se encontro el registro"),
                    @ApiResponse(responseCode = "409",
                            description = "El producto pertenece a un detalle de venta")
            }
    )
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@Schema(description = "Ingresa el id del producto", example = "1") @PathVariable Long id) {
        productoService.eliminar(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}
