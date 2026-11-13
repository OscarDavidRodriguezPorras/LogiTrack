package com.s2.Logitrack.service.impl;

import com.s2.Logitrack.service.VentaService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class VentaServiceImpl implements VentaService {

    private final VentaMapper ventaMapper;
    private final VentaRepository ventaRepository;
    private final DetalleVentaRepository detalleVentaRepository;
    private final ProductoRepository productoRepository;

    @Override
    public VentaDtoResponse guardar(VentaDtoRequest dto) {
        Venta venta = ventaMapper.dtoToEntity(dto);
        return ventaMapper.entityToDto(ventaRepository.save(venta));
    }

    @Override
    public VentaDtoResponse actualizar(VentaDtoRequest dto, Long id) {
        Venta venta = buscarEntidad(id);
        ventaMapper.updateEntityFromDto(venta, dto);
        return ventaMapper.entityToDto(ventaRepository.save(venta));
    }

    @Override
    public void eliminar(Long id) {
        Venta v = ventaRepository.findById(id).orElseThrow(()->new DatabaseRuleException("La venta a eliminar no existe"));
        if(detalleVentaRepository.existsByVentaId(v.getId()))
            throw new IllegalStateException("Esta venta esta ligada a un detalle de venta");
        ventaRepository.delete(v);

    }

    @Override
    @Transactional(readOnly = true)
    public List<VentaDtoResponse> listar() {
        return ventaRepository.findAll().stream().map(ventaMapper::entityToDto).toList();
    }

    @Override
    @Transactional(readOnly = true)
    public VentaDtoResponse buscarId(Long id) {
        return ventaMapper.entityToDto(buscarEntidad(id));
    }

    private Venta buscarEntidad(Long id) {
        return ventaRepository.findById(id)
                .orElseThrow(() -> new DatabaseRuleException("No se encontro la venta con id " + id));
    }
}
