package com.s2.Logitrack.service;

import com.s2.Logitrack.dto.request.BodegaRequest;
import com.s2.Logitrack.dto.response.BodegaResponse;
import com.s2.Logitrack.exception.ProductoNoEncontradoException;
import com.s2.Logitrack.mapper.BodegaMapper;
import com.s2.Logitrack.model.Bodega;
import com.s2.Logitrack.model.Inventario;
import com.s2.Logitrack.repository.BodegaRepository;
import com.s2.Logitrack.repository.InventarioRepository;

import lombok.RequiredArgsConstructor;
import org.apache.coyote.BadRequestException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class BodegaService {

    private final BodegaRepository bodegaRepository;
    private final InventarioRepository inventarioRepository;
    private final BodegaMapper bodegaMapper;

    @Transactional(readOnly = true)
    public List<BodegaResponse> listar() {
        return bodegaRepository.findAll().stream()
                .map(bodegaMapper::toResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public BodegaResponse obtener(Long id) {
        return bodegaMapper.toResponse(buscar(id));
    }

    @Transactional
    public BodegaResponse crear(BodegaRequest request) {
        Bodega guardada = bodegaRepository.save(bodegaMapper.toEntity(request));
        return bodegaMapper.toResponse(guardada);
    }

    @Transactional
    public BodegaResponse actualizar(Long id, BodegaRequest request) throws BadRequestException {
        Bodega bodega = buscar(id);
        int ocupado = unidadesEnBodega(id);
        if (request.capacidad() < ocupado) {
            throw new BadRequestException("La nueva capacidad (" + request.capacidad()
                    + ") es menor a las unidades almacenadas (" + ocupado + ")");
        }

        bodegaMapper.actualizar(bodega, request);
        return bodegaMapper.toResponse(bodegaRepository.save(bodega));
    }

    @Transactional
    public void eliminar(Long id) throws BadRequestException {
        Bodega bodega = buscar(id);

        if (unidadesEnBodega(id) > 0) {
            throw new BadRequestException("No se puede eliminar la bodega: aún tiene productos almacenados");
        }
        bodegaRepository.delete(bodega);
    }

    private Bodega buscar(Long id) {
        return bodegaRepository.findById(id)
                .orElseThrow(() -> new ProductoNoEncontradoException("Bodega", id));
    }

    private int unidadesEnBodega(Long bodegaId) {
        return inventarioRepository.findByBodegaId(bodegaId).stream()
                .mapToInt(Inventario::getCantidad)
                .sum();
    }
}
