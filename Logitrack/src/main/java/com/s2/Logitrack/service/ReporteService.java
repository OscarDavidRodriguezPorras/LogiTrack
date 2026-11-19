package com.s2.Logitrack.service;

import com.s2.Logitrack.dto.response.ResumenGeneralResponse;
import com.s2.Logitrack.repository.DetalleMovimientoRepository;
import com.s2.Logitrack.repository.InventarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;



@Service
@RequiredArgsConstructor
public class ReporteService {

    private static final int TOP_PRODUCTOS = 5;

    private final InventarioRepository inventarioRepository;
    private final DetalleMovimientoRepository detalleMovimientoRepository;

    @Transactional(readOnly = true)
    public ResumenGeneralResponse resumenGeneral() {
        return new ResumenGeneralResponse(
                inventarioRepository.stockTotalPorBodega(),
                detalleMovimientoRepository.productosMasMovidos(PageRequest.of(0, TOP_PRODUCTOS)));
    }
}