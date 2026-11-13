package com.s2.Logitrack.service;

import java.util.List;

public interface VentaService {
    VentaDtoResponse guardar(VentaDtoRequest dto);

    VentaDtoResponse actualizar(VentaDtoRequest dto, Long id);

    void eliminar(Long id);

    List<VentaDtoResponse> listar();

    VentaDtoResponse buscarId(Long id);

}
