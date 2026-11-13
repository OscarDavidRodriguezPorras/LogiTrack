package com.s2.Logitrack.service;

import java.util.List;

public interface ProductoService {

    ProductoDtoResponse guarda(ProductoDtoRequest dto);

    ProductoDtoResponse actualizar(ProductoDtoRequest dto, Long id);

    void eliminar(Long id);

    List<ProductoDtoResponse> listar();

    ProductoDtoResponse buscarId(Long id);

    List<ProductoDtoResponse> buscarMarca(String marca);
}
